package org.georges.georges.service;

import org.georges.georges.pojos.Contact;
import org.georges.georges.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

    public void addContact(String email){
        Contact contact =  new Contact();
        contact.setEmail(email);
        contactRepository.save(contact);
    }

    public void deleteContact(Long contactId){
        contactRepository.deleteById(contactId);
    }

    public List<Contact> getContacts(){
        return  contactRepository.findAll();
    }
}
