package org.georges.georges.Contact;


import org.georges.georges.Config.CustomAnnotation.RequireAuthorization;
import org.georges.georges.Config.Utils.SecurityUtils;
import org.georges.georges.DTO.ContactRes;
import org.georges.georges.Todo.TodoRepository;
import org.georges.georges.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Body;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RequireAuthorization
@RestController
@RequestMapping("/api/user/friends")
public class ContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public ResponseEntity<?> getFriends() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Contact> contacts = contactRepository.findAllByUser(currentUser);
        List<ContactRes> contactResponses = contacts.stream().map(contact -> {
            ContactRes contactRes = new ContactRes();
            contactRes.setId(contact.getId());;
            contactRes.setContacts(List.of(contact.getUser().getUsername()));
            return contactRes;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(contactResponses, HttpStatus.OK);
}
    @GetMapping("/{id}")
    public ResponseEntity<?> getFriendsByID( @PathVariable Long id) {
            Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
            String username = contact.getUser().getUsername();
            return ResponseEntity.status(HttpStatus.OK).body(username);
    }

    @PostMapping("/add/")
    public ResponseEntity<?> addFriend( @Body Contact addFriend) {
            User currentUser = SecurityUtils.getCurrentUser();
            Contact contact = new Contact();
            contact.setUser(currentUser);
            contact.setContact(addFriend.getContact());
            contactRepository.save(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFriend( @PathVariable Long id) {
            Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
            contactRepository.delete(contact);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Contact deleted"));
    }

}
