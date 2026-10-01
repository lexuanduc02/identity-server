package com.daniel.identity_service.configuration.security;

import com.daniel.identity_service.configuration.security.properties.SecurityWhitelistProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityWhitelistProperties whitelist;
    private final CustomJwtDecoder customJwtDecoder;

    /**
     * CHAIN 1 (@Order(1)): Xử lý toàn bộ endpoint nằm trong Whitelist (từ file YAML).
     * Tuyệt đối KHÔNG gắn oauth2ResourceServer ở đây để:
     * - Bỏ qua hoàn toàn bộ lọc Bearer token.
     * - Gửi token rác, token hết hạn vào endpoint public vẫn thành công bình thường.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain publicFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatchers(matchers -> {
                    if (!whitelist.getPost().isEmpty()) {
                        matchers.requestMatchers(HttpMethod.POST, whitelist.getPost().toArray(String[]::new));
                    }
                    if (!whitelist.getGet().isEmpty()) {
                        matchers.requestMatchers(HttpMethod.GET, whitelist.getGet().toArray(String[]::new));
                    }
                    if (!whitelist.getAny().isEmpty()) {
                        matchers.requestMatchers(whitelist.getAny().toArray(String[]::new));
                    }
                })
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    /**
     * CHAIN 2 (@Order(2)): Mặc định bảo vệ tất cả endpoint còn lại.
     * Bắt buộc phải có Bearer Token hợp lệ, nếu không trả về 401 qua JwtAuthenticationEntryPoint.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain protectedFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}