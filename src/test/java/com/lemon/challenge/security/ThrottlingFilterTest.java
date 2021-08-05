package com.lemon.challenge.security;

import com.lemon.challenge.TestWebConfig;
import com.lemon.challenge.external.FoassRestClientImp;
import com.lemon.challenge.service.FoaasServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@WebMvcTest
@ContextConfiguration(classes = {TestWebConfig.class})
class ThrottlingFilterTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @InjectMocks
    private FoaasServiceImp foaasService;

    @InjectMocks
    private FoassRestClientImp foassRestClientImp;

    @BeforeEach // For Junit5
    public void setup() {
        ThrottlingFilter throttlingFilter =new ThrottlingFilter();
        throttlingFilter.setMaxRequestRateLimit(5);
        throttlingFilter.setPeriodTime(10);
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(sharedHttpSession())
                .addFilter(throttlingFilter)
                .build();

    }

    @Test
    void whenCallServiceFiveTimesShouldReturnOK() throws Exception {

        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void whenCallServiceSixTimesShouldReturnToManyRequest() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message"));
        mockMvc.perform(get("/message")).andExpect(status().isTooManyRequests());

    }
}