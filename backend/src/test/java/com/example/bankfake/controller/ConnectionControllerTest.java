package com.example.bankfake.controller;

import com.example.bankfake.service.DatabaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConnectionController.class)
class ConnectionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DatabaseService dbService;

    @Test
    void listDatabasesReturnsNames() throws Exception {
        // Mock the service
        when(dbService.listDatabases(any()))
            .thenReturn(java.util.Arrays.asList("db1", "db2"));

        mvc.perform(post("/api/connect/databases")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"host\":\"x\",\"port\":1433,\"username\":\"u\",\"password\":\"p\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("db1"))
            .andExpect(jsonPath("$[1]").value("db2"));
    }
}