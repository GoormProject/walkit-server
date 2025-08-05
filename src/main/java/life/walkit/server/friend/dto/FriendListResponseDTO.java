package life.walkit.server.friend.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class FriendListResponseDTO {
    private int total;
    private int online;
    private int offline;
    private List<FriendResponseDTO> onlineFriends;
    private List<FriendResponseDTO> offlineFriends;
}

