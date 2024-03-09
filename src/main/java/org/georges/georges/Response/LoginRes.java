package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRes {
    private String username;
    private String token ;
    public LoginRes(String username, String token){
        this.username=username;
        this.token=token;
    }
}
