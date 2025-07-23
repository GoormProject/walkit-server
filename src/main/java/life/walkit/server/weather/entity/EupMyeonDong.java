package life.walkit.server.weather.entity;

public enum EupMyeonDong {
    // 일산동구
    GOBONG_DONG("고봉동"),
    MADU1_DONG("마두1동"),
    MADU2_DONG("마두2동"),
    BAEKSEOK1_DONG("백석1동"),
    BAEKSEOK2_DONG("백석2동"),
    SIKSA_DONG("식사동"),
    JANGHANG1_DONG("장항1동"),
    JANGHANG2_DONG("장항2동"),
    JEONGBALSAN_DONG("정발산동"),
    JUNGSAN_DONG("중산동"),
    PUNGSAN_DONG("풍산동"),

    // 일산서구
    DAEHWA_DONG("대화동"),
    SONGSAN_DONG("송산동"),
    SONGPO_DONG("송포동"),
    ILSAN1_DONG("일산1동"),
    ILSAN2_DONG("일산2동"),
    ILSAN3_DONG("일산3동"),
    JUYEOP1_DONG("주엽1동"),
    JUYEOP2_DONG("주엽2동"),
    TANHYEON_DONG("탄현동");

    private final String dong;

    EupMyeonDong(String dong) {
        this.dong = dong;
    }

    public String getDong() {
        return dong;
    }
}






