package com.robey.trumptweets;

/**
 * Created by scott on 2/18/17.
 */
public class Log {

    void info(String msg) {
        System.out.println(msg);
    }

    void error(String msg) {
        System.err.println(msg);
    }
    void error(String msg, Throwable t) {
        System.err.println(msg);
        t.printStackTrace();
    }
}
