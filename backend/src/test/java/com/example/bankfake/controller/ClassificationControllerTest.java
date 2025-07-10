package com.example.bankfake.controller;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankfake.model.ClassificationRequest;
import com.example.bankfake.model.ColumnSuggestion;
import com.example.bankfake.model.ConnectionProperties;
import com.example.bankfake.service.ClassificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClassificationController.class)
class ClassificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClassificationService classificationService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void classifyReturnsSuggestions() throws Exception {
        // Stub the service to return one ColumnSuggestion
        ColumnSuggestion cs = new ColumnSuggestion("col1", "EMAIL");
        when(classificationService.classify(
                any(ConnectionProperties.class),
                anyString(),
                anyString(),
                anyInt(),
                anyMap()))
            .thenReturn(List.of(cs));

        // Build a dummy ConnectionProperties
        ConnectionProperties conn = new ConnectionProperties();
        conn.setHost("localhost");
        conn.setPort(1433);
        conn.setUsername("sa");
        conn.setPassword("pass");

        // Build and populate the request
        ClassificationRequest req = new ClassificationRequest();
        req.setConnection(conn);
        req.setSampleSize(10);
        req.setOverrides(Map.of());

        // Execute the POST and verify JSON response
        mvc.perform(post("/api/db1/table1/classify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].columnName").value("col1"))
           .andExpect(jsonPath("$[0].suggestion").value("EMAIL"));
    }
}
