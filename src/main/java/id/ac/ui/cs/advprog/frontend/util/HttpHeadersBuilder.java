package id.ac.ui.cs.advprog.frontend.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpHeadersBuilder {

    public static HttpHeaders build() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin("http://localhost:8080");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
