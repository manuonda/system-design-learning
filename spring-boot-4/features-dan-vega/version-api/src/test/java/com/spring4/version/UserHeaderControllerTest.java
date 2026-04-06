package com.spring4.version;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Testing API versioning can be done using Spring's MockMvc or RestTestClient.
 * Here, we will use RestTestClient to test our versioned endpoints.
 */
@SpringBootTest
public class UserHeaderControllerTest {

    private RestTestClient restTestClient;


    @BeforeEach
    void setup(){
        restTestClient = RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:8080")
                .build();
    }


    @Test
    void givenUser_whenGetUsers_thenReturnV1() {
        // Given
        String version = "1.0";


    }

}
