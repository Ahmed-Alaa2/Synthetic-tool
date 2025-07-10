package com.example.bankfake.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankfake.model.FakeRequest;
import com.example.bankfake.model.UploadRequest;
import com.example.bankfake.service.DatabaseService;
import com.example.bankfake.service.FakeTableGeneratorService;

@RestController
@RequestMapping("/api/{db}/{table}")
@CrossOrigin(origins = "http://localhost:3000")
public class FakeDataController {

    @Autowired
    private FakeTableGeneratorService fakeService;

    @Autowired
    private DatabaseService databaseService;

    /**
     * Generate fake rows in memory.
     *
     * POST /api/{db}/{table}/fake
     * {
     *   "rowCount": 123,
     *   "columns": { "col1": { …spec… }, … }
     * }
     */
    @PostMapping("/fake")
    public List<Map<String, Object>> generateFake(
            @PathVariable String db,
            @PathVariable String table,
            @RequestBody FakeRequest req) {

        return fakeService.generate(
            req.getRowCount(),
            req.getColumns()
        );
    }

    /**
     * Persist generated rows into a new table "<originalTable>Fake".
     *
     * POST /api/{db}/{table}/upload
     * {
     *   "connection": { …your ConnectionProperties… },
     *   "rows": [
     *     { "col1": val1, "col2": val2, … },
     *     …
     *   ]
     * }
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFake(
            @PathVariable String db,
            @PathVariable String table,
            @RequestBody UploadRequest req) {

        databaseService.saveFakeTable(
            req.getConnection(),
            db,
            table,
            req.getRows()
        );

        return ResponseEntity
                .ok(Map.of("message", "Upload successful"));
    }
}
