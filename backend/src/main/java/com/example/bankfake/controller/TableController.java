package com.example.bankfake.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.bankfake.model.ConnectionProperties;
import com.example.bankfake.service.DatabaseService;

@RestController
@RequestMapping("/api/{db}")
@CrossOrigin(origins = "http://localhost:3000")
public class TableController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/tables")
    public List<String> listTables(
            @PathVariable("db") String db,
            @RequestBody ConnectionProperties props) {
        return databaseService.listTables(props, db);
    }

    @PostMapping("/tables/{table}/preview")
    public List<Map<String, Object>> previewTable(
            @PathVariable("db") String db,
            @PathVariable("table") String table,
            @RequestBody ConnectionProperties props,
            @RequestParam(defaultValue = "10") int limit) {
        return databaseService.previewTable(props, db, table, limit);
    }

    @PostMapping("/tables/{table}/count")
    public long countRows(
            @PathVariable("db") String db,
            @PathVariable("table") String table,
            @RequestBody ConnectionProperties props) {
        return databaseService.countRows(props, db, table);
    }
}
