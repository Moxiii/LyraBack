package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class ErrorRes {
    HttpStatus httpStatus;
    String message ;
    public ErrorRes(HttpStatus httpStatus , String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
