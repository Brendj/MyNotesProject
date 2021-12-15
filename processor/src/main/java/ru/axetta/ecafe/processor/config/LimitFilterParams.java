package ru.axetta.ecafe.processor.config;

public class LimitFilterParams {
    private final int syncLimit;
    private final int syncRetryAfter;

    public LimitFilterParams(int syncLimit, int syncRetryAfter) {
        this.syncLimit = syncLimit;
        this.syncRetryAfter = syncRetryAfter;
    }

    public int getSyncLimit() {
        return syncLimit;
    }

    public int getSyncRetryAfter() {
        return syncRetryAfter;
    }
}
