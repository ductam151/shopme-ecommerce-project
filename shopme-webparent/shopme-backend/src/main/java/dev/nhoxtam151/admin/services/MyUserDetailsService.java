package dev.nhoxtam151.admin.services;

import dev.nhoxtam151.shopmecommon.models.User;
import dev.nhoxtam151.admin.models.UserDecorator;
import dev.nhoxtam151.admin.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDecorator loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Can not find this user"));
        return new UserDecorator(user);
    }
}
