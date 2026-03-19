package danil.blog.dto;

import lombok.Data;

@Data
public class PostDto {
    private String postText;
    private byte[] postImage;
    private String username;
}
