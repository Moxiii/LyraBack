package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRes {
    private String email;
    private String token ;
    public LoginRes(String email, String token){
        this.email=email;
        this.token=token;
    }
}
