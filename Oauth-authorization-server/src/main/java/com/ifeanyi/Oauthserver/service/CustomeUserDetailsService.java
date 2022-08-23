package com.ifeanyi.Oauthserver.service;

import com.ifeanyi.Oauthserver.entity.User;
import com.ifeanyi.Oauthserver.repository.Userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomeUserDetailsService implements UserDetailsService {
    @Autowired
    private Userrepository userrepository;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userrepository.findUserByEmail(email);
        if(!user.isPresent())
            throw new NullPointerException("User not found");
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                user.get().isEnabled(),
                true,
                true,
                true,
                getAuthorities(List.of(user.get().getRole()))
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
     for(String role:roles){
         grantedAuthorities.add(new SimpleGrantedAuthority(role));
     }
     return grantedAuthorities;
    }
}
