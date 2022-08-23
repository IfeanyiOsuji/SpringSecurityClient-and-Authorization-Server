package com.ifeanyi.springsecurityclient.event.listener;

import com.ifeanyi.springsecurityclient.entity.User;
import com.ifeanyi.springsecurityclient.event.RegistrationCompleteevent;
import com.ifeanyi.springsecurityclient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener <RegistrationCompleteevent>{
    @Autowired
    private UserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteevent event) {
        //Create the verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);

        //send email to user
        String url =
                event.getApplicationurl()
                + "/verifyRegistration?token="
                +token;
        //sendVerificationEmail
        log.info("Click the link to verify account: {}",
                url);

    }
}
