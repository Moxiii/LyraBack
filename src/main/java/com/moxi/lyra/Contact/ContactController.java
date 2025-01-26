package com.moxi.lyra.Contact;


import com.moxi.lyra.DTO.ContactDTO;
import lombok.extern.slf4j.Slf4j;
import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import com.moxi.lyra.DTO.ContactRes;
import com.moxi.lyra.DTO.FriendRequest;
import com.moxi.lyra.User.User;
import com.moxi.lyra.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private Contact contactFound(Long contactID){
        Contact contact = contactService.findById(contactID);
        if(contact == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return contact;
    }

    @GetMapping("/")
    public ResponseEntity<?> getFriends() {
        User currentUser = SecurityUtils.getCurrentUser();
        List<ContactDTO> contacts = contactService.getContactForUser(currentUser);
        List<ContactRes> contactResponses = contacts.stream().map(contact -> {
            ContactRes contactRes = new ContactRes();
            contactRes.setId(contact.getId());
            contactRes.setContacts(contact.getName());
            contactRes.setStatus(contact.getStatus());
            contactRes.setDateAdded(LocalDate.now());
            return contactRes;
        }).collect(Collectors.toList());
        return new ResponseEntity<>(contactResponses, HttpStatus.OK);
}

    @GetMapping("/{id}")
    public ResponseEntity<?> getFriendsByID( @PathVariable Long id) {
            Contact contact = contactFound(id);
            ContactRes contactRes = new ContactRes();
            contactRes.setId(contact.getId());
            contactRes.setContacts(contact.getContact().getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(contactRes);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFriend( @RequestBody FriendRequest friendRequest) {
            User currentUser = SecurityUtils.getCurrentUser();
            User addFriend = userService.findByUsername(friendRequest.getUsername());
            Optional<Contact> existingContact = contactService.findContactByID(addFriend.getId(), currentUser.getId());
            log.info("Result: {}", existingContact);
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
    public ResponseEntity<?> updateFriend( @PathVariable Long ID , @RequestBody Map<String ,String> request) {
        Contact contact = contactFound(ID);
        Contact existingContact = contactService.findByUserId(contact.getUser().getId());
        log.info("User to find: {}", existingContact);
        if (!request.containsKey("nickname")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'nickname' in request");
        }
        String nickname = request.get("nickname");
        if (existingContact != null) {
            existingContact.setNickName(nickname);
            contactService.save(existingContact);
            return ResponseEntity.status(HttpStatus.OK).body("Contact successfully updated");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contact not found");
    }

    @PutMapping("/accept/{ID}")
    public ResponseEntity<?> acceptFriend( @PathVariable Long ID) {
        Contact acceptedFriend = contactFound(ID);
        acceptedFriend.setStatus(ContactStatus.ACCEPTED);
        contactService.save(acceptedFriend);
        return ResponseEntity.status(HttpStatus.OK).body("Contact Accepted");
    }
    @PutMapping("/block/{ID}")
    public ResponseEntity<?> blockFriend( @PathVariable Long ID) {
        Contact blockedFriend = contactFound(ID);
        blockedFriend.setStatus(ContactStatus.BLOCKED);
        contactService.save(blockedFriend);
        return ResponseEntity.status(HttpStatus.OK).body("Contact Blocked");
    }
    @PutMapping("/mute/{ID}")
    public ResponseEntity<?> muteFriend( @PathVariable Long ID) {
        Contact mutedFriend = contactFound(ID);
        mutedFriend.setStatus(ContactStatus.MUTED);
        contactService.save(mutedFriend);
        return ResponseEntity.status(HttpStatus.OK).body("Contact Muted");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFriend( @PathVariable Long id) {
            Contact contact = contactService.findById(id);
            contactService.delete(contact);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Contact deleted"));
    }

}
