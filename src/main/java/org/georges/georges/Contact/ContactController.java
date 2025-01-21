package org.georges.georges.Contact;


import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Config.CustomAnnotation.RequireAuthorization;
import org.georges.georges.Config.Utils.SecurityUtils;
import org.georges.georges.DTO.ContactRes;
import org.georges.georges.DTO.FriendRequest;
import org.georges.georges.User.User;
import org.georges.georges.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@RequireAuthorization
@RestController
@RequestMapping("/api/user/friends")
public class ContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    private UserService userService;


    @GetMapping("/")
    public ResponseEntity<?> getFriends() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<Contact> contacts = contactService.findAllByUser(currentUser);
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
            Contact contact = contactService.findById(id);
            ContactRes contactRes = new ContactRes();
            contactRes.setId(contact.getId());
            contactRes.setContacts(List.of(contact.getUser().getUsername()));
            return ResponseEntity.status(HttpStatus.OK).body(contactRes);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFriend( @RequestBody FriendRequest friendRequest) {
            User currentUser = SecurityUtils.getCurrentUser();
            User addFriend = userService.findByUsername(friendRequest.getUsername());
            Contact existingContact = contactService.findByUser(addFriend);
            if (existingContact != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Contact already exists");
            }
            Contact contact = new Contact();
            contact.setUser(currentUser);
            contact.setContact(addFriend);
            contact.setStatus(ContactStatus.PENDING);
            contact.setDateAdded(LocalDate.now());
            contactService.save(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message :", "friend " + addFriend.getUsername() + " added successfully"));
    }
    @PutMapping("/update/{ID}")
    public ResponseEntity<?> updateFriend( @PathVariable Long ID, @RequestBody String friendName) {
        User currentUser = SecurityUtils.getCurrentUser();
        User updateFriend = userService.findById(currentUser.getId());
        Contact existingContact = contactService.findByUser(updateFriend);
        if (existingContact != null) {
            existingContact.setStatus(ContactStatus.PENDING);


        }
        contactService.save(existingContact);
        return ResponseEntity.status(HttpStatus.OK).body("Contact successfully updated");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFriend( @PathVariable Long id) {
            Contact contact = contactService.findById(id);
            contactService.delete(contact);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Contact deleted"));
    }

}
