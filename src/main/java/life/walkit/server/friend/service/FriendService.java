package life.walkit.server.friend.service;

import life.walkit.server.friend.dto.FriendResponseDTO;
import life.walkit.server.friend.entity.Friend;
import life.walkit.server.member.dto.LocationDto;
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

    public void approveFriendRequest(Long friendRequestId, Long memberId) {
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
        // 주어진 memberId로 회원(Member) 정보 조회, 존재하지 않을 경우 예외 발생
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 회원과 연관된 친구 관계를 조회
        List<Friend> friends = friendRepository.findAllByMember(member);

        // FriendResponseDTO.of()를 활용하여 변환
        return friends.stream().map(friend -> {
            // 현재 회원이 속한 친구 관계에서 친구(파트너) 정보 추출
            Member friendMember = friend.getPartner();

            // 친구의 LocationDto 생성
            LocationDto locationDto = friendMember.getLocation() != null
                    ? LocationDto.builder()
                    .lat(friendMember.getLocation().getY()) // 위도 추출
                    .lng(friendMember.getLocation().getX()) // 경도 추출
                    .build()
                    : null;

            // FriendResponseDTO.of() 메서드 활용
            return FriendResponseDTO.of(friendMember, locationDto);
        }).toList();
    }
}


