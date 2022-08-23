package com.ifeanyi.springsecurityclient.controller;

import com.ifeanyi.springsecurityclient.entity.User;
import com.ifeanyi.springsecurityclient.entity.VerificationToken;
import com.ifeanyi.springsecurityclient.event.RegistrationCompleteevent;
import com.ifeanyi.springsecurityclient.model.PasswordModel;
import com.ifeanyi.springsecurityclient.model.UserModel;
import com.ifeanyi.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
//@RequestMapping("/api/v1")
public class RegistrationController {
    @Autowired
    UserService userService;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userMmodel, final HttpServletRequest request){
       User user =  userService.registerUser(userMmodel);
       applicationEventPublisher.publishEvent(new RegistrationCompleteevent(
               user,
               applicationurl(request)
       ));
     return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token){
        String result =  userService.verifyToken(token);
        if(result.equalsIgnoreCase("valid")){
            return "user verified successfully";
        }
        return "Bad user";

    }
    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
       // User user = verificationToken.getUser();
        resendVerificationTokenMail(applicationurl(request), verificationToken);
        return "Verification Link sent";

    }

    private void resendVerificationTokenMail(String applicationurl, VerificationToken verificationToken) {

        //send email to user
        String url =
                applicationurl
                        + "/resendVerificationToken?token="
                        +verificationToken.getToken();
        //resendVerificationEmail
        log.info("Click the link to verify account: {}",
                url);
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel resetPasswordModel, HttpServletRequest request){
        Optional<User> optional = userService.findUserByEmail(resetPasswordModel.getEmail());
        if(!optional.isPresent())
            return null;
        User user = optional.get();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        String url = passwordResetTokenMail(applicationurl(request), token);

        return url;
    }
    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid"))
            return "Invalid Token";
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(), passwordModel.getNewPassword());
        return "Password reset successfully";
        }
        else return "Invalid token";

    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        Optional<User> user = userService.findUserByEmail(passwordModel.getEmail());
        if(user.isPresent()){
            if(!userService.checkIfValidOldPassword(user.get(), passwordModel.getOldPassword())){
                return "Invalid old password";
            }
            //save new Password
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password Changed successfully";
        }
        return "invalid user";

    }


    private String passwordResetTokenMail(String applicationurl, String token) {
        String url =
                applicationurl
                        + "/savePassword?token="
                        +token;
        //resendVerificationEmail
        log.info("Click the link below to reset your password: {}",
                url);
        return url;
    }

    private String applicationurl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":"+
                request.getServerPort() +
                request.getContextPath();

    }

}
