package ragde.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InfoCtrlTest {

    private final String INVALID_TOKEN = "invalid";

    private final String VALID_TOKEN = "valid";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TokenService tokenService;

    @Before
    public void setup() throws Exception {
        given(tokenService.getLoggedUser(INVALID_TOKEN)).willThrow(new IOException());
        given(tokenService.getLoggedUser(VALID_TOKEN)).willReturn(new LoggedUser());
    }

    /**
     * Should return version when not token
     */
    @Test
    public void versionNotToken() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/version")
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "{\"version\":\"0.1\"}";

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, never()).getLoggedUser(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void versionInvalid() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/version")
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(builder)
                .andExpect(status().isUnauthorized());

        verify(tokenService, times(1)).getLoggedUser(INVALID_TOKEN);
    }

    /**
     * Should return version when not permissions
     */
    @Test
    public void versionNotPermission() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/version")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "{\"version\":\"0.1\"}";

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VALID_TOKEN);
    }

    /**
     * Should return environment when not token
     */
    @Test
    public void environmentNotToken() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/environment")
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "{\"environment\":[]}";

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, never()).getLoggedUser(any());
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void environmentInvalid() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/environment")
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(builder)
                .andExpect(status().isUnauthorized());

        verify(tokenService, times(1)).getLoggedUser(INVALID_TOKEN);
    }

    /**
     * Should return environment when not permissions
     */
    @Test
    public void environmentNotPermission() throws Exception {
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/info/environment")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON);

        final String bodyExpected = "{\"environment\":[]}";

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertNotSame(bodyExpected, bodyResult);
        assertEquals(bodyExpected, bodyResult);
        verify(tokenService, times(1)).getLoggedUser(VALID_TOKEN);
    }
}