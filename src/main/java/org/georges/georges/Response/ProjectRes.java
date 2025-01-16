package org.georges.georges.Response;

import lombok.Getter;
import lombok.Setter;
import org.georges.georges.User.User;

import java.util.List;

@Getter
@Setter
public class ProjectRes {
    private Long id;
    private String name;
    private String description;
    private List<String> links;
    private List<String> users;
    private byte[] projectPicture;

}
