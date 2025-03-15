package com.todo.controller;

import com.todo.entity.User;
import com.todo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EntranceController {
    private final UserService userService;

    @Autowired
    public EntranceController(UserService userService) {
        this.userService = userService;
    }

    //Checking forms to make sure there is no white space
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showSignIn")
    public String showSignInPage() {
        return "entrance/signin";
    }

    @GetMapping("/showSignUp")
    public String showSignUpForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "entrance/signup";
    }

    @PostMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("user") User user, BindingResult theBindingResult) {
        //Valid annotation basically say that, it needs to perform validation for given form and ModelAttribute read the data from customer that is submitted on html form
        //BindingResult basically checks the result again with an input and if there's an error, it will go back, or we can create more custom validation
        System.out.println("lastName: |" + user.getLastname() + "|");
        System.out.println("Binding result: " + theBindingResult.toString());
        if (theBindingResult.hasErrors()) {//If something is wrong go back to form page
            return "entrance/signup";
        }
        userService.saveUser(user);
        return "entrance/confirmation"; //Redirecting the confirmation page of HTML file that we created
    }
}
