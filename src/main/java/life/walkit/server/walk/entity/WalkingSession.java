package life.walkit.server.walk.entity;

import jakarta.persistence.*;
import life.walkit.server.walk.entity.enums.EventType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "walking_session")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "walk_id",
        nullable = false
    )
    private Walk walk;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "event_type",
        nullable = false
    )
    private EventType eventType;

    @Column(
        name = "event_time",
        nullable = false
    )
    private LocalDateTime eventTime;

    @Builder
    public WalkingSession(
        Walk walk,
        EventType eventType
    ) {
        this.walk = walk;
        this.eventType = eventType;
        this.eventTime = LocalDateTime.now();
    }

    public void updateWalkingSessionEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
