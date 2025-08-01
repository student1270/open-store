package ru.gb;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = JUnitSpringBootBase.TestSecurityConfiguration.class)
@AutoConfigureWebTestClient
public abstract class JUnitSpringBootBase {

    @TestConfiguration
    static class TestSecurityConfiguration {

//    @Bean
//    SecurityFilterChain testSecurityFilterChain(HttpSecurity security) throws Exception {
//      return security.authorizedHttpRequests(registry -> registry
//        .anyRequest().permitAll()
//      ).build;
//    }

    }

}