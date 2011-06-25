package com.esotericsoftware.minlog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * A low overhead, lightweight logging system.
 *
 * @author Nathan Sweet <misc@n4te.com>
 * @version $Id$
 */
public class Log {
    /**
     * No logging at all.
     */
    static public final int LEVEL_NONE = 6;
    /**
     * Critical errors. The application may no longer work correctly.
     */
    static public final int LEVEL_ERROR = 5;
    /**
     * Important warnings. The application will continue to work correctly.
     */
    static public final int LEVEL_WARN = 4;
    /**
     * Informative messages. Typically used for deployment.
     */
    static public final int LEVEL_INFO = 3;
    /**
     * Debug messages. This level is useful during development.
     */
    static public final int LEVEL_DEBUG = 2;
    /**
     * Trace messages. A lot of information is logged, so this level is usually only needed when debugging a problem.
     */
    static public final int LEVEL_TRACE = 1;

    /**
     * The level of messages that will be logged. Compiling this and the booleans below as "final" will cause the compiler to
     * remove all "if (Log.info) ..." type statements below the set level.
     */
    static private int level = LEVEL_INFO;

    /**
     * True when the ERROR level will be logged.
     */
    static public boolean ERROR = level <= LEVEL_ERROR;
    /**
     * True when the WARN level will be logged.
     */
    static public boolean WARN = level <= LEVEL_WARN;
    /**
     * True when the INFO level will be logged.
     */
    static public boolean INFO = level <= LEVEL_INFO;
    /**
     * True when the DEBUG level will be logged.
     */
    static public boolean DEBUG = level <= LEVEL_DEBUG;
    /**
     * True when the TRACE level will be logged.
     */
    static public boolean TRACE = level <= LEVEL_TRACE;

    /**
     * Sets the level to log. If a version of this class is being used that has a final log level, this has no affect.
     *
     * @param level a int.
     */
    static public void set(int level) {
        // Comment out method contents when compiling fixed level JARs.
        Log.level = level;
        ERROR = level <= LEVEL_ERROR;
        WARN = level <= LEVEL_WARN;
        INFO = level <= LEVEL_INFO;
        DEBUG = level <= LEVEL_DEBUG;
        TRACE = level <= LEVEL_TRACE;
    }

    /**
     * <p>NONE.</p>
     */
    static public void NONE() {
        set(LEVEL_NONE);
    }

    /**
     * <p>ERROR.</p>
     */
    static public void ERROR() {
        set(LEVEL_ERROR);
    }

    /**
     * <p>WARN.</p>
     */
    static public void WARN() {
        set(LEVEL_WARN);
    }

    /**
     * <p>INFO.</p>
     */
    static public void INFO() {
        set(LEVEL_INFO);
    }

    /**
     * <p>DEBUG.</p>
     */
    static public void DEBUG() {
        set(LEVEL_DEBUG);
    }

    /**
     * <p>TRACE.</p>
     */
    static public void TRACE() {
        set(LEVEL_TRACE);
    }

    /**
     * Sets the logger that will write the log messages.
     *
     * @param logger a {@link com.esotericsoftware.minlog.Log.Logger} object.
     */
    static public void setLogger(Logger logger) {
        Log.logger = logger;
    }

    /** Constant <code>logger</code> */
    static private Logger logger = new Logger();

    /**
     * <p>error.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void error(String message, Throwable ex) {
        if (ERROR) logger.log(LEVEL_ERROR, null, message, ex);
    }

    /**
     * <p>error.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void error(String category, String message, Throwable ex) {
        if (ERROR) logger.log(LEVEL_ERROR, category, message, ex);
    }

    /**
     * <p>error.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    static public void error(String message) {
        if (ERROR) logger.log(LEVEL_ERROR, null, message, null);
    }

    /**
     * <p>error.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     */
    static public void error(String category, String message) {
        if (ERROR) logger.log(LEVEL_ERROR, category, message, null);
    }

    /**
     * <p>warn.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void warn(String message, Throwable ex) {
        if (WARN) logger.log(LEVEL_WARN, null, message, ex);
    }

    /**
     * <p>warn.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void warn(String category, String message, Throwable ex) {
        if (WARN) logger.log(LEVEL_WARN, category, message, ex);
    }

    /**
     * <p>warn.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    static public void warn(String message) {
        if (WARN) logger.log(LEVEL_WARN, null, message, null);
    }

    /**
     * <p>warn.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     */
    static public void warn(String category, String message) {
        if (WARN) logger.log(LEVEL_WARN, category, message, null);
    }

    /**
     * <p>info.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void info(String message, Throwable ex) {
        if (INFO) logger.log(LEVEL_INFO, null, message, ex);
    }

    /**
     * <p>info.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void info(String category, String message, Throwable ex) {
        if (INFO) logger.log(LEVEL_INFO, category, message, ex);
    }

    /**
     * <p>info.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    static public void info(String message) {
        if (INFO) logger.log(LEVEL_INFO, null, message, null);
    }

    /**
     * <p>info.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     */
    static public void info(String category, String message) {
        if (INFO) logger.log(LEVEL_INFO, category, message, null);
    }

    /**
     * <p>debug.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void debug(String message, Throwable ex) {
        if (DEBUG) logger.log(LEVEL_DEBUG, null, message, ex);
    }

    /**
     * <p>debug.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void debug(String category, String message, Throwable ex) {
        if (DEBUG) logger.log(LEVEL_DEBUG, category, message, ex);
    }

    /**
     * <p>debug.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    static public void debug(String message) {
        if (DEBUG) logger.log(LEVEL_DEBUG, null, message, null);
    }

    /**
     * <p>debug.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     */
    static public void debug(String category, String message) {
        if (DEBUG) logger.log(LEVEL_DEBUG, category, message, null);
    }

    /**
     * <p>trace.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void trace(String message, Throwable ex) {
        if (TRACE) logger.log(LEVEL_TRACE, null, message, ex);
    }

    /**
     * <p>trace.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     * @param ex a {@link java.lang.Throwable} object.
     */
    static public void trace(String category, String message, Throwable ex) {
        if (TRACE) logger.log(LEVEL_TRACE, category, message, ex);
    }

    /**
     * <p>trace.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    static public void trace(String message) {
        if (TRACE) logger.log(LEVEL_TRACE, null, message, null);
    }

    /**
     * <p>trace.</p>
     *
     * @param category a {@link java.lang.String} object.
     * @param message a {@link java.lang.String} object.
     */
    static public void trace(String category, String message) {
        if (TRACE) logger.log(LEVEL_TRACE, category, message, null);
    }

    /**
     * <p>Constructor for Log.</p>
     */
    private Log() {
    }

    /**
     * Performs the actual logging. Default implementation logs to System.out. Extended and use {@link Log#logger} set to handle
     * logging differently.
     */
    static public class Logger {
        private long firstLogTime = new Date().getTime();

        public void log(int level, String category, String message, Throwable ex) {
            StringBuilder builder = new StringBuilder(256);

            long time = new Date().getTime() - firstLogTime;
            long minutes = time / (1000 * 60);
            long seconds = time / (1000) % 60;
            if (minutes <= 9) builder.append('0');
            builder.append(minutes);
            builder.append(':');
            if (seconds <= 9) builder.append('0');
            builder.append(seconds);

            switch (level) {
                case LEVEL_ERROR:
                    builder.append(" ERROR: ");
                    break;
                case LEVEL_WARN:
                    builder.append("  WARN: ");
                    break;
                case LEVEL_INFO:
                    builder.append("  INFO: ");
                    break;
                case LEVEL_DEBUG:
                    builder.append(" DEBUG: ");
                    break;
                case LEVEL_TRACE:
                    builder.append(" TRACE: ");
                    break;
            }

            if (category != null) {
                builder.append('[');
                builder.append(category);
                builder.append("] ");
            }

            builder.append(message);

            if (ex != null) {
                StringWriter writer = new StringWriter(256);
                ex.printStackTrace(new PrintWriter(writer));
                builder.append('\n');
                builder.append(writer.toString().trim());
            }

            print(builder.toString());
        }

        /**
         * Prints the message to System.out. Called by the default implementation of {@link #log(int, String, String, Throwable)}.
         */
        protected void print(String message) {
            System.out.println(message);
        }
    }
}
