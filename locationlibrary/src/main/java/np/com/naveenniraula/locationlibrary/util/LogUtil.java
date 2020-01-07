package np.com.naveenniraula.locationlibrary.util;

import android.util.Log;

import np.com.naveenniraula.locationlibrary.BuildConfig;

public class LogUtil {

    public static void d(String tag, String msg) {
        if (isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug()) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug()) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug()) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug()) {
            Log.w(tag, msg);
        }
    }

    public static void wtf(String tag, String msg) {
        if (isDebug()) {
            Log.wtf(tag, msg);
        }
    }

    private static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

}
