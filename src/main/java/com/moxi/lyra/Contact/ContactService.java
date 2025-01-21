package com.moxi.lyra.Contact;

import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

public List<Contact> findAllByUser(User currentUser) {
    return contactRepository.findAllByUser(currentUser);
}

public Contact findById(Long id) {
    if (contactRepository.existsById(id)) {
        return contactRepository.findById(id).get();
    }
    return null;
}
public Contact findByUserId(Long userId) {
    if (contactRepository.existsById(userId)) {
        return contactRepository.findById(userId).get();
    }
    return null;
}
public Contact findByUser(User user) {
    return contactRepository.findByUser(user);
}
public void save(Contact contact) {
    contactRepository.save(contact);
}

public void delete(Contact contact) {
    contactRepository.delete(contact);
}
}
