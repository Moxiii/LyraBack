package com.moxi.lyra.DTO;

import com.moxi.lyra.User.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
private int id;
private String username;

public UserDTO(String username) {
	this.username = username;
}
public static UserDTO convertUserToDTO(User user) {
	return new UserDTO(user.getUsername());
}
}
