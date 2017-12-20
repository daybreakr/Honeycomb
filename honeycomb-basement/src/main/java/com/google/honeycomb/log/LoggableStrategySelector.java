package com.google.honeycomb.log;

import java.util.ArrayList;
import java.util.List;

public class LoggableStrategySelector implements ILoggableStrategy {
    private List<ILoggableStrategy> mStrategies = new ArrayList<>();

    void addLoggableStrategy(ILoggableStrategy loggableStrategy) {
        if (!mStrategies.contains(loggableStrategy)) {
            mStrategies.add(loggableStrategy);
        }
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        for (ILoggableStrategy strategy : mStrategies) {
            if (strategy.isLoggable(level, tag)) {
                return true;
            }
        }
        return false;
    }
}
