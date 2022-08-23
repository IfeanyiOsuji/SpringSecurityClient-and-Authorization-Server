package com.ifeanyi.springsecurityclient.event;

import com.ifeanyi.springsecurityclient.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class RegistrationCompleteevent extends ApplicationEvent {
    private User user;
    private String applicationurl;
    public RegistrationCompleteevent(User user, String applicationurl) {
        super(user);
        this.user = user;
        this.applicationurl = applicationurl;
    }
}
