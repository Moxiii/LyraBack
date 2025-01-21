package com.moxi.lyra.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CreateProjectDTO {
    private String name;
    private String description;
    private List<String> links;
    private List<String> username;
    private String projectPicture;
}
