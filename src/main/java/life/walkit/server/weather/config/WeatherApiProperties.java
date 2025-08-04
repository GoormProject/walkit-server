package life.walkit.server.weather.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherApiProperties {
    private String serviceKey;
    private String ultraSrtNcstUrl;
    private String ultraSrtFcstUrl;
    private String vilageFcstUrl;

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public void setUltraSrtNcstUrl(String ultraSrtNcstUrl) {
        this.ultraSrtNcstUrl = ultraSrtNcstUrl;
    }

    public void setUltraSrtFcstUrl(String ultraSrtFcstUrl) {
        this.ultraSrtFcstUrl = ultraSrtFcstUrl;
    }

    public void setVilageFcstUrl(String vilageFcstUrl) {
        this.vilageFcstUrl = vilageFcstUrl;
    }
}
