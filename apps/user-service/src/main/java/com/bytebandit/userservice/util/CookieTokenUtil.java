package com.bytebandit.userservice.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieTokenUtil {

    public static void saveTokenInCookie(
            String token,
            String name,
            long expiration,
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie(name, token);
        cookie.setMaxAge((int) (expiration / 1000));
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }
}
