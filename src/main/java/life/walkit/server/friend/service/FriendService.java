package life.walkit.server.friend.service;

import life.walkit.server.friend.dto.*;
import life.walkit.server.friend.entity.Friend;
import life.walkit.server.member.dto.LocationDto;
import org.springframework.transaction.annotation.Transactional;
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

    public FriendRequestApprovedResponse approveFriendRequest(Long friendRequestId, Long memberId) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendRequest.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new FriendException(FriendErrorCode.FRIEND_STATUS_INVALID);
        }

        if (!friendRequest.getReceiver().getMemberId().equals(memberId)) {
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
        Member approvedFriend = friendRequest.getSender();

        return new FriendRequestApprovedResponse(approvedFriend.getMemberId());
    }

    public void rejectFriendRequest(Long friendRequestId, Long memberId) {

        FriendRequest request = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!request.getStatus().equals(FriendRequestStatus.PENDING)) {
            throw new FriendException(FriendErrorCode.FRIEND_STATUS_INVALID);
        }

        if (!request.getReceiver().getMemberId().equals(memberId)) {
            throw new FriendException(FriendErrorCode.UNAUTHORIZED_APPROVER);
        }

        friendRequestRepository.delete(request);
    }

    @Transactional(readOnly = true)
    public List<FriendResponseDTO> getFriends(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Friend> friends = friendRepository.findAllByMember(member);

        return friends.stream().map(friend -> {
            Member friendMember = friend.getPartner();

            LocationDto locationDto = friendMember.getLocation() != null
                    ? LocationDto.builder()
                    .lat(friendMember.getLocation().getY()) // 위도 추출
                    .lng(friendMember.getLocation().getX()) // 경도 추출
                    .build()
                    : null;

            return FriendResponseDTO.of(friendMember, locationDto);
        }).toList();
    }

    public void deleteFriend(Long requesterId, Long recipientId) {
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Member recipient = memberRepository.findById(recipientId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Friend friendRequestToRecipient = friendRepository.findByMemberAndPartner(requester, recipient)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_NOT_FOUND));
        Friend friendRecipientToRequest = friendRepository.findByMemberAndPartner(recipient, requester)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_NOT_FOUND));

        friendRepository.deleteAll(List.of(friendRequestToRecipient, friendRecipientToRequest));
    }




}


