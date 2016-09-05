package io.tjeubaoit.pokeradar.util;

import android.util.Log;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class Logger {

    private final String tag;

    private Logger(Class<?> clazz) {
        tag = clazz.getSimpleName();
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }

    public void debug(Object msg) {
        if (msg != null) {
            Log.d(tag, msg.toString());
        }
    }

    public void debug(Object msg, Throwable tr) {
        if (msg != null) {
            Log.d(tag, msg.toString(), tr);
        } else {
            Log.d(tag, "Exception", tr);
        }
    }

    public void info(Object msg) {
        if (msg != null) {
            Log.i(tag, msg.toString());
        }
    }

    public void info(Object msg, Throwable tr) {
        if (msg != null) {
            Log.i(tag, msg.toString(), tr);
        } else {
            Log.i(tag, "Exception", tr);
        }
    }

    public void warn(Object msg) {
        if (msg != null) {
            Log.w(tag, msg.toString());
        }
    }

    public void warn(Object msg, Throwable tr) {
        if (msg != null) {
            Log.w(tag, msg.toString(), tr);
        } else {
            Log.w(tag, "Exception", tr);
        }
    }

    public void error(Object msg) {
        if (msg != null) {
            Log.e(tag, msg.toString());
        }
    }

    public void error(Object msg, Throwable tr) {
        if (msg != null) {
            Log.e(tag, msg.toString(), tr);
        } else {
            Log.e(tag, "Exception", tr);
        }
    }
}
