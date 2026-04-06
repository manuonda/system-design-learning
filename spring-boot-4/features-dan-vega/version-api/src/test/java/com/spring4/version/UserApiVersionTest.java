package com.spring4.version;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.ApiVersionInserter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiVersionTest {

    @LocalServerPort
    private int port;
    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:"+port)
                .apiVersionInserter(ApiVersionInserter.usePathSegment(1))
                .build();
    }

    @Test
    void givenUser_whenGetUsers_thenReturnV1() {
        // Given
        String version = "1.0";

        // When / Then
        restTestClient
                .get()
                .uri("/api/users")
                .apiVersion(version)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Dan Vega")
                .jsonPath("$[0].email").isEqualTo("danvega@gmail.com")
                .jsonPath("$[0].webSite").isEqualTo("https://www.danvega.com")
                .jsonPath("$[0].firstName").doesNotExist();
    }

    @Test
    void giveUserV11_whenGetUsers_thenReturnV11() {
        String version  = "1.1";

        //when then
        restTestClient
                .get()
                .uri("/api/users",version)
                .apiVersion(version)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Dan Vega")
                .jsonPath("$[0].email").isEqualTo("danvega@gmail.com");
    }

    @Test
    @DisplayName("Given Null when version is 1.2")
    void givenNull_whenGetUsers_thenReturnNotFound() {
        String version  = "1.2";
        restTestClient
                .get()
                .uri("/api/users")
                .apiVersion(version)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

    }
}
