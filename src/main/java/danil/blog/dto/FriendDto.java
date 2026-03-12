package danil.blog.dto;

import danil.blog.models.FriendStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendDto {

    private int id;
    private String username;
    private byte[] avatar;
    private FriendStatus sentStatus;
    private FriendStatus receivedStatus;

    public FriendDto(int id, String username, byte[] avatar, FriendStatus sentStatus, FriendStatus receivedStatus) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.sentStatus = sentStatus;
        this.receivedStatus = receivedStatus;
    }
}
