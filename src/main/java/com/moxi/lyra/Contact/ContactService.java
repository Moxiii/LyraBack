package com.moxi.lyra.Contact;

import com.moxi.lyra.Conversation.Conversation;
import com.moxi.lyra.Conversation.ConversationRepository;
import com.moxi.lyra.DTO.ContactDTO;
import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {
    @Autowired
    private ContactRepository contactRepository;

@Autowired
private ConversationRepository conversationRepository;

public List<Contact> findAllByUser(User currentUser) {
    return contactRepository.findAllByUser(currentUser);
}
public List<ContactDTO> getContactForUser(User user) {
    List<Contact> contacts =contactRepository.findAllByUser(user);
    return contacts.stream().map(contact -> {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(contact.getId());
        contactDTO.setName(user.getUsername());
        contactDTO.setStatus(contact.getStatus());
        contactDTO.setDateAdded(contact.getDateAdded());
        List<Conversation> conversation =  conversationRepository.findByParticipantsContaining(contact.getUser());
      List<Long> conversationIds = conversation
              .stream()
              .map(Conversation::getId)
              .collect(Collectors.toList());
      contactDTO.setConversationIDs(conversationIds);
        return contactDTO;
    }).collect(Collectors.toList());

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
public Optional<Contact> findContactByID(Long contactUserID , Long userID ) {
    return contactRepository.findContactBetweenUsers(contactUserID,userID);
}
public void save(Contact contact) {
    contactRepository.save(contact);
}

public void delete(Contact contact) {
    contactRepository.delete(contact);
}
}
