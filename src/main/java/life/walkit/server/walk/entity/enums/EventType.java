package life.walkit.server.walk.entity.enums;

import lombok.Getter;

@Getter
public enum EventType {
    START("산책 시작"),
    PAUSE("일시정지"),
    RESUME("재개"),
    END("산책 종료");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

}
