package com.moxi.lyra.DTO;

import com.moxi.lyra.Contact.ContactStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ContactDTO {
private Long id;
private String name;
private String nickname ="";
private ContactStatus status;
private LocalDate dateAdded;
private Long ConversationID;
}

