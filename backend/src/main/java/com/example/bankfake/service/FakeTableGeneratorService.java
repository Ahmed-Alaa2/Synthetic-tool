package com.example.bankfake.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.bankfake.model.FakeColumnSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

@Service
public class FakeTableGeneratorService {

    // Faker instances for fallback:
    private final Faker fakerEn = new Faker(Locale.ENGLISH);
    private final Faker fakerAr = new Faker(new Locale("ar"));

    // BTN key, injected from properties
    @Value("${behindthename.key}")
    private String btnKey;

    @Autowired
    private GeoNamesService geoService;

    // For Egyptian National ID generation:
    private static final String[] GOVERNORATES = {
        "01","02","03","04","05","06","07","08","09",
        "10","11","12","13","14","15","16","17","18",
        "19","20","21","22","23","24","25","26","27",
        "28","29","31","35","88"
    };

    public List<Map<String,Object>> generate(int rowCount,
                                             Map<String, FakeColumnSpec> specs) {
        List<Map<String,Object>> rows = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            Map<String,Object> row = new LinkedHashMap<>();
            for (var entry : specs.entrySet()) {
                row.put(entry.getKey(),
                        generateValue(entry.getValue()));
            }
            rows.add(row);
        }
        return rows;
    }

    private Object generateValue(FakeColumnSpec spec) {
        String type = spec.getType()
                          .trim()
                          .toUpperCase()
                          .replaceAll("\\s+","_");
        switch (type) {
            case "NATIONAL_ID":     return generateNationalId();
            case "ACCOUNT_NUMBER":  return fakerEn.number().digits(17);
            case "IBAN":            return "eg" + fakerEn.number().digits(24);
            case "CUSTOMER_NUMBER": return "cust" + fakerEn.number().digits(4);
            case "PHONE_NUMBER":    return generatePhoneString();
            case "EMAIL":           return fakerEn.internet().emailAddress();
            case "BALANCE":         return fakerEn.number()
                                                  .randomDouble(2, 0, 1_000_000);
            case "IS_CORPORATE_CUSTOMER":
                                    return fakerEn.bool().bool();
            case "CUSTOMER_NAME":   return randomEgyptianName();
            case "ADDRESS":         return geoService.randomAddress();
            case "CUSTOM_TEXT":     return customText(spec);
            case "CUSTOM_NUMBER":   return customNumber(spec);
            default:                return fallbackBySqlType(spec);
        }
    }

    private String randomEgyptianName() {
        // BehindTheNames random endpoint, filter by Egyptian usage "egy"
        String url = String.format(
            "https://api.behindthename.com/api/random.json?usage=egy&number=1&key=%s",
            btnKey
        );
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                                         .uri(URI.create(url))
                                         .build();
            HttpResponse<String> resp = client.send(
                req, HttpResponse.BodyHandlers.ofString());
            JsonNode root = new ObjectMapper().readTree(resp.body());
            JsonNode names = root.get("names");
            if (names != null && names.isArray() && names.size() > 0) {
                return names.get(0).asText();
            }
        } catch (Exception e) {
            System.err.println("BTN name fetch failed: " + e.getMessage());
        }
        // fallback to Faker’s Arabic name
        return fakerEn.name().firstName() + " " + fakerEn.name().lastName();
    }

    private String generatePhoneString() {
        String prefix = fakerEn.options()
                              .option("010","011","012","015");
        return prefix + fakerEn.number().digits(8);
    }

    private String generateNationalId() {
        LocalDate start = LocalDate.of(1900, 1, 1);
        LocalDate end   = LocalDate.now(ZoneId.of("UTC"));
        long randomDay = ThreadLocalRandom.current()
            .longs(start.toEpochDay(), end.toEpochDay())
            .findAny().getAsLong();
        LocalDate bd = LocalDate.ofEpochDay(randomDay);

        String yy = String.format("%02d", bd.getYear() % 100);
        String mm = String.format("%02d", bd.getMonthValue());
        String dd = String.format("%02d", bd.getDayOfMonth());
        String century = bd.getYear() >= 2000 ? "3" : "2";
        String gov     = GOVERNORATES[
            new Random().nextInt(GOVERNORATES.length)
        ];
        String serial  = fakerEn.number().digits(3);
        String gender  = String.valueOf(
            fakerEn.number().numberBetween(0, 10)
        );

        String core = century + yy + mm + dd + gov + serial + gender;
        char check = computeLuhnCheckDigit(core);
        return core + check;
    }

    private char computeLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) n = (n % 10) + 1;
            }
            sum += n;
            alternate = !alternate;
        }
        return Character.forDigit((10 - (sum % 10)) % 10, 10);
    }

     private Object customText(FakeColumnSpec spec) {
        String pre = spec.getStartsWith() != null ? spec.getStartsWith() : "";
        String suf = spec.getEndsWith()   != null ? spec.getEndsWith()   : "";

        Integer totalLen = spec.getLength();
        String core;
        if (totalLen != null) {
            // subtract prefix+suffix from total
            int coreLen = totalLen - pre.length() - suf.length();
            if (coreLen < 0) coreLen = 0;
            core = fakerEn.lorem().characters(coreLen);
        } else {
            int randomLen = fakerEn.number().numberBetween(5, 21);
            core = fakerEn.lorem().characters(randomLen);
        }

        return pre + core + suf;
    }

    private Object customNumber(FakeColumnSpec spec) {
        Integer min = spec.getMin();
        Integer max = spec.getMax();
        Integer len = spec.getLength();

        if (min != null && max != null) {
            return fakerEn.number()
                          .numberBetween(min.longValue(),
                                         max.longValue() + 1);
        }
        if (len != null) {
            return Long.parseLong(fakerEn.number().digits(len));
        }
        if (min != null) {
            return fakerEn.number()
                          .numberBetween(min.longValue(),
                                         min.longValue() + 10_000);
        }
        if (max != null) {
            return fakerEn.number()
                          .numberBetween(0, max.longValue() + 1);
        }
        return fakerEn.number().randomNumber();
    }

    private Object fallbackBySqlType(FakeColumnSpec spec) {
        String dt = spec.getDataType() != null
                    ? spec.getDataType().toLowerCase()
                    : "";
        String pre = spec.getStartsWith() != null
                     ? spec.getStartsWith() : "";
        String suf = spec.getEndsWith()   != null
                     ? spec.getEndsWith()   : "";

        if (dt.contains("int")) {
            long min = spec.getMin() != null
                       ? spec.getMin() : 0;
            long max = spec.getMax() != null
                       ? spec.getMax() : min + 9999;
            long num = fakerEn.number()
                              .numberBetween(min, max + 1);
            return pre + num + suf;
        } else {
            int minLen = spec.getMin() != null
                         ? spec.getMin() : 8;
            int maxLen = spec.getMax() != null
                         ? spec.getMax() : minLen;
            int length = fakerEn.number()
                                .numberBetween(minLen, maxLen + 1);
            String core = fakerEn.lorem().characters(length);
            return pre + core + suf;
        }
    }
}
