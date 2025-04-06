package com.todo.service;

import com.todo.dao.UserRepository;
import com.todo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User findUserByUserId(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cannot find the user with id: " + userId));
    }

    @Override
    public void deleteUserByUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void update(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            throw new RuntimeException("User not found with id: " + user.getUserId());
        }
        userRepository.save(user); // Same attitude save/update
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }


    //Forgot Password part
    @Override
    @Transactional
    public void updateResetPasswordToken(String token, String email) {
        User user = findUserByEmail(email);
        user.setResetPasswordToken(token);
        userRepository.save(user);
    }

    @Override
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("User not found with token: " + token));
    }

    @Override
    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

}
