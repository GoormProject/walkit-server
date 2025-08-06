package life.walkit.server.weather.entity;

public enum Sigungu {
    GOYANG_DONGGU("고양시", "일산동구"),
    GOYANG_SEOGU("고양시", "일산서구"),
    GOYANG_DEOGYANGGU("고양시", "덕양구");

    private final String city;
    private final String district;

    Sigungu(String city, String district) {
        this.city = city;
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }
}
