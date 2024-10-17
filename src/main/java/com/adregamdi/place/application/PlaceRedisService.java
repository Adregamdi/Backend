package com.adregamdi.place.application;

import com.adregamdi.core.redis.exception.RedisException;
import com.adregamdi.place.dto.PopularPlaceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceRedisService {
    private static final String POPULAR_PLACES_KEY = "popular:places";
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void savePopularPlaces(List<PopularPlaceDTO> popularPlaces) {
        try {
            redisTemplate.delete(POPULAR_PLACES_KEY);
            for (PopularPlaceDTO place : popularPlaces) {
                redisTemplate.opsForZSet().add(POPULAR_PLACES_KEY,
                        objectMapper.writeValueAsString(place),
                        place.place().getAddCount());
            }
            redisTemplate.expire(POPULAR_PLACES_KEY, Duration.ofHours(1));
        } catch (JsonProcessingException e) {
            log.error("인기 장소 저장 중 JSON 직렬화 오류", e);
            throw new RedisException.RedisDataSerializationException();
        } catch (RedisConnectionFailureException e) {
            log.error("인기 장소 저장 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (Exception e) {
            log.error("인기 장소 저장 중 예기치 않은 오류", e);
            throw new RedisException.RedisCommandExecutionException();
        }
    }

    public List<PopularPlaceDTO> getPopularPlaces() {
        try {
            Set<String> cachedPlaces = redisTemplate.opsForZSet().reverseRange(POPULAR_PLACES_KEY, 0, -1);
            if (cachedPlaces == null || cachedPlaces.isEmpty()) {
                return null;
            }
            return cachedPlaces.stream()
                    .map(place -> {
                        try {
                            return objectMapper.readValue(place, PopularPlaceDTO.class);
                        } catch (JsonProcessingException e) {
                            log.error("인기 장소 역직렬화 중 오류", e);
                            throw new RedisException.RedisDataSerializationException();
                        }
                    })
                    .collect(Collectors.toList());
        } catch (RedisConnectionFailureException e) {
            log.error("인기 장소 조회 중 Redis 연결 실패", e);
            throw new RedisException.RedisConnectionFailureException();
        } catch (Exception e) {
            log.error("인기 장소 조회 중 예기치 않은 오류", e);
            throw new RedisException.RedisCommandExecutionException();
        }
    }
}
