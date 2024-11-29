package dev.nhoxtam151.admin.models;

import dev.nhoxtam151.shopmecommon.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDecorator implements UserDetails {

    private final User user;

    public UserDecorator(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().toString())).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();

    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }


    public String getFullName() {
        return user.getFullName();
        //return user.getFirstName() + " " + user.getLastName();
    }

}
