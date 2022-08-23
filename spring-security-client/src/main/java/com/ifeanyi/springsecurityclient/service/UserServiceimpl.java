package com.ifeanyi.springsecurityclient.service;

import com.ifeanyi.springsecurityclient.entity.PasswordResetToken;
import com.ifeanyi.springsecurityclient.entity.User;
import com.ifeanyi.springsecurityclient.entity.VerificationToken;
import com.ifeanyi.springsecurityclient.model.PasswordModel;
import com.ifeanyi.springsecurityclient.model.UserModel;
import com.ifeanyi.springsecurityclient.repository.PasswordResetTokenRepository;
import com.ifeanyi.springsecurityclient.repository.UserRepository;
import com.ifeanyi.springsecurityclient.repository.VerificationtokenRepository;
import org.aspectj.weaver.ast.Instanceof;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceimpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationtokenRepository verificationtokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationtokenRepository.save(verificationToken);
    }

    @Override
    public String verifyToken(String token) {
        Optional<VerificationToken>optional = verificationtokenRepository.findByToken(token);
        if(!optional.isPresent())
            return "Invalid";
        VerificationToken verificationToken = optional.get();
        if(isExpiredToken(verificationToken)) {
            verificationtokenRepository.delete(verificationToken);
            return "Expired";
        }
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";


    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        Optional<VerificationToken>optional = verificationtokenRepository.findByToken(oldToken);
        if(!optional.isPresent())
            return null;
        VerificationToken verificationToken = optional.get();
        verificationToken.setToken(UUID.randomUUID().toString());
        return verificationtokenRepository.save(verificationToken);

    }

    @Override
    public User resetUserPassword(PasswordModel resetPasswordModel) {
        Optional<User>optional = userRepository.findUserByEmail(resetPasswordModel.getEmail());
        if(!optional.isPresent())
            return null;
        User user = optional.get();
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        return user;
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken == null)
            return "Invalid";
        if(isExpiredToken(passwordResetToken)) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "Expired";
        }

        return "Valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());

    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    private String applicationurl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":"+
                request.getServerPort() +
                request.getContextPath();

    }

    private boolean isExpiredToken(Object object) {
        Calendar calendar = Calendar.getInstance();
        if(object == VerificationToken.class){
            VerificationToken verificationToken = (VerificationToken)object;

        return verificationToken.getExpirationTime().getTime() <= calendar.getTime().getTime();
        }
        else{
            PasswordResetToken passwordResetToken = (PasswordResetToken) object;
            return passwordResetToken.getExpirationTime().getTime() <= calendar.getTime().getTime();
        }
    }
}
