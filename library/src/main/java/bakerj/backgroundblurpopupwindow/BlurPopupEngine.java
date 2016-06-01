package bakerj.backgroundblurpopupwindow;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * Encapsulate the whole behaviour to provide a blur effect on a DialogFragment.
 * <p/>
 * All the screen behind the dialog will be blurred except the action bar.
 * <p/>
 * Simply linked all methods to the matching lifecycle ones.
 */
public class BlurPopupEngine {

    /**
     * Since image is going to be blurred, we don't care about resolution.
     * Down scale factor to reduce blurring time and memory allocation.
     */
    static final float DEFAULT_BLUR_DOWN_SCALE_FACTOR = 4.0f;

    /**
     * Radius used to blur the background
     */
    static final int DEFAULT_BLUR_RADIUS = 8;

    /**
     * Default dimming policy.
     */
    static final boolean DEFAULT_DIMMING_POLICY = false;

    /**
     * Default debug policy.
     */
    static final boolean DEFAULT_DEBUG_POLICY = false;

    /**
     * Default action bar blurred policy.
     */
    static final boolean DEFAULT_ACTION_BAR_BLUR = false;

    /**
     * Default use of RenderScript.
     */
    static final boolean DEFAULT_USE_RENDERSCRIPT = false;

    /**
     * Log cat
     */
    private static final String TAG = BlurPopupEngine.class.getSimpleName();
    private Context mContext;
    private View mBlurView;
    private Rect clipRect = new Rect();
    /**
     * Image view used to display blurred background.
     */
    private ImageView mBlurredBackgroundView;

    /**
     * Layout params used to add blurred background.
     */
    private FrameLayout.LayoutParams mBlurredBackgroundLayoutParams;

    /**
     * Task used to capture screen and blur it.
     */
    private BlurAsyncTask mBluringTask;

    /**
     * Used to enable or disable debug mod.
     */
    private boolean mDebugEnable = false;

    /**
     * Factor used to down scale background. High quality isn't necessary
     * since the background will be blurred.
     */
    private float mDownScaleFactor = DEFAULT_BLUR_DOWN_SCALE_FACTOR;

    /**
     * Radius used for fast blur algorithm.
     */
    private int mBlurRadius = DEFAULT_BLUR_RADIUS;

    /**
     * Duration used to animate in and out the blurred image.
     * <p/>
     * In milli.
     */
    private int mAnimationDuration;

    /**
     * Boolean used to know if RenderScript should be used
     */
    private boolean mUseRenderScript;


    /**
     * Constructor.
     */
    public BlurPopupEngine(int animDuration, Context context, View blurView, ImageView
            blurHolderView) {
        mAnimationDuration = animDuration;
        mContext = context;
        mBlurView = blurView;
        mBlurredBackgroundView = blurHolderView;
    }

    /**
     * Resume the engine.
     *
     * @param retainedInstance use getRetainInstance.
     */
    public void onResume(boolean retainedInstance) {
        if (mBlurredBackgroundView == null || retainedInstance) {
            mBluringTask = new BlurAsyncTask();
            mBluringTask.execute();
        }
    }

    /**
     * Must be linked to the original lifecycle.
     */
    @SuppressLint("NewApi")
    public void onDismiss() {
        //remove blurred background and clear memory, could be null if dismissed before blur effect
        //processing ends
        //cancel async task
        if (mBluringTask != null) {
            mBluringTask.cancel(true);
        }
        if (mBlurredBackgroundView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mBlurredBackgroundView
                        .animate()
                        .alpha(0f)
                        .setDuration(mAnimationDuration)
                        .setInterpolator(new AccelerateInterpolator())
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//                                super.onAnimationCancel(animation);
//                            }
//                        })
                        .start();
            }
        }
    }

    /**
     * Must be linked to the original lifecycle.
     */
    public void onDestroy() {
        if (mBluringTask != null) {
            mBluringTask.cancel(true);
        }
        mBluringTask = null;
        mContext = null;
    }

    /**
     * Enable / disable debug mode.
     * <p/>
     * LogCat and graphical information directly on blurred screen.
     *
     * @param enable true to display log in LogCat.
     */
    public void debug(boolean enable) {
        mDebugEnable = enable;
    }

    /**
     * Apply custom down scale factor.
     * <p/>
     * By default down scale factor is set to
     * {@link BlurPopupEngine#DEFAULT_BLUR_DOWN_SCALE_FACTOR}
     * <p/>
     * Higher down scale factor will increase blurring speed but reduce final rendering quality.
     *
     * @param factor customized down scale factor, must be at least 1.0 ( no down scale applied )
     */
    public void setDownScaleFactor(float factor) {
        if (factor >= 1.0f) {
            mDownScaleFactor = factor;
        } else {
            mDownScaleFactor = 1.0f;
        }
    }

    /**
     * Set use of RenderScript
     * <p/>
     * By default RenderScript is set to
     * {@link BlurPopupEngine#DEFAULT_USE_RENDERSCRIPT}
     * <p/>
     * Don't forget to add those lines to your build.gradle
     * <pre>
     *  defaultConfig {
     *  ...
     *  renderscriptTargetApi 22
     *  renderscriptSupportModeEnabled true
     *  ...
     *  }
     * </pre>
     *
     * @param useRenderScript use of RenderScript
     */

    public void setUseRenderScript(boolean useRenderScript) {
        mUseRenderScript = useRenderScript;
    }

    /**
     * Apply custom blur radius.
     * <p/>
     * By default blur radius is set to
     * {@link BlurPopupEngine#DEFAULT_BLUR_RADIUS}
     *
     * @param radius custom radius used to blur.
     */
    public void setBlurRadius(int radius) {
        if (radius >= 0) {
            mBlurRadius = radius;
        } else {
            mBlurRadius = 0;
        }
    }

    public void setBlurRect(int left, int top, int right, int bottom) {
        clipRect.set(left, top, right, bottom);
    }

    /**
     * Blur the given bitmap and add it to the activity.
     *
     * @param bkg  should be a bitmap of the background.
     * @param view background view.
     */
    private Bitmap blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        //define layout params to the previous imageView in order to match its parent
        mBlurredBackgroundLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        //overlay used to build scaled preview and blur background
        Bitmap overlay = null;

        //add offset to the source boundaries since we don't want to blur actionBar pixels
        Rect srcRect = new Rect(
                0,
                0,
                bkg.getWidth(),
                bkg.getHeight()
        );

        //in order to keep the same ratio as the one which will be used for rendering, also
        //add the offset to the overlay.
        double height = Math.ceil(bkg.getHeight() / mDownScaleFactor);
        double width = Math.ceil((bkg.getWidth() * height
                / bkg.getHeight()));

        // Render script doesn't work with RGB_565
        if (mUseRenderScript) {
            overlay = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        } else {
            overlay = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.RGB_565);
        }
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                //add offset as top margin since actionBar height must also considered when we display
                // the blurred background. Don't want to draw on the actionBar.
                mBlurredBackgroundLayoutParams.gravity = Gravity.TOP;
            }
        } catch (NoClassDefFoundError e) {
            // no dependency to appcompat, that means no additional top offset due to actionBar.
            mBlurredBackgroundLayoutParams.setMargins(0, 0, 0, 0);
        }
        //scale and draw background view on the canvas overlay
        Canvas canvas = new Canvas(overlay);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);

        //build drawing destination boundaries
        final RectF destRect = new RectF(0, 0, overlay.getWidth(), overlay.getHeight());

        //draw background from source area in source background to the destination area on the overlay
        canvas.drawBitmap(bkg, srcRect, destRect, paint);

        //apply fast blur on overlay
        if (mUseRenderScript) {
            overlay = RenderScriptBlurHelper.doBlur(overlay, mBlurRadius, true, mContext);
        } else {
            overlay = FastBlurHelper.doBlur(overlay, mBlurRadius, true);
        }
        if (mDebugEnable) {
            String blurTime = (System.currentTimeMillis() - startMs) + " ms";
            Log.d(TAG, "Blur method : " + (mUseRenderScript ? "RenderScript" : "FastBlur"));
            Log.d(TAG, "Radius : " + mBlurRadius);
            Log.d(TAG, "Down Scale Factor : " + mDownScaleFactor);
            Log.d(TAG, "Blurred achieved in : " + blurTime);
            Log.d(TAG, "Allocation : " + bkg.getRowBytes() + "ko (screen capture) + "
                    + overlay.getRowBytes() + "ko (blurred bitmap)"
                    + (!mUseRenderScript ? " + temp buff " + overlay.getRowBytes() + "ko." : "."));
            Rect bounds = new Rect();
            Canvas canvas1 = new Canvas(overlay);
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setTextSize(20.0f);
            paint.getTextBounds(blurTime, 0, blurTime.length(), bounds);
            canvas1.drawText(blurTime, 2, bounds.height(), paint);
        }
        return overlay;
    }

    /**
     * Used to check if the status bar is translucent.
     *
     * @return true if the status bar is translucent.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isStatusBarTranslucent() {
        TypedValue typedValue = new TypedValue();
        int[] attribute = new int[]{android.R.attr.windowTranslucentStatus};
        TypedArray array = mContext.obtainStyledAttributes(typedValue.resourceId, attribute);
        boolean isStatusBarTranslucent = array.getBoolean(0, false);
        array.recycle();
        return isStatusBarTranslucent;
    }

    /**
     * Async task used to process blur out of ui thread
     */
    private class BlurAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Bitmap mBackground;
        private View mBackgroundView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mBackgroundView = mBlurView;

            //retrieve background view, must be achieved on ui thread since
            //only the original thread that created a view hierarchy can touch its views.

            Rect rect = new Rect();
            mBackgroundView.getWindowVisibleDisplayFrame(rect);
            mBackgroundView.destroyDrawingCache();
            mBackgroundView.setDrawingCacheEnabled(true);
            mBackgroundView.buildDrawingCache(true);
            mBackground = mBackgroundView.getDrawingCache(true);
            mBackground = Bitmap.createBitmap(mBackground, clipRect.left, clipRect.top,
                    clipRect.width(), clipRect.height(), null, false);

            /**
             * After rotation, the DecorView has no height and no width. Therefore
             * .getDrawingCache() return null. That's why we  have to force measure and layout.
             */
            if (mBackground == null) {
                mBackgroundView.measure(
                        View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY)
                );
                mBackgroundView.layout(0, 0, mBackgroundView.getMeasuredWidth(),
                        mBackgroundView.getMeasuredHeight());
                mBackgroundView.destroyDrawingCache();
                mBackgroundView.setDrawingCacheEnabled(true);
                mBackgroundView.buildDrawingCache(true);
                mBackground = mBackgroundView.getDrawingCache(true);
                mBackground = Bitmap.createBitmap(mBackground, clipRect.left, clipRect.top,
                        clipRect.width(), clipRect.height(), null, false);
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap blur;
            //process to the blue
            if (!isCancelled()) {
                blur = blur(mBackground, mBackgroundView);
            } else {
                return null;
            }
            //clear memory
            mBackground.recycle();
            return blur;
        }

        @Override
        @SuppressLint("NewApi")
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                mBlurredBackgroundView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mBlurredBackgroundView.setImageDrawable(new BitmapDrawable(mContext.getResources(),
                        bitmap));
            }

            mBackgroundView.destroyDrawingCache();
            mBackgroundView.setDrawingCacheEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                mBlurredBackgroundView.setAlpha(0f);
                mBlurredBackgroundView
                        .animate()
                        .alpha(1f)
                        .setDuration(mAnimationDuration)
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
            mBackgroundView = null;
            mBackground = null;
        }
    }
}
