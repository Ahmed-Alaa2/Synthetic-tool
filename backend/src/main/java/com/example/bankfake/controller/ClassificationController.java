package com.example.bankfake.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankfake.model.ClassificationRequest;
import com.example.bankfake.model.ColumnConfig;
import com.example.bankfake.model.ColumnSuggestion;
import com.example.bankfake.service.ClassificationService;
import com.example.bankfake.service.DatabaseService;

@RestController
@RequestMapping("/api/{db}/{table}")
@CrossOrigin(origins = "http://localhost:3000")
public class ClassificationController {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private DatabaseService databaseService;

    /** (unchanged) your existing classify endpoint */
    @PostMapping("/classify")
    public List<ColumnSuggestion> classify(
            @PathVariable String db,
            @PathVariable String table,
            @RequestBody ClassificationRequest req
    ) {
        return classificationService.classify(
            req.getConnection(), db, table,
            req.getSampleSize(), req.getOverrides()
        );
    }

    /**
     * NEW: merges raw SQL column names, their SQL data types, and
     *       your initial suggestion into one payload for the front-end.
     */
    @PostMapping("/configure")
    public List<ColumnConfig> configure(
            @PathVariable String db,
            @PathVariable String table,
            @RequestBody ClassificationRequest req
    ) {
        // 1) get the suggestions
        List<ColumnSuggestion> suggestions =
            classificationService.classify(
                req.getConnection(), db, table,
                req.getSampleSize(), req.getOverrides()
            );

        // 2) get the SQL data types
        List<Map<String,String>> types =
            databaseService.listColumnTypes(req.getConnection(), db, table);

        // 3) zip them by columnName
        return suggestions.stream()
            .map(s -> {
                String dt = types.stream()
                        .filter(m -> m.get("columnName").equals(s.getColumnName()))
                        .findFirst()
                        .map(m -> m.get("dataType"))
                        .orElse("");
                return new ColumnConfig(
                    s.getColumnName(),
                    dt,
                    s.getSuggestion()
                );
            })
            .collect(Collectors.toList());
    }
}
