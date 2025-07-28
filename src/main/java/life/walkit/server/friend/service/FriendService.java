package life.walkit.server.friend.service;

import jakarta.transaction.Transactional;
import life.walkit.server.friend.dto.FriendRequestResponseDTO;
import life.walkit.server.friend.error.FriendErrorCode;
import life.walkit.server.friend.error.FriendException;
import life.walkit.server.friend.repository.FriendRepository;
import life.walkit.server.friend.entity.FriendRequest;
import life.walkit.server.friend.repository.FriendRequestRepository;
import life.walkit.server.member.entity.Member;
import life.walkit.server.member.error.MemberException;
import life.walkit.server.member.error.enums.MemberErrorCode;
import life.walkit.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    // 친구 요청 로직
    public FriendRequestResponseDTO sendFriendRequest(Long requesterId, String targetNickname) {
        // 1. 본인에게 친구 요청 방지
        Member sender = memberRepository.findById(requesterId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (sender.equals(receiver)) {
            throw new MemberException(MemberErrorCode.SELF_FRIEND_REQUEST);
        }

        // 2. 이미 친구 관계 확인
        if (friendRepository.existsByMemberAndPartner(sender, receiver)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }

        // 3. 동일한 요청 중복 방지
        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new MemberException(MemberErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        // 4. 새 요청 저장
        FriendRequest newRequest = friendRequestRepository.save(FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build());

        // 5. 생성된 요청에 대한 정보를 포함한 DTO 반환
        return new FriendRequestResponseDTO(
                newRequest.getStatus(),
                sender.getNickname(),
                receiver.getNickname(),
                newRequest.getFriendRequestId()
        );
    }




}
