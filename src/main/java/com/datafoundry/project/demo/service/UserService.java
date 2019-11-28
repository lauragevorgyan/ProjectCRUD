package com.datafoundry.project.demo.service;

import java.util.ArrayList;
import java.util.List;

import com.datafoundry.project.demo.repo.UserRepository;
import com.datafoundry.project.demo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepo;

    private boolean existsById(Long id) {
        return userRepo.existsById(id);
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepo.findAll().forEach(users::add);
        return users;
    }

    public User save(User user) throws Exception {
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new Exception("Email is required");
        }
        if (user.getId() != null && existsById(user.getId())) {
            throw new Exception("User with id: " + user.getId() + " already exists");
        }
        return userRepo.save(user);
    }

    public void deleteById(Long id) throws Exception {
        if (!existsById(id)) {
            throw new Exception("Cannot find User with id: " + id);
        }
        else {
            userRepo.deleteById(id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
            return (UserDetails) userRepo.findByEmail(s);
    }
}
