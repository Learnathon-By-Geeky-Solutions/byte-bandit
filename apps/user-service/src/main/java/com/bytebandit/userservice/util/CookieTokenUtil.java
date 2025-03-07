package com.bytebandit.userservice.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;

@UtilityClass
public class CookieTokenUtil {

    public static void saveTokenInCookie(
            String jwtToken,
            String name,
            long expiration,
            HttpServletResponse response
    ) {
        Cookie jwtCookie = new Cookie(name, jwtToken);
        jwtCookie.setMaxAge((int) (expiration / 1000));
        jwtCookie.setPath("/");
        jwtCookie.setDomain("");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        response.addCookie(jwtCookie);
    }
}
