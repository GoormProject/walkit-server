package life.walkit.server.weather.entity;

public enum EupMyeonDong {
    // 일산동구
    MAERANG_DONG("마두동"),
    JEONGBAL_DONG("정발산동"),
    BAEKBOK_DONG("백석동"),
    PUNG_DONG("풍동"),
    JANGHANG_DONG("장항동"),
    SANGSAN_DONG("식사동"),
    MUNBONG_DONG("문봉동"),

    // 일산서구
    ILSAN_DONG("일산동"),
    ILSANSEO_DONG(  "대화동"),
    JUYOP_DONG( "주엽동"),
    TANHYUN_DONG( "탄현동"),
    HOEGOK_DONG( "후곡동");

    private final String dong;

    EupMyeonDong(String dong) {
        this.dong = dong;
    }

    public String getDong() {
        return dong;
    }
}






