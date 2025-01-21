package com.moxi.lyra.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContactRes {
    private Long id;
    private List<String> contacts;


}
