package org.burgas.trainingservice.redis;

import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.dto.project.ProjectResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, IdentityResponse> identityRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, IdentityResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(IdentityResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, CourseResponse> courseRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, CourseResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(CourseResponse.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, ProjectResponse> projectRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ProjectResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JacksonJsonRedisSerializer<>(ProjectResponse.class));
        return template;
    }
}
