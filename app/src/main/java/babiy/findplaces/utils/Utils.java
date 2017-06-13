package babiy.findplaces.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.Locale;

public class Utils {

    public static boolean checkConnection(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static String getLocale() {

        return Locale.getDefault().getLanguage();
    }
}