package bakerj.backgroundblurpopupwindow;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author BakerJ
 */
public class Utils {
    public static boolean isFullScreen(Activity activity) {
        return (WindowManager.LayoutParams.FLAG_FULLSCREEN & activity.getWindow().getAttributes().flags)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static boolean isTranslucentStatusBar(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & activity.getWindow().getAttributes().flags)
                        == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    }

    public static float getStatusBarHeight(Context context) {
        final int heightResId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return heightResId > 0 ? context.getResources().getDimensionPixelSize(heightResId) : 0;
    }
}
