package life.walkit.server.weather.entity;

public enum Sido {
    GYEONGGI("경기도");

    private final String sido;

    Sido(String sido) {
        this.sido = sido;
    }

    public String getSido() {
        return sido;
    }
}
