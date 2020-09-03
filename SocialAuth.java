import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import reedride.crypto.cipher.MD5Digest;
import reedride.exception.RException;
import reedride.web.auth.api.FacebookApi;
import reedride.web.auth.api.GoogleApi;
import reedride.web.auth.api.KakaoApi;
import reedride.web.auth.api.NaverApi;
import reedride.web.auth.data.AuthDS;
import reedride.web.util.Constant.Auth.Provider;

public class SocialAuth {
    private OAuth20Service service = null;
    private String oAuthRequestUrl = null;
    private String clientId = null;
    private String clientSecret = null;
    private String callbackUrl = null;

    public SocialAuth(String clientId, String clientSecret, String callbackUrl, String provider) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.callbackUrl = callbackUrl;
        DefaultApi20 providerApi = NaverApi.instance();
        if (provider.equalsIgnoreCase(Provider.NAVER)) {
            providerApi = NaverApi.instance();
            this.oAuthRequestUrl = NaverApi.instance().getUserInfoEndpoint();
        } else if (provider.equalsIgnoreCase(Provider.KAKAO)) {
            providerApi = KakaoApi.instance(clientId);
            this.oAuthRequestUrl = KakaoApi.instance(clientId).getUserInfoEndpoint();
        } else if (provider.equalsIgnoreCase(Provider.FACEBOOK)) {
            providerApi = FacebookApi.instance();
            this.oAuthRequestUrl = FacebookApi.instance().getUserInfoEndpoint();
        } else if (provider.equalsIgnoreCase(Provider.GOOGLE)) {
            providerApi = GoogleApi.instance();
            this.oAuthRequestUrl = GoogleApi.instance().getUserInfoEndpoint();
        }

        if (provider.equalsIgnoreCase(Provider.GOOGLE)) {
            this.service = (new ServiceBuilder(clientId)).apiSecret(clientSecret).defaultScope("profile").callback(callbackUrl).build((DefaultApi20)providerApi);
        } else {
            this.service = (new ServiceBuilder(clientId)).apiSecret(clientSecret).callback(callbackUrl).build((DefaultApi20)providerApi);
        }

    }

    public String getRequestUrl(String secretState) {
        return this.service.getAuthorizationUrl(secretState);
    }

    public AuthDS getUserInfo(String code, String secretState) {
        String body = "{\"id\":-1}";

        try {
            if (!MD5Digest.get(this.clientId + this.clientSecret + this.callbackUrl).equals(secretState)) {
                return new AuthDS(body);
            }

            OAuth2AccessToken accessToken = this.service.getAccessToken(code);
            OAuthRequest request = new OAuthRequest(Verb.GET, this.oAuthRequestUrl);
            this.service.signRequest(accessToken, request);
            Response response = this.service.execute(request);
            body = response.getBody();
        } catch (IOException | InterruptedException | ExecutionException | RException var7) {
            System.out.print("ERROR");
            var7.printStackTrace();
        }

        return new AuthDS(body);
    }

    public AuthDS getUserInfo(String token) {
        String body = "{\"id\":-1}";

        try {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(token);
            OAuthRequest request = new OAuthRequest(Verb.GET, this.oAuthRequestUrl);
            this.service.signRequest(accessToken, request);
            Response response = this.service.execute(request);
            body = response.getBody();
        } catch (InterruptedException | ExecutionException | IOException var6) {
            System.out.print("ERROR");
            var6.printStackTrace();
        }

        return new AuthDS(body);
    }

    public AuthDS unlinkKakaoApp(String token) {
        String body = "{\"id\":-1}";

        try {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(token);
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://kapi.kakao.com/v1/user/unlink");
            this.service.signRequest(accessToken, request);
            Response response = this.service.execute(request);
            body = response.getBody();
        } catch (InterruptedException | ExecutionException | IOException var6) {
            System.out.print("ERROR");
            var6.printStackTrace();
        }

        return new AuthDS(body);
    }
}
