package com.moxi.lyra.Conversation;

import com.moxi.lyra.Config.CustomAnnotation.RequireAuthorization;
import com.moxi.lyra.Config.Utils.SecurityUtils;
import com.moxi.lyra.Conversation.Message.Message;
import com.moxi.lyra.Conversation.Message.MessageService;
import com.moxi.lyra.DTO.ConversationDTO;
import com.moxi.lyra.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequireAuthorization
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
@Autowired
private ConversationService conversationService;
@Autowired
private MessageService messageService;
@GetMapping("/get")
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
@GetMapping("/get/{id}")
public ResponseEntity<?> getConversationById(@PathVariable("id") Long id) {
	Conversation conversation = conversationService.findById(id);
	ConversationDTO dto = new ConversationDTO(conversation);
	messageService.transfertOldMessageToSql();
	return ResponseEntity.ok(dto);
}
}
