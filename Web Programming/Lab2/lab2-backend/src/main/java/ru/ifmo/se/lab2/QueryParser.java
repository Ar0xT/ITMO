package ru.ifmo.se.lab2;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParser {

    // splits the query string into names and their values
    public static Map<String, List<String>> parse(String query) throws BadRequestException {
        Map<String, List<String>> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        for (String pair : query.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int eq = pair.indexOf('=');
            String name = eq >= 0 ? pair.substring(0, eq) : pair;
            String value = eq >= 0 ? pair.substring(eq + 1) : "";
            try {
                name = URLDecoder.decode(name, StandardCharsets.UTF_8);
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("The query string is malformed");
            }
            // a name can appear more than once, so each name keeps a list of values
            params.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        }
        return params;
    }
}