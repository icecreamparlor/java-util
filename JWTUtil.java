import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reedride.crypto.cipher.RBase64;
import reedride.exception.RException;
import reedride.web.auth.JWTControl;
import wisely.web.member.constant.JWTConstant;
import wisely.web.member.constant.WhaleConstant;
import wisely.web.member.dto.UserInfoDTO;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Component
public class JWTUtil {

    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);

    @Value("${jwt.rsa256.private.key}")
    private String PRIVATE_KEY;

    @Value("${jwt.rsa256.public.key}")
    private String PUBLIC_KEY;

    private RSAPrivateKey RSA_PRIVATE_KEY = null;
    private RSAPublicKey  RSA_PUBLIC_KEY = null;

    private static final long ONE_DAY= 24*60*60*1000;

    @PostConstruct
    void init() {
        PRIVATE_KEY = PRIVATE_KEY.replace("-----BEGIN PRIVATE KEY-----", "");
        PRIVATE_KEY = PRIVATE_KEY.replace("-----END PRIVATE KEY-----", "");
        PRIVATE_KEY = PRIVATE_KEY.replaceAll("\\n", "");

        try {

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(RBase64.decode(PRIVATE_KEY));
            KeyFactory rsaFact = KeyFactory.getInstance(JWTConstant.RSA);
            RSA_PRIVATE_KEY = (RSAPrivateKey) rsaFact.generatePrivate(keySpec);

            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) RSA_PRIVATE_KEY;
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
            RSA_PUBLIC_KEY = (RSAPublicKey) KeyFactory.getInstance(JWTConstant.RSA).generatePublic(publicKeySpec);

        } catch (NoSuchAlgorithmException | RException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param userInfo
     * @return String
     *
     * 1. UserInfo 를 바탕으로, JWT 의 Claim 을 작성한다.
     * 2. 다른 메소드들을 통해 JWT 를 생성한다.
     * 3. "Bearer ${token}" 의 형태로 JWT Token 을 생성한다.
     *
     */
    public String generateJwtToken(UserInfoDTO userInfo) {

        JwtBuilder builder = Jwts.builder()
                                .setSubject(userInfo.getEmail())
                                .setHeader(createHeader())
                                .setClaims(createClaims(userInfo))
                                .setExpiration(createExpireDateFor24Week())
                                .signWith(SignatureAlgorithm.RS256, RSA_PRIVATE_KEY);

        return JWTConstant.JWT_PREFIX + builder.compact();
    }

    /**
     *
     * @param token
     * @return boolean
     *
     * 1. JWT token 을 분석하여, 유효한 토큰인지 parse 한다.
     * 2. JWT 에 Email 필드가 있는지, UNID 값이 0 이상인지 체크한다.
     * 3. 유효한 값이면 true, 유효하지 않으면 false 를 반환한다.
     *
     */
    public boolean isValidToken(String token) {

        /*
            1. Email 토큰 값이 있는가 ?
            2. Expiration Date 가 지났는가 ?
         */

        try {

            Claims claims = getClaimsFromToken(token);

            if((claims.get(JWTConstant.Email, String.class) == null)) {
                return false;
            }

            if((claims.get(JWTConstant.UNID, Integer.class) <= 0)) {
                return false;
            }

            return true;

        } catch (ExpiredJwtException e) {
            logger.error("Token Expired", e);
            return false;
        } catch (JwtException e) {
            logger.error("Token Tampered", e);
            return false;
        } catch (NullPointerException e) {
            logger.error("Token is null", e);
            return false;
        }
    }

    /**
     *
     * @param request
     * @return
     *
     * 1. Header 에 Bearer JWT 토큰이 있는지 확인한다.
     * 2. JWT Token 이 없다면, Cookie 값들을 뒤져서 JWT Token 값을 확인한다.
     * 3. 위에서 확인한 JWT Token 을 반환한다.
     *
     */
    public String getTokenFromRequest(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        String token = "";

        token = request.getHeader(JWTConstant.JWT_HEADER);

        if(StringUtil.isEmpty(token) && cookies != null) {
            for(Cookie cookie : cookies) {
                if(WhaleConstant.WHALE_AUTHKEY.equals(cookie.getName())
                    && !StringUtil.isEmpty(cookie.getValue())) {
                    token = cookie.getValue();
                }
            }
        }

        return StringUtil.isEmpty(token) ? "" : token.replace(JWTConstant.JWT_PREFIX, "");

    }


    private Date createExpireDateFor24Week() {
        // 토큰 만료시간은 24주로 설정
        Calendar c= Calendar.getInstance();
        c.add(Calendar.WEEK_OF_YEAR, 24);
//        c.add(Calendar.DATE, 30);
        return c.getTime();
    }

    /**
     * @param
     * @return
     *
     * 1. RS256 알고리즘, regDate 를 현재 시점으로 JWT Header 를 생성한다.
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", "JWT");
        header.put("alg", "RS256");
        header.put("regDate", System.currentTimeMillis());

        return header;
    }

    /**
     *
     * @param userInfo
     * @return JWT claims
     *
     * userInfo 의 Property 들을 바탕으로,
     * JWT Claim 에 값들을 넣고 반환한다.
     */
    private Map<String, Object> createClaims(UserInfoDTO userInfo) {
        // 비공개 클레임으로 사용자의 이름과 이메일을 설정, 세션 처럼 정보를 넣고 빼서 쓸 수 있다.
        Map<String, Object> claims = new HashMap<>();

        claims.put(JWTConstant.UNID, userInfo.getUnid());
        claims.put(JWTConstant.Email, userInfo.getEmail());
        claims.put(JWTConstant.UserIP, userInfo.getUserIp());
        claims.put(JWTConstant.FullName, userInfo.getFullName());
        claims.put(JWTConstant.DOB, userInfo.getDob());
        claims.put(JWTConstant.Phone, userInfo.getPhone());
        claims.put(JWTConstant.AgreeNotifications, userInfo.getAgreeNotifications());
        claims.put(JWTConstant.PayingCustomer, userInfo.getPayingCustomer());
        claims.put(JWTConstant.SubscriptionStatus, userInfo.getSubscriptionStatus());
        claims.put(JWTConstant.IsVerifiedEmail, userInfo.getIsVerifiedEmail());
        claims.put(JWTConstant.IsVerifiedPhone, userInfo.getIsVerifiedPhone());
        claims.put(JWTConstant.UserStatus, userInfo.getUserStatus());
        claims.put(JWTConstant.IsAdmin, userInfo.getIsAdmin());
        claims.put(JWTConstant.ScheduledDate, userInfo.getScheduledDate());
        claims.put(JWTConstant.LastLoggedInDate, userInfo.getLastLoggedInDate());
        claims.put(JWTConstant.ModifiedDate, userInfo.getModifiedDate());
        claims.put(JWTConstant.RegDate, userInfo.getRegDate());

        return claims;
    }

    private Key createSigningKey() {

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] apiKeySecretBytes = encoder.encode(PRIVATE_KEY.getBytes());
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.RS256.getJcaName());
    }

    public UserInfoDTO getUserInfoFromRequest(HttpServletRequest request) {

        String token = getTokenFromRequest(request);
        return getUserInfoFromToken(token);

    }

    /**
     *
     * @param token
     * @return UserInfoDTO
     *
     * 1. JWT Token 을 parsing 하여, Claim 정보를 얻는다.
     * 2. Claim 정보를 확인하여, UserInfoDTO 를 반환한다.
     *
     */
    public UserInfoDTO getUserInfoFromToken(String token) {

        if(token.contains(JWTConstant.JWT_PREFIX)) {

            token.replace(JWTConstant.JWT_PREFIX, "");

        }

        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setUnid(Integer.parseInt(getValueFromToken(token, JWTConstant.UNID)));
        userInfo.setEmail(getValueFromToken(token, JWTConstant.Email));
        userInfo.setUserIp(getValueFromToken(token, JWTConstant.UserIP));
        userInfo.setFullName(getValueFromToken(token, JWTConstant.FullName));
        userInfo.setDob(getValueFromToken(token, JWTConstant.DOB));
        userInfo.setPhone(getValueFromToken(token, JWTConstant.Phone));
        userInfo.setAgreeNotifications(getValueFromToken(token, JWTConstant.AgreeNotifications));
        userInfo.setPayingCustomer(getValueFromToken(token, JWTConstant.PayingCustomer));
        userInfo.setSubscriptionStatus(getValueFromToken(token, JWTConstant.SubscriptionStatus));
        userInfo.setIsVerifiedEmail(getValueFromToken(token, JWTConstant.IsVerifiedEmail));
        userInfo.setIsVerifiedPhone(getValueFromToken(token, JWTConstant.IsVerifiedPhone));
        userInfo.setUserStatus(getValueFromToken(token, JWTConstant.UserStatus));
        userInfo.setIsAdmin(getValueFromToken(token, JWTConstant.IsAdmin));
        userInfo.setScheduledDate(getValueFromToken(token, JWTConstant.ScheduledDate));
        userInfo.setLastLoggedInDate(getValueFromToken(token, JWTConstant.LastLoggedInDate));
        userInfo.setModifiedDate(getValueFromToken(token, JWTConstant.ModifiedDate));
        userInfo.setRegDate(getValueFromToken(token, JWTConstant.RegDate));

        return userInfo;

    }

    public Claims getClaimsFromToken(String token) {

        if(token.contains(JWTConstant.JWT_PREFIX)) {
            token.replace(JWTConstant.JWT_PREFIX, "");
        }

        return Jwts.parser().setSigningKey(RSA_PUBLIC_KEY)
                .parseClaimsJws(token).getBody();

    }

    public String getValueFromToken(String token, String key) {

        if(token.contains(JWTConstant.JWT_PREFIX)) {

            token.replace(JWTConstant.JWT_PREFIX, "");

        }

        Claims claims = getClaimsFromToken(token);
        return String.valueOf(claims.get(key));

    }

    /**
     *
     * @param request
     * @return boolean
     *
     * 1. Header 에 JWT Token 이 존재하는지 확인한다.
     * 2. Header 에 JWT 가 없다면, Cookie 값에 JWT Token 이 존재하는지 확인한다.
     * 3. token 값을 파싱하여 valid 한 토큰인지 검증한다.
     */
    public boolean validateToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        String cookieToken = "";
        String token = "";

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(WhaleConstant.WHALE_AUTHKEY.equals(cookie.getName())) {

                    cookieToken = cookie.getValue();

                }
            }
        }

        String header = request.getHeader(JWTConstant.JWT_HEADER);

//        logger.info("header : " + header);
//        logger.info("cookieToken : " + cookieToken);

        if(StringUtil.isEmpty(header) && StringUtil.isEmpty(cookieToken)) {

            return false;

        }

        token = StringUtil.isEmpty(header) ? cookieToken : header.replace(JWTConstant.JWT_PREFIX, "");

        logger.info("token : " + token);

        return isValidToken(token);

    }

    public void removeJwtFromHeader(HttpServletResponse response) {

        String jwtHeader = response.getHeader(JWTConstant.JWT_HEADER);

        logger.info("jwt header : " + jwtHeader);

        if(!StringUtil.isEmpty(jwtHeader)) {

            logger.info("NOT EMPTY !");

            response.setHeader(JWTConstant.JWT_HEADER, "");

        }
    }

}
