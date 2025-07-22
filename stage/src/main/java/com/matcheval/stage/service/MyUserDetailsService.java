package com.matcheval.stage.service;

import com.matcheval.stage.model.Users;
import com.matcheval.stage.model.UsersDetails;
import com.matcheval.stage.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user= userRepo.findByEmail(username);
        if (user == null) {
            System.out.println("User Not Found");

            throw new UsernameNotFoundException("user not found");
        }

        return new UsersDetails(user);
    }
}
