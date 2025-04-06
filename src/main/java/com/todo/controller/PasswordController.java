package com.todo.controller;

import com.todo.entity.User;
import com.todo.service.UserService;
import com.todo.utilities.Utility;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;

@Controller
public class PasswordController {
    private final UserService userService;
    private final JavaMailSender mailSender;

    @Autowired
    public PasswordController(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @GetMapping("/showForgotPassword")
    public String showForgotPasswordPage(Model model) {
        return "password-reset/forgot-password";
    }

    @PostMapping("/sendRequest")
    public String resetUserPassword(Model model, HttpServletRequest request) {
        String email = request.getParameter("email"); //Retrieving email field from html file

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i < 30; i++) {
            int index = (int) (chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        String token = sb.toString();

        try {
            userService.updateResetPasswordToken(token, email); //Changing password field
            String resetPasswordLink = Utility.getSiteURL(request) + "/resetPassword?token=" + token; //Creating token for our specific user
            sendEmail(email, resetPasswordLink);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong when sending an email!");
        }

        return "password-reset/success-reset-password";
    }

    public void sendEmail(String recipientEmail, String link) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        //Sender Email
        helper.setFrom("emregokyar1940@gmail.com", "ToDo Support");
        helper.setTo(recipientEmail);

        String subject = "Here link to reset your password";

        String content = "<p> Hello <p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            throw new RuntimeException("Can not find user with this email!");
        }
        return "password-reset/reset-password-form";
    }

    @PostMapping("/processNewPassword")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token"); //Retrieving hidden token that we created for only user
        String password = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Reset your password");

        if (user == null) {
            throw new RuntimeException("Can not find user with this mail");
        } else {
            userService.updatePassword(user, password);
        }

        return "redirect:/entrance/singin";
    }
}
