package com.ifeanyi.springsecurityclient.service;

import com.ifeanyi.springsecurityclient.entity.User;
import com.ifeanyi.springsecurityclient.entity.VerificationToken;
import com.ifeanyi.springsecurityclient.model.PasswordModel;
import com.ifeanyi.springsecurityclient.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    User registerUser(UserModel model);

    void saveVerificationTokenForUser(String token, User user);

    String verifyToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User resetUserPassword(PasswordModel resetPasswordModel);
    void createPasswordResetTokenForUser(User user, String token);

    Optional<User> findUserByEmail(String email);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);
}
