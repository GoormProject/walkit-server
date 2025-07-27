package life.walkit.server.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JwtTokenRepository implements RedisKeyValueRepository<String, String>  {

    private final StringRedisTemplate stringRedisTemplate;
    private final String PREFIX = "refreshToken:";

    @Override
    public void saveWithTTL(String key, String value, Duration duration) {
        stringRedisTemplate.opsForValue().set(PREFIX + key, value, duration);
    }

    @Override
    public Optional<String> findByKey(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(PREFIX + key));
    }

    @Override
    public Boolean deleteByKey(String key) {
        return stringRedisTemplate.delete(PREFIX + key);
    }
}
