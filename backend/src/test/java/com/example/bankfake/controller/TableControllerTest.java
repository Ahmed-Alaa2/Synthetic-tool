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

@WebMvcTest(TableController.class)
class TableControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DatabaseService dbService;

    @Test
    void listTablesReturnsNames() throws Exception {
        when(dbService.listTables(any(), any()))
            .thenReturn(java.util.Arrays.asList("table1", "table2"));

        mvc.perform(post("/api/db1/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"host\":\"x\",\"port\":1433,\"username\":\"u\",\"password\":\"p\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("table1"))
            .andExpect(jsonPath("$[1]").value("table2"));
    }
}