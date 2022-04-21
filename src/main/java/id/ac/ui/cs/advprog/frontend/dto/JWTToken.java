package id.ac.ui.cs.advprog.frontend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class JWTToken {
    private String idToken;
    private String refreshToken;

    public boolean idTokenIsEmpty() { return idToken == null || idToken.isEmpty();}

    public boolean refreshTokenIsEmpty() { return refreshToken == null || refreshToken.isEmpty();}

    public void setToken(Map<String, String> cookie) throws NullPointerException {
        if (cookie.containsKey("idToken")) setIdToken(cookie.get("idToken"));
        if (cookie.containsKey("refreshToken")) setRefreshToken(cookie.get("refreshToken"));
    }

    public String getCookie() {
        return "idToken=" + idToken + "; Secure; HttpOnly";
    }

    public String getCookieRefresh() {
        return "refreshToken=" + refreshToken + "; Secure; HttpOnly";
    }

}
