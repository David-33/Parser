package com.utils;

import com.Constants;

import java.nio.charset.StandardCharsets;
import java.util.logging.*;


public final class Loggers {

    public static Logger getLogger(String name) {
        return configureLogger(Logger.getLogger(name));
    }

    private static Logger configureLogger(Logger logger) {
        logger.setUseParentHandlers(false);
        logger.setLevel(Constants.LOGGING_LEVEL);

        var formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%s] %s",
                        record.getLevel(), record.getMessage());
//                return String.format("[%s.%s] (%s) %s",
//                        record.getSourceClassName(), record.getSourceMethodName(),
//                        record.getLevel(), record.getMessage());
            }
        };

        var fileHandler = new SimpleFileHandler("log.txt", true);
        fileHandler.setLevel(Constants.LOGGING_LEVEL);
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);

        var consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Constants.LOGGING_LEVEL);
        consoleHandler.setFormatter(formatter);
        logger.addHandler(consoleHandler);

        return logger;
    }

    private static class SimpleFileHandler extends Handler {
        private final String filename;
        private final boolean append;

        private SimpleFileHandler(String filename, boolean append) {
            this.filename = filename;
            this.append = append;
        }

        @Override
        public void publish(LogRecord record) {
            if (!isLoggable(record)) {
                return;
            }
            FileUtil.writeFile(getFormatter().format(record),
                    filename, StandardCharsets.UTF_8, append);
        }

        @Override
        public void flush() {
            // nothing
        }

        @Override
        public void close() throws SecurityException {
            // nothing
        }
    }

}
