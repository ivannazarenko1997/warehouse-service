package com.mycompany.demo.config;


import org.apache.catalina.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = SecurityConfigTest.TestConfig.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;


    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({
            SecurityConfig.class,
            OpenApiTestController.class,
            SecuredTestController.class
    })
    static class TestConfig {
    }

    @RestController
    static class OpenApiTestController {
        @GetMapping("/v1/api/alarms")
        public String openAlarms() {
            return "open-alarms";
        }
    }

    @RestController
    static class SecuredTestController {
        @GetMapping("/secure/test")
        public String secureGet() {
            return "secure-get";
        }

        @PostMapping("/secure/test")
        public String securePost() {
            return "secure-post";
        }
    }

    @Test

    void securedPath_shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/secure/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test-user")

    void securedPath_shouldReturn200WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/secure/test"))
                .andExpect(status().isOk());
    }


}
