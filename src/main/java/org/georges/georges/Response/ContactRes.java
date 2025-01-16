package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;
import org.georges.georges.User.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContactRes {
    private Long id;
    private List<String> contacts;


}
