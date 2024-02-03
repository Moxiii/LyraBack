package org.georges.georges.Api;

import org.georges.georges.pojos.User;
import org.georges.georges.repository.UserRepository;
import org.georges.georges.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserApiService {
    @Autowired
    private UserService UserService;
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}
