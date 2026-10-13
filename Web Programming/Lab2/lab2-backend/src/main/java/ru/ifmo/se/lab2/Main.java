package ru.ifmo.se.lab2;

import com.fastcgi.FCGIInterface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class is the starting point, listens to the given socket as per the FastCGI documentation dictates
 */
public class Main {
    // every result since the server started, kept in memory between requests (poor man's database)
    private static final List<Result> HISTORY = new ArrayList<>();

    public static void main(String[] args) {
        FCGIInterface fcgi = new FCGIInterface();

        while (fcgi.FCGIaccept() >= 0) {
            Properties request = FCGIInterface.request.params;
            try {
                handle(request);
            } catch (BadRequestException e) {
                sendJson(400, "Bad Request", Json.error(e.getMessage()));
            } catch (Exception e) {
                // anything unexpected must not kill the server
                e.printStackTrace();
                sendJson(500, "Internal Server Error", Json.error("Internal server error"));
            }
        }
    }

    private static void handle(Properties request) throws BadRequestException {
        long start = System.nanoTime();
        String query = request.getProperty("QUERY_STRING");

        // no parameters means the page just wants the history
        if (query == null || query.isEmpty()) {
            sendJson(200, "OK", Json.results(HISTORY));
            return;
        }

        Point point = Validator.validate(QueryParser.parse(query));
        boolean hit = AreaChecker.isHit(point);
        long execTimeNs = System.nanoTime() - start;

        HISTORY.add(new Result(point, hit, System.currentTimeMillis(), execTimeNs));
        sendJson(200, "OK", Json.results(HISTORY));
    }

    private static void sendJson(int code, String reason, String json) {
        sendResponse(code, reason, "application/json; charset=utf-8", json);
    }

    // write a complete HTTP response, because of -nph (nph is the flag that forces httpd to not parse fastcgi)
    private static void sendResponse(int code, String reason, String contentType, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String head = "HTTP/1.1 " + code + " " + reason + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + bodyBytes.length + "\r\n"
                + "Cache-Control: no-store\r\n"
                + "\r\n";
        byte[] headBytes = head.getBytes(StandardCharsets.US_ASCII);

        System.out.write(headBytes, 0, headBytes.length);
        System.out.write(bodyBytes, 0, bodyBytes.length);
        System.out.flush();
    }
}