package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InfoCtrlIntegrationTest {

    private final String BASE_URL = "/info";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should get application.properties api-version
     */
    @Test
    public void version() throws Exception {
        final String VERSION = "0.1";
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(BASE_URL + "/version")
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);

        assertNotSame(VERSION, mapResult.get("version"));
        assertEquals(VERSION, mapResult.get("version"));
    }

    /**
     * Should get an empty list
     */
    @Test
    public void environment() throws Exception {
        final List<String> ENVIRONMENT = Collections.emptyList();
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(BASE_URL + "/environment")
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);

        assertNotSame(ENVIRONMENT, mapResult.get("environment"));
        assertEquals(ENVIRONMENT, mapResult.get("environment"));
    }
}