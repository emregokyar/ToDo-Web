package com.todo.service;

import com.todo.entity.User;

public interface UserService {
    User saveUser(User user);

    User findUserByUserId(int userId);

    void deleteUserByUserId(int userId);

    void update(User user);

    User findUserByEmail(String email);

    void updateResetPasswordToken(String token, String email);

    User getByResetPasswordToken(String token);

    void updatePassword(User user, String newPassword);
}
