package com.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class GlobalFunction {

    public static String getUsername(HttpServletRequest request) {
        return getCustomCookie(request).get(0);
    }

    public static String getUserRole(HttpServletRequest request) {
        return getCustomCookie(request).get(1);
    }

    public static List<String> getCustomCookie(HttpServletRequest request) {
        Optional<String> encodedCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> "CUSTOM_COOKIE".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny();

        byte[] decodedBytes = Base64.getDecoder().decode(encodedCookie.get());

        String decodedCookie = new String(decodedBytes);

        List<String> userDetails = List.of(decodedCookie.split("-"));

        return userDetails;
    }

}
