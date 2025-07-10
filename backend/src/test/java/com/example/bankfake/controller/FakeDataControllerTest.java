package com.example.bankfake.controller;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankfake.model.FakeColumnSpec;
import com.example.bankfake.model.FakeRequest;
import com.example.bankfake.service.FakeTableGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FakeDataController.class)
class FakeDataControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FakeTableGeneratorService fakeService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void fakeGeneratesRows() throws Exception {
        // 1) Stub the new two-arg generate(int, Map<String,FakeColumnSpec>)
        when(fakeService.generate(anyInt(), anyMap()))
            .thenReturn(List.of(Map.of("col1", "val1")));

        // 2) Build a FakeRequest with rowCount + columns
        FakeColumnSpec spec = new FakeColumnSpec();
        spec.setType("EMAIL");
        FakeRequest req = new FakeRequest();
        req.setRowCount(1);
        req.setColumns(Map.of("col1", spec));

        // 3) Perform POST and verify the response
        mvc.perform(post("/api/db1/table1/fake")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].col1").value("val1"));
    }
}
