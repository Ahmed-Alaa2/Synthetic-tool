package com.example.bankfake.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bankfake.model.ColumnSuggestion;
import com.example.bankfake.model.ConnectionProperties;
import com.example.bankfake.model.FakeColumnSpec;

@Service
public class ClassificationService {

    // Address‐detection keywords (English + Arabic)
    private static final String[] ADDRESS_KEYWORDS = {
        "street", "\\bst\\b", "avenue", "\\bave\\b",
        "road", "\\brd\\b", "boulevard", "\\bblvd\\b",
        "drive", "\\bdr\\b", "lane", "\\bln\\b",
        "building", "\\bbldg\\b", "floor", "\\bfl\\b",
        "p\\.o\\. box", "po box",
        "شارع", "حارة", "عمارة", "مجمع", "ميدان", "رمز\\s*بريدي"
    };
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
        String.join("|", ADDRESS_KEYWORDS),
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private static final Pattern NATIONAL_ID_PATTERN     =
        Pattern.compile("^[23]\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{7}$");
    private static final Pattern ACCOUNT_NUMBER_PATTERN  = Pattern.compile("^\\d{17}$");
    private static final Pattern IBAN_PATTERN            = Pattern.compile("^EG\\d{27}$");
    private static final Pattern CUSTOMER_NUMBER_PATTERN = Pattern.compile("^cust\\d{4}$");

    /**
     * PHONE_PATTERN now matches:
     *  - Local:      01[0,1,2,5] + 8 digits
     *  - Intl w/+:   +20[0?1[0,1,2,5]] + 8 digits
     *  - Intl w/o+:  20[0?1[0,1,2,5]] + 8 digits
     *
     * That covers:
     *   01012345678, (+201012345678), 201012345678,
     *   2001012345678, +2001012345678, etc.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(?(?:\\+?20(?:0?1[0125])\\d{8}|01[0125]\\d{8})\\)?$"
    );

    private static final Pattern EMAIL_PATTERN           =
        Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern BALANCE_PATTERN         =
        Pattern.compile("^\\d+\\.\\d+$");
    private static final Pattern NAME_PATTERN            =
        Pattern.compile("^[A-Za-z\u0600-\u06FF]+\\s+[A-Za-z\u0600-\u06FF]+$");

    @Autowired
    private DatabaseService databaseService;

    /**
     * Inspect up to sampleSize rows from the given table, extract the raw SQL column names
     * (col1, col2, …) from the first row’s keySet, and infer a suggestion per column
     * purely based on the sampled data values.
     */
    public List<ColumnSuggestion> classify(
            ConnectionProperties props,
            String db,
            String table,
            int sampleSize,
            Map<String, FakeColumnSpec> overrides
    ) {
        List<Map<String,Object>> rows =
            databaseService.previewTable(props, db, table, sampleSize);
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> rawCols = new ArrayList<>(rows.get(0).keySet());
        List<ColumnSuggestion> suggestions = new ArrayList<>(rawCols.size());

        for (String col : rawCols) {
            if (overrides != null && overrides.containsKey(col)) {
                suggestions.add(new ColumnSuggestion(col, overrides.get(col).getType()));
                continue;
            }

            List<String> samples = rows.stream()
                .map(r -> r.get(col) == null ? "" : r.get(col).toString())
                .collect(Collectors.toList());

            suggestions.add(new ColumnSuggestion(col, detectType(samples)));
        }

        return suggestions;
    }

    /**
     * Apply your pattern‐based rules in priority order to infer a type from the samples.
     */
    private String detectType(List<String> samples) {
        if (samples.stream().anyMatch(s -> ADDRESS_PATTERN.matcher(s).find())) {
            return "ADDRESS";
        }
        if (samples.stream().allMatch(v -> NATIONAL_ID_PATTERN.matcher(v).matches())) {
            return "NATIONAL_ID";
        }
        if (samples.stream().allMatch(v -> ACCOUNT_NUMBER_PATTERN.matcher(v).matches())) {
            return "ACCOUNT_NUMBER";
        }
        if (samples.stream().allMatch(v -> IBAN_PATTERN.matcher(v).matches())) {
            return "IBAN";
        }
        if (samples.stream().allMatch(v -> CUSTOMER_NUMBER_PATTERN.matcher(v).matches())) {
            return "CUSTOMER_NUMBER";
        }
        if (samples.stream().allMatch(v -> PHONE_PATTERN.matcher(v).matches())) {
            return "PHONE_NUMBER";
        }
        if (samples.stream().allMatch(v -> EMAIL_PATTERN.matcher(v).matches())) {
            return "EMAIL";
        }
        if (samples.stream().allMatch(v -> BALANCE_PATTERN.matcher(v).matches())) {
            return "BALANCE";
        }
        if (samples.stream().allMatch(v ->
            v.equalsIgnoreCase("true") ||
            v.equalsIgnoreCase("false") ||
            v.equals("0") ||
            v.equals("1")
        )) {
            return "IS_CORPORATE_CUSTOMER";
        }
        if (samples.stream().allMatch(v -> NAME_PATTERN.matcher(v).matches())) {
            return "CUSTOMER_NAME";
        }
        return "UNKNOWN";
    }
}
