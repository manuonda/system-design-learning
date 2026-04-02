package com.spring4.version;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.ApiVersionInserter;

@SpringBootTest
public class UserApiVersionTest {

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:8080" + "8080")
                .apiVersionInserter(ApiVersionInserter.usePathSegment(1))
                .build();
    }

    @Test
    void givenUser_whenGetUsers_thenReturnV1() {
        // Given
        String version = "1.0";

        // When
        var response =  restTestClient
                .get()
                .uri("/api/{version}/users", "v1")
                .apiVersion(version)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Dan Vega")
                .jsonPath("$[0].email").isEqualTo("danvega@gmail.com")
                .jsonPath("$[0].webSite").isEqualTo("https://www.danvega.com")
                .jsonPath("$[0].firstName").doesNotExist();


        // Then
        var users = response.returnResult().getResponseBody();
        assert users != null;
        assert users.size() == 2;
        assert users.get(0).name().equals("John Doe");
    }
}
