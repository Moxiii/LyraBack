package com.moxi.lyra.Conversation;

import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import com.moxi.lyra.DTO.ConversationDTO;
import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequireAuthorization
@RestController("/user/conversation")
public class ConversationController {
@Autowired
private ConversationService conversationService;
@GetMapping("/")
public ResponseEntity<?> getConversation() {
	User currentUser = SecurityUtils.getCurrentUser();
	List<Conversation> conversations = conversationService.findByUser(currentUser);
	List<ConversationDTO> conversationDTOs = conversations.stream().map(conversation -> {
		ConversationDTO dto = new ConversationDTO();
		dto.setId(conversation.getId());
		dto.setParticipants(conversation.getParticipants().stream()
				.filter(user -> !user.getId().equals(currentUser.getId()))
				.map(User::getUsername)
				.collect(Collectors.toList()
				)
		);
			dto.setLastMessage(conversation.getMessages().isEmpty()
			? null : conversation.getMessages().get(conversation.getMessages().size() -1).getContent());
			return dto;
	}).collect(Collectors.toList());
	return ResponseEntity.ok(conversationDTOs);

}
@PostMapping("/add")
public ResponseEntity<String> addConversation() {
	return null;
}

}
