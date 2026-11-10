package lab3.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Result implements Serializable {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final BigDecimal x;
    private final BigDecimal y;
    private final BigDecimal r;
    private final boolean hit;
    private final LocalDateTime time;

    public Result(BigDecimal x, BigDecimal y, BigDecimal r, boolean hit) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.time = LocalDateTime.now();
    }

    public BigDecimal getX() {
        return this.x;
    }
    public BigDecimal getY() {
        return this.y;
    }
    public BigDecimal getR() {
        return this.r;
    }
    public boolean isHit() {
        return this.hit;
    }
    public String getTime() {
        return time.format(FORMAT);
    }
}
