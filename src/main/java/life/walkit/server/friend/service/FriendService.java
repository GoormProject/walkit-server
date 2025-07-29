package life.walkit.server.friend.service;

import life.walkit.server.friend.entity.Friend;
import org.springframework.transaction.annotation.Transactional;
import life.walkit.server.friend.dto.FriendRequestResponseDTO;
import life.walkit.server.friend.dto.ReceivedFriendResponse;
import life.walkit.server.friend.dto.SentFriendResponse;
import life.walkit.server.friend.enums.FriendRequestStatus;
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
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public FriendRequestResponseDTO sendFriendRequest(Long senderId, String targetNickname) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Member receiver = memberRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (sender.equals(receiver)) {
            throw new FriendException(FriendErrorCode.SELF_FRIEND_REQUEST);
        }

        if (friendRepository.existsByMemberAndPartner(sender, receiver)) {
            throw new FriendException(FriendErrorCode.ALREADY_FRIEND);
        }

        if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            throw new FriendException(FriendErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        FriendRequest newRequest = friendRequestRepository.save(FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .build());

        return new FriendRequestResponseDTO(
                newRequest.getStatus(),
                sender.getNickname(),
                receiver.getNickname(),
                newRequest.getFriendRequestId()
        );
    }

    @Transactional(readOnly = true)
    public List<SentFriendResponse> getSentFriendRequests(Long memberId) {
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<FriendRequest> requests = friendRequestRepository.findBySender(sender);

        return requests.stream()
                .map(request -> new SentFriendResponse(
                        request.getReceiver().getNickname(),
                        request.getReceiver().getStatus()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReceivedFriendResponse> getReceivedFriendRequests(Long memberId) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<FriendRequest> requests = friendRequestRepository.findByReceiverAndStatus(receiver, FriendRequestStatus.PENDING);

        return requests.stream()
                .map(request -> ReceivedFriendResponse.of(request.getSender(), request.getStatus()))
                .toList();
    }

    public void approveFriendRequest(Long friendRequestId, Long approverId) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendRequest.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new FriendException(FriendErrorCode.FRIEND_STATUS_INVALID);
        }

        if (!friendRequest.getReceiver().getMemberId().equals(approverId)) {
            throw new FriendException(FriendErrorCode.UNAUTHORIZED_APPROVER);
        }

        friendRequest.approve();
        friendRequestRepository.save(friendRequest);

        List<Friend> friends = List.of(
                Friend.builder()
                        .member(friendRequest.getSender())
                        .partner(friendRequest.getReceiver())
                        .build(),
                Friend.builder()
                        .member(friendRequest.getReceiver())
                        .partner(friendRequest.getSender())
                        .build()
        );

        friendRepository.saveAll(friends);
    }


}


