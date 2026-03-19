package danil.blog.dto;

import lombok.Data;

@Data
public class ProfileUpdateForm {

    private int id;
    private String username;
    private String email;
    private String password;
    private byte[] avatar;

    public ProfileUpdateForm() {
    }
}
