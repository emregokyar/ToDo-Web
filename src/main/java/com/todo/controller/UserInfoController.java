package com.todo.controller;

import com.todo.entity.User;
import com.todo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/home")
public class UserInfoController {

    private UserService userService;

    @Autowired
    public UserInfoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/showUserInfo")
    public String showUserInfo(Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);

        model.addAttribute("user", user);
        return "main/user-info";
    }

    @PostMapping("/processUpdate")
    public String processUpdate(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Principal principal, Model model, @RequestParam("imageFile") MultipartFile file) {
        String email = principal.getName();
        User existingUser = userService.findUserByEmail(email);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "main/user-info";
        }

        if (!file.isEmpty()) {
            try {
                existingUser.setProfilePicture(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        userService.saveUser(existingUser);

        return "main/update-confirmation";
    }

    @GetMapping("/showUserImage")
    public ResponseEntity<byte[]> getUserImage(Principal principal){
        String email= principal.getName();
        User user= userService.findUserByEmail(email);

        if (user.getProfilePicture() != null){
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(user.getProfilePicture());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/deleteUserAccount")
    public String deleteUserAccount(Model model, Principal principal){
        String email= principal.getName();
        User existingUser= userService.findUserByEmail(email);

        userService.deleteUserByUserId(existingUser.getUserId());
        return "redirect:/showSignIn";
    }
}
