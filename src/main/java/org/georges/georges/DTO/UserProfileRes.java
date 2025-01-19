package org.georges.georges.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileRes {
    private String Username;
    private String Description;
    private String EmailAddress;
    private byte[] ProfileImage;
    public UserProfileRes(String username, String description, String emailAddress) {
        Username = username;
        Description = description;
        EmailAddress = emailAddress;
    }
    public UserProfileRes() {}
}
