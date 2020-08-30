package tech.eportfolio.server.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String title;
    private String phone;
    private String username;
}
