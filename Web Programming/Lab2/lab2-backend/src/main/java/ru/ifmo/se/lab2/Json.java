package ru.ifmo.se.lab2;

import java.util.List;

public class Json {

    // the whole history as one JSON object from the poor man's database
    public static String results(List<Result> results) {
        StringBuilder sb = new StringBuilder("{\"results\":[");
        for (int i = 0; i < results.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            Result res = results.get(i);
            sb.append("{\"x\":\"").append(res.point().x().toPlainString()).append('"')
                    .append(",\"y\":\"").append(res.point().y().toPlainString()).append('"')
                    .append(",\"r\":\"").append(res.point().r().toPlainString()).append('"')
                    .append(",\"hit\":").append(res.hit())
                    .append(",\"time\":").append(res.time())
                    .append(",\"execTimeNs\":").append(res.execTimeNs())
                    .append('}');
        }
        return sb.append("]}").toString();
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    // quotes, backslashes and control characters would break a JSON string, so they get escaped
    private static String escape(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}