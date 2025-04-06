package com.todo.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
/*
    private final UserService userService;

    @Autowired
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

 */


    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        // Selecting username and password
        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT email, password, enabled FROM users WHERE email=?");
        //Using default role system
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT email, 'ROLE_USER' FROM users WHERE email=?");

        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/showSignUp",
                            "/showForgotPassword",
                            "/processForm",
                            "/sendRequest",
                            "/resetPassword",
                            "/processNewPassword").permitAll();
                    //Permitting to use other links rather than login link
                    auth.requestMatchers("/home/**").authenticated();
                    auth.anyRequest().authenticated();
                })
                .formLogin(form ->
                        form
                                .loginPage("/showSignIn")
                                .loginProcessingUrl("/authenticateTheUser")
                                /*.successHandler(((request, response, authentication) -> {
                                    String userEmail = authentication.getName();
                                    User user = userService.findUserByEmail(userEmail);

                                    response.sendRedirect("/home/tasks?userId=" + user.getUserId());
                                }))*/
                                .defaultSuccessUrl("/home/tasks", true)
                                .permitAll())
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}

