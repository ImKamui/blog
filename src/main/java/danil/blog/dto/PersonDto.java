package danil.blog.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonDto {

    private int id;
    private String username;
    private String email;
    private byte[] avatar;
    private String role;
    private List<PostDto> posts;
}
