package io;
import graph.*;
import gui.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
    static final Logger rootLogger = LogManager.getRootLogger();

    public void info(String e) {
        rootLogger.info(e);
    }


    public void warning(String e) {
        rootLogger.warn(e);
    }


    public void error(String e) {
        rootLogger.error(e);
    }


    public void fatal(String e) {
        rootLogger.fatal(e);
    }
}