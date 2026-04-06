package com.spring4.version;


import com.spring4.version.config.ApiVersionParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.mock.mockito.MockitoBean;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for header-based API versioning strategy.
 *
 * Strategy: useRequestHeader("X-API-Version")
 * URL pattern: /header/users (version goes in header, not in path)
 *
 * Examples:
 *   GET /header/users  X-API-Version: 1.0  → UserDTOv1
 *   GET /header/users  X-API-Version: 1.1  → UserDTOv1
 *   GET /header/users  X-API-Version: 2.0  → UserDTOv2
 *   GET /header/users  X-API-Version: 9.9  → 400 BAD_REQUEST
 */
@WebMvcTest(
        controllers = UserHeaderController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.spring4.version.config.WebConfig.class
        )
)
@Import(UserHeaderControllerTest.HeaderVersioningConfig.class)
public class UserHeaderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserMapper userMapper;

    /** Test-only WebMvcConfigurer that activates the request-header strategy. */
    @TestConfiguration
    static class HeaderVersioningConfig implements WebMvcConfigurer {
        @Override
        public void configureApiVersioning(ApiVersionConfigurer configurer) {
            configurer
                    .addSupportedVersions("1.0", "1.1", "2.0")
                    .setDefaultVersion("1.0")
                    .setVersionParser(new ApiVersionParser())
                    .useRequestHeader("X-API-Version");
        }
    }

    @Test
    @DisplayName("X-API-Version: 1.0 → returns UserDTOv1 with webSite")
    void givenUser_whenGetUsersV1_thenReturnUserDTOv1() throws Exception {
        var user = new User(1, "Dan Vega", "danvega@gmail.com", "https://www.danvega.com");
        var dto  = new UserDTOv1(1, "Dan Vega", "danvega@gmail.com", "https://www.danvega.com");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toV1(user)).thenReturn(dto);

        mockMvc.perform(get("/header/users")
                        .header("X-API-Version", "1.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dan Vega"))
                .andExpect(jsonPath("$[0].email").value("danvega@gmail.com"))
                .andExpect(jsonPath("$[0].webSite").value("https://www.danvega.com"))
                .andExpect(jsonPath("$[0].lastName").doesNotExist());
    }

    @Test
    @DisplayName("X-API-Version: 1.1 → returns same UserDTOv1 fields")
    void givenUser_whenGetUsersV11_thenReturnUserDTOv1() throws Exception {
        var user = new User(1, "Dan Vega", "danvega@gmail.com", "https://www.danvega.com");
        var dto  = new UserDTOv1(1, "Dan Vega", "danvega@gmail.com", "https://www.danvega.com");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toV1(user)).thenReturn(dto);

        mockMvc.perform(get("/header/users")
                        .header("X-API-Version", "1.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dan Vega"))
                .andExpect(jsonPath("$[0].email").value("danvega@gmail.com"))
                .andExpect(jsonPath("$[0].webSite").value("https://www.danvega.com"))
                .andExpect(jsonPath("$[0].lastName").doesNotExist());
    }

    @Test
    @DisplayName("X-API-Version: 2.0 → returns UserDTOv2 with lastName, no webSite")
    void givenUser_whenGetUsersV2_thenReturnUserDTOv2() throws Exception {
        var user = new User(1, "Dan Vega", "danvega@gmail.com", "https://www.danvega.com");
        var dto  = new UserDTOv2(1, "Dan", "danvega@gmail.com", "Vega");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toV2(user)).thenReturn(dto);

        mockMvc.perform(get("/header/users")
                        .header("X-API-Version", "2.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dan"))
                .andExpect(jsonPath("$[0].email").value("danvega@gmail.com"))
                .andExpect(jsonPath("$[0].lastName").value("Vega"))
                .andExpect(jsonPath("$[0].webSite").doesNotExist());
    }

    @Test
    @DisplayName("X-API-Version: v2 (prefix) → ApiVersionParser normalizes to 2.0, returns UserDTOv2")
    void givenUser_whenGetUsersV2WithPrefix_thenReturnUserDTOv2() throws Exception {
        var user = new User(1, "Dan Vega", "danvega@gmail.com", "https://www.danvega.com");
        var dto  = new UserDTOv2(1, "Dan", "danvega@gmail.com", "Vega");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toV2(user)).thenReturn(dto);

        mockMvc.perform(get("/header/users")
                        .header("X-API-Version", "v2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Vega"));
    }

    @Test
    @DisplayName("X-API-Version: 9.9 (unsupported) → 400 BAD_REQUEST")
    void givenUnsupportedVersion_whenGetUsers_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/header/users")
                        .header("X-API-Version", "9.9"))
                .andExpect(status().isBadRequest());
    }
}
