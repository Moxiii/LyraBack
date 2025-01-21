package com.moxi.lyra.User.UserRole;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public UserRole findById(Long id) {
        return userRoleRepository.findById(id).orElse(null);
    }
}
