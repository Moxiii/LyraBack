package com.moxi.lyra.DTO;

import com.moxi.lyra.Contact.ContactStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ContactRes {
    private Long id;
    private String contacts;
    private ContactStatus status;
    private LocalDate dateAdded;

}
