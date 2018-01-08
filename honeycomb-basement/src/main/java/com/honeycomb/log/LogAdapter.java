package com.honeycomb.log;

/**
 * Default implementation of {@link ILogAdapter}, just delegates loggable logic to
 * {@link ILoggableStrategy} and delegates log printing logic to {@link ILogPrinter}.
 */
public class LogAdapter implements ILogAdapter {
    private final ILoggableStrategy mLoggableStrategy;
    private final ILogPrinter mLogPrinter;

    private LogAdapter(ILoggableStrategy loggableStrategy, ILogPrinter logPrinter) {
        mLoggableStrategy = loggableStrategy;
        mLogPrinter = logPrinter;
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        return mLoggableStrategy.isLoggable(level, tag);
    }

    @Override
    public void printLog(LogLevel level, String tag, String message) {
        mLogPrinter.printLog(level, tag, message);
    }

    public static Builder buildUpon() {
        return new Builder();
    }

    public static class Builder {
        private LoggableStrategySelector mLoggableStrategySelector;
        private ILogPrinter mLogPrinter;

        private Builder() {
        }

        public Builder addLoggableStrategy(ILoggableStrategy loggableStrategy) {
            if (mLoggableStrategySelector == null) {
                mLoggableStrategySelector = new LoggableStrategySelector();
            }
            mLoggableStrategySelector.addLoggableStrategy(loggableStrategy);
            return this;
        }

        public Builder logPrinter(ILogPrinter logPrinter) {
            mLogPrinter = logPrinter;
            return this;
        }

        public LogAdapter build() {
            ILoggableStrategy loggableStrategy = mLoggableStrategySelector;
            if (loggableStrategy == null) {
                loggableStrategy = new LogcatLoggableStrategy();
            }
            if (mLogPrinter == null) {
                mLogPrinter = new LogcatLogPrinter();
            }
            return new LogAdapter(loggableStrategy, mLogPrinter);
        }
    }
}
