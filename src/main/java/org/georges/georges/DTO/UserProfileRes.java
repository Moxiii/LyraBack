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
    public UserProfileRes() {}
}
