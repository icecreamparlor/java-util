import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import wisely.web.member.constant.JWTConstant;
import wisely.web.member.constant.WhaleConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class CookieUtil {

    private static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    private static final String COOKIE_PATH = "/";

    public static void setCookieMultiDomain(HttpServletResponse httpResponse, String key, String value, int maxAge) {

        String domainString = WhaleUtil.getApplicationPropertyValue("cookie.multidomain");
        String[] domains = domainString.split(";");

        for(String domain : domains) {

            Cookie cookie = new Cookie(key, value);

            cookie.setPath(COOKIE_PATH);
            cookie.setDomain(domain);
            cookie.setMaxAge(maxAge);
            httpResponse.addCookie(cookie);

        }
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String key, String value, int maxAge) {

        if(StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {

            return;

        }

        String domain = WhaleUtil.getApplicationPropertyValue("cookie.domain");

        Cookie cookie = new Cookie(key, value);

        cookie.setPath(COOKIE_PATH);
        cookie.setDomain(domain);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

    }

    /**
     *
     * @param request
     * @param response
     * @param key
     * @param value
     * @param maxAge
     * @param sameSite
     *
     * Chrome 80 업데이트에 의한 SameSite=None 설정을 해 주는 setCookie 함수
     * Https 에만 적용되는지 확인해 보아야 한다.
     *
     */

    public static void setCookieWithSameSite(HttpServletRequest request, HttpServletResponse response, String key, String value, String domain, String sameSite) {

        int oneDay = 60*60*24;
        int maxAge = oneDay*365*20;

        setCookieWithSameSite(request, response, key, value, domain, maxAge, sameSite);

    }

    public static void setCookieWithSameSite(HttpServletRequest request, HttpServletResponse response, String key, String value, int maxAge, String sameSite) {

        String domain = WhaleUtil.getApplicationPropertyValue("cookie.domain");

        setCookieWithSameSite(request, response, key, value, domain, maxAge, sameSite);

    }

    public static void setCookieWithSameSite(HttpServletRequest request, HttpServletResponse response, String key, String value, String domain, int maxAge, String sameSite) {

        if(StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
            return;
        }

        rewriteSetCookieExceptKey(request, response, key);

        String xForwardedProto = request.getHeader(WhaleConstant.X_FORWARDED_PROTO);

        // ELB 를 거치면, SSL 인증을 받아도 http 로 표시되는 경우가 있음
        if(!request.isSecure()
                && (xForwardedProto == null  || !"https".equalsIgnoreCase(xForwardedProto))) {
            /*
             *   HTTPS 가 아니면 setCookie 를 적용시키는게 맞지만,
             *   Amazon ELB 를 거쳐 서버에서 Port 를 확인하게 되면 80으로 인식되는 이슈가 있음
             *   개발환경에서도 쿠키가 적용되게, 다른 포트에서는 setCookie 를 호출한다.
             */
            setCookie(request, response, key, value, maxAge);
            return;
        }

        ResponseCookie cookie = ResponseCookie.from(key, value)
                .domain(domain)
                .sameSite(sameSite)
                .secure(true)
                .path(COOKIE_PATH)
                .maxAge((long) maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }

    /**
     *
     * @param request
     * @param key
     * @return cookieValue
     *
     * request 로부터 key 에 해당하는 Cookie 값들을 찾아서 반환한다.
     */
    public static String getCookieValue(HttpServletRequest request, String key) {

        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(key.equals(cookie.getName())) {

                    return cookie.getValue();

                }
            }
        }

        return "";
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key) {

//        rewriteSetCookieExceptKey(request, response, key);

        setCookieWithSameSite(request, response, key, "removed", 0, "None");

    }

    /**
     *
     * @param request
     * @param response
     * @param key
     *
     * parameter 로 넘어온 key 를 제외하고,
     * Set-Cookie 헤더를 다시 작성한다.
     * (쿠키 값을 삭제, 수정할 때 사용되는 로직)
     */
    public static void rewriteSetCookieExceptKey(HttpServletRequest request, HttpServletResponse response, String key) {

        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean firstHeader = true;

        for(String header : headers) {
            if(!header.contains(key)) {

//                logger.info("(CookieUtil) Header : " + header);

                if(firstHeader) {
                    response.setHeader(HttpHeaders.SET_COOKIE, header);
                    firstHeader = false;
                } else {
                    response.addHeader(HttpHeaders.SET_COOKIE, header);
                }
            }
        }

    }

//    public String getCacheKey(HttpServletRequest request) {
//
//        Cookie[] cookies = request.getCookies();
//        String cookieCacheKey = "";
//        String cacheKey = "";
//
//        if(cookies != null) {
//            for(Cookie cookie : cookies) {
//                if(WhaleConstant.WHALE_CACHEKEY.equals(cookie.getName())) {
//
//                    cookieCacheKey = cookie.getValue();
//
//                }
//            }
//        }
//
//        String header = request.getHeader(WhaleConstant.HEADER_WHALE_CACHEKEY);
//
//        cacheKey = StringUtil.isEmpty(header) ? cookieCacheKey : header;
//
//        return cacheKey;
//
//    }
}
