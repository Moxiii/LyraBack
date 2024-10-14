package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRes {
    private String username;
    private String accessToken ;

    public LoginRes(String username, String accessToken) {
        this.username = username;
        this.accessToken = accessToken;
    }
}
