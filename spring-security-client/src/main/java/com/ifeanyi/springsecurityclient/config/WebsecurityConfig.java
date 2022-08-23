package com.ifeanyi.springsecurityclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.FilterChain;

@EnableWebSecurity
public class WebsecurityConfig {
   private static final String[] WHITE_LIST_URL = {
       "/hello",
           "/register", "/verifyRegistration", "/resendVerificationToken"
   };
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .cors()
               .and()
               .csrf()
               .disable()
               .authorizeRequests()
               .antMatchers(WHITE_LIST_URL)
               .permitAll()
               .antMatchers("/api/**").authenticated()
               .and()
               .oauth2Login(oauth2login ->                   //authorization/api-client-oidc is the registration id to auth server
                       oauth2login.loginPage("/oauth2/authorization/api-client-oidc"))
               .oauth2Client(Customizer.withDefaults());

       return http.build();
    }

}
