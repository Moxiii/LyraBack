package org.georges.georges.Contact;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/contact")
@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;

    @PostMapping
    public void addContact(@RequestParam String email){
        contactService.addContact(email);
    }

    @GetMapping
    public List<Contact> getContact()
    {
        return contactService.getContacts();
    }

    @DeleteMapping("/{contactId}")
    public void deleteContact(@PathVariable Long contactId){
        contactService.deleteContact(contactId);
    }
}
