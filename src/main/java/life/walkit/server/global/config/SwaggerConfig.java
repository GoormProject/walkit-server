package life.walkit.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("cookieAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name("ACCESS_TOKEN") // 쿠키 이름
                        )
                )
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Walkit API")
                .description("소셜 위치 기반 산책 플랫폼 Walkit의 API 명세서입니다.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Walkit Team")
                        .url("https://walkit.life"));
    }


}
