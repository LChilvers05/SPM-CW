package com.example.spmapp.Models;

/**
 * A collection of one days sleep to be presented on a chart
 */
public class ChartSession {

    private final long start;
    private final long end;

    public ChartSession(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }
    public long getEnd() {
        return end;
    }
    public long getDuration() {
        return end - start;
    }
}
