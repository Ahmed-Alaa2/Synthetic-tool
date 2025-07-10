package com.example.bankfake.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;    // ← add this
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankfake.model.ConnectionProperties;
import com.example.bankfake.service.DatabaseService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")    // ← allow your React dev server
public class ConnectionController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/connect/databases")
    public List<String> listDatabases(@RequestBody ConnectionProperties props) {
        return databaseService.listDatabases(props);
    }
}
