package life.walkit.server.friend.service;

import life.walkit.server.auth.repository.LastActiveRepository;
import life.walkit.server.friend.dto.*;
import life.walkit.server.friend.entity.Friend;
import life.walkit.server.member.dto.LocationDto;
import life.walkit.server.member.entity.ProfileImage;
import life.walkit.server.member.entity.enums.MemberStatus;
import life.walkit.server.member.service.MemberService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final LastActiveRepository lastActiveRepository;
    private final MemberService memberService;

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
    public FriendListResponseDTO getFriends(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Friend> friends = friendRepository.findAllByMember(member);

        int onlineCount = 0;
        int offlineCount = 0;

        List<FriendResponseDTO> onlineFriends = new ArrayList<>();
        List<FriendResponseDTO> offlineFriends = new ArrayList<>();

        // 친구의 상태를 갱신하고 FriendResponseDTO를 생성
        for (Friend friend : friends) {
            Member partner = friend.getPartner();
            MemberStatus updatedStatus = memberService.refreshMemberStatus(partner.getMemberId()); // 친구 상태 갱신
            partner.updateStatus(updatedStatus); // 상태 갱신 후 현재 객체에도 반영

            FriendResponseDTO friendDTO = FriendResponseDTO.of(partner);

            if (partner.getStatus() == MemberStatus.ONLINE || partner.getStatus() == MemberStatus.WALKING) {
                onlineFriends.add(friendDTO);
                onlineCount++;
            } else {
                offlineFriends.add(friendDTO);
                offlineCount++;
            }
        }

        return FriendListResponseDTO.builder()
                .total(onlineCount + offlineCount)
                .online(onlineCount)
                .offline(offlineCount)
                .onlineFriends(onlineFriends)
                .offlineFriends(offlineFriends)
                .build();
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

    @Transactional(readOnly = true)
    public List<FriendResponseDTO> getFriendsByStatus(Long memberId, MemberStatus status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Friend> friends = friendRepository.findAllByMember(member);

        return friends.stream()
                .map(Friend::getPartner)
                .map(partner -> {
                   // 각 친구의 상태 갱신
                    MemberStatus updatedStatus = memberService.refreshMemberStatus(partner.getMemberId());
                    partner.updateStatus(updatedStatus);
                    return partner;
                })
                .filter(partner -> {
                    // 상태별로 필터링: WALKING/ONLINE → ONLINE, OFFLINE → OFFLINE
                    if (status == MemberStatus.ONLINE) {
                        return partner.getStatus() == MemberStatus.ONLINE || partner.getStatus() == MemberStatus.WALKING;
                    } else if (status == MemberStatus.OFFLINE) {
                        return partner.getStatus() == MemberStatus.OFFLINE;
                    }
                    return false;
                })
                .map(FriendResponseDTO::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FriendLocationResponseDTO> getFriendLocations(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Friend> friends = friendRepository.findAllByMember(member);

        // 산책 중인 친구(WALKING 상태)의 위치만 필터링하여 반환
        return friends.stream()
                .map(Friend::getPartner)
                .map(partner -> {
                    // 각 친구의 상태 갱신
                    MemberStatus updatedStatus = memberService.refreshMemberStatus(partner.getMemberId());
                    partner.updateStatus(updatedStatus);
                    return partner;
                })
                .filter(partner -> partner.getStatus() == MemberStatus.WALKING)
                .map(partner -> {
                    // Friend 엔티티의 ID를 찾기 위해 Friend 객체 참조 필요
                    Friend friendEntity = friends.stream()
                            .findFirst()
                            .orElse(null);

                    return FriendLocationResponseDTO.builder()
                            .friendId(friendEntity != null ? friendEntity.getFriendId() : null)
                            .memberId(partner.getMemberId())
                            .nickname(partner.getNickname())
                            .profile(Optional.ofNullable(partner.getProfileImage())
                                    .map(ProfileImage::getProfileImage)
                                    .orElse(""))
                            .location(Optional.ofNullable(partner.getLocation())
                                    .map(loc -> LocationDto.builder()
                                            .lng(loc.getX())
                                            .lat(loc.getY())
                                            .build())
                                    .orElse(null))
                            .build();
                })
                .toList();
    }
}


