package com.controller;

import com.dto.CredentialDto;
import com.dto.UserDetailDto;
import com.entity.User;
import com.service.UserService;
import com.util.GlobalFunction;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("credentialDto", new CredentialDto());

        return "login";
    }

    @PostMapping("/login")
    public String authenticate(CredentialDto credentialDto, HttpServletResponse response) {
        User user = userService.validateCredential(credentialDto);

        if (user != null) {
            Cookie cookie = new Cookie("CUSTOM_COOKIE", generateCustomCookie(user));

            cookie.setMaxAge(36000);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);

            response.addCookie(cookie);

            return "redirect:/data-master";
        } else {
            return "redirect:/login";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());

        if (cookies.isPresent()) {
            for (int i = 0; i < cookies.get().length; i++) {
                Cookie cookie = cookies.get()[i];
                cookie.setMaxAge(0);

                response.addCookie(cookie);
            }
        }

        return "redirect:/login";
    }

    private String generateCustomCookie(User user) {
        String plainCookie = user.getUsername() + "-" + user.getRole();

        return Base64.getEncoder().encodeToString(plainCookie.getBytes(StandardCharsets.UTF_8));
    }

}
