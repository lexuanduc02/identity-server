package com.daniel.identity_service.configuration.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.whitelist")
@Getter
@Setter
public class SecurityWhitelistProperties {
    private List<String> post = new ArrayList<>();
    private List<String> get = new ArrayList<>();
    private List<String> any = new ArrayList<>();
}
