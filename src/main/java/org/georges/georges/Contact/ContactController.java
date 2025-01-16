package org.georges.georges.Contact;

import jakarta.servlet.http.HttpServletRequest;
import org.georges.georges.Config.JwtUtil;
import org.georges.georges.Config.SecurityUtils;
import org.georges.georges.Response.ContactRes;
import org.georges.georges.Todo.TodoRepository;
import org.georges.georges.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Body;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/friends")
public class ContactController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public ResponseEntity<?> getFriends(HttpServletRequest request) {
    if(SecurityUtils.isAuthorized(request, jwtUtil)){
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
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
}
    @GetMapping("/{id}")
    public ResponseEntity<?> getFriendsByID(HttpServletRequest request , @PathVariable Long id) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
            String username = contact.getUser().getUsername();
            return ResponseEntity.status(HttpStatus.OK).body(username);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }

    @PostMapping("/add/")
    public ResponseEntity<?> addFriend(HttpServletRequest request , @Body Contact addFriend) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            User currentUser = SecurityUtils.getCurrentUser();
            Contact contact = new Contact();
            contact.setUser(currentUser);
            contact.setContact(addFriend.getContact());
            contactRepository.save(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(contact);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFriend(HttpServletRequest request , @PathVariable Long id) {
        if(SecurityUtils.isAuthorized(request, jwtUtil)){
            Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
            contactRepository.delete(contact);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }

}
