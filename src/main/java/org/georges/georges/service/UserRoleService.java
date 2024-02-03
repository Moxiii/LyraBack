package org.georges.georges.service;

import org.georges.georges.pojos.UserRole;
import org.georges.georges.repository.UserRoleRepository;
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
