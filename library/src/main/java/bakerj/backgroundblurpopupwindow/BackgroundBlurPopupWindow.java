package bakerj.backgroundblurpopupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;


/**
 * @author BakerJ
 *         Thanks to https://github.com/tvbarthel/BlurDialogFragment for blur engine
 */
public class BackgroundBlurPopupWindow extends PopupWindow {

    public static final int DEFAULT_BLUR_RADIUS = 6;
    public static final float DEFAULT_BLUR_DOWN_SCALE_FACTOR = 1f;
    private View mDarkView;
    private FrameLayout mBackgroundLayout;
    private ImageView mBlurView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mDarkLP;
    private int mScreenWidth, mScreenHeight;
    private int mRightOf, mLeftOf, mBelow, mAbove;
    private int[] mLocationInWindowPosition = new int[2];
    private int mDarkStyle = -1;
    private boolean isDarkShowing = false;
    private BlurPopupEngine mBlurPopupEngine;
    private WeakReference<View> mRightOfPositionView, mLeftOfPositionView, mBelowPositionView,
            mAbovePositionView, mFillPositionView;
    private int mBlurTopBottomFixSize;

    /**
     * Constructor without blur
     */
    public BackgroundBlurPopupWindow(View contentView, int width, int height) {
        this(contentView, width, height, null, false);
    }

    /**
     * Constructor with blur
     */
    public BackgroundBlurPopupWindow(View contentView, int width, int height, Activity activity,
                                     boolean isDark) {
        super(contentView, width, height);
        if (contentView != null) {
            mWindowManager = (WindowManager) contentView.getContext().getSystemService(Context.WINDOW_SERVICE);
            mDarkLP = createDarkLayout(contentView.getWindowToken());
            mBackgroundLayout = new FrameLayout(contentView.getContext());
            mBackgroundLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mBackgroundLayout.setBackgroundColor(Color.parseColor("#a0000000"));
            DisplayMetrics dm = new DisplayMetrics();
            mWindowManager.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
            darkFillScreen();
            mDarkView = mBackgroundLayout;
            if (activity != null) {
                initBlurEngine(activity, isDark);
            }
        }
    }

    private void initBlurEngine(Activity activity, boolean isDark) {
        mBlurView = new ImageView(mBackgroundLayout.getContext());
        mBlurView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        mBackgroundLayout.addView(mBlurView);
        mBackgroundLayout.setBackgroundColor(Color.TRANSPARENT);
        View rootView = activity.findViewById(android.R.id.content);
        mBlurPopupEngine = new BlurPopupEngine(300, mBackgroundLayout.getContext(),
                rootView, mBlurView);
        mBlurPopupEngine.setBlurRadius(DEFAULT_BLUR_RADIUS);
        mBlurPopupEngine.setDownScaleFactor(DEFAULT_BLUR_DOWN_SCALE_FACTOR);
        mBlurPopupEngine.setUseRenderScript(true);
        if (!Utils.isFullScreen(activity) && !Utils.isTranslucentStatusBar(activity)) {
            mBlurTopBottomFixSize = (int) Utils.getStatusBarHeight(activity);
        }
        if (isDark) {
            View darkView = new View(mBackgroundLayout.getContext());
            darkView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            darkView.setBackgroundColor(Color.parseColor("#a0000000"));
            mBackgroundLayout.addView(darkView);
            mDarkView = darkView;
        }
    }

    /**
     * Apply custom blur radius.
     * <p/>
     * By default blur radius is set to
     * {@link BlurPopupEngine#DEFAULT_BLUR_RADIUS}
     *
     * @param radius custom radius used to blur.
     */
    public void setBlurRadius(int radius){
        mBlurPopupEngine.setBlurRadius(radius);
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
    public void setDownScaleFactor(float factor){
        mBlurPopupEngine.setDownScaleFactor(factor);
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
    public void setUseRenderScript(boolean useRenderScript){
        mBlurPopupEngine.setUseRenderScript(useRenderScript);
    }

    /**
     * create dark layout
     *
     * @param token
     * @return
     */
    private WindowManager.LayoutParams createDarkLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
        p.token = token;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
        return p;
    }

    private int computeFlags(int curFlags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            curFlags &= ~(
                    WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                            WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        } else {
            curFlags &= ~(
                    WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        return curFlags;
    }

    /**
     * get dark animation
     *
     * @return
     */
    private int computeDarkAnimation() {
        if (mDarkStyle == -1) {
            return R.style.DarkAnimation;
        }
        return mDarkStyle;
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        invokeBgCover();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        invokeBgCover();
        super.showAtLocation(parent, gravity, x, y);
    }

    /**
     * show dark background
     */
    private void invokeBgCover() {
        if (isShowing() || getContentView() == null) {
            return;
        }
        checkPosition();
        if (mBackgroundLayout != null && !isDarkShowing) {
            computeDarkLayout();
            mDarkLP.windowAnimations = computeDarkAnimation();
            mWindowManager.addView(mBackgroundLayout, mDarkLP);
            isDarkShowing = true;
            if (mBlurPopupEngine != null) {
                mBlurPopupEngine.onResume(true);
            }
        }
    }

    /**
     * check whether the position of dark is set
     */
    private void checkPosition() {
        checkPositionLeft();
        checkPositionRight();
        checkPositionBelow();
        checkPositionAbove();
        checkPositionFill();
        if (mBlurPopupEngine != null) {
            int top = mBelow - mBlurTopBottomFixSize;
            int bottom = mAbove - mBlurTopBottomFixSize;
            if (top <= 0) {
                mBlurView.setPadding(0, mBlurTopBottomFixSize, 0, 0);
                top = 0;
            } else {
                mBlurView.setPadding(0, 0, 0, 0);
            }
            mBlurPopupEngine.setBlurRect(mRightOf, top, mLeftOf,
                    bottom > 0 ? bottom : 0);
        }
    }

    /**
     * check whether the left-of-position of dark is set
     */
    private void checkPositionLeft() {
        if (mLeftOfPositionView != null) {
            View leftOfView = mLeftOfPositionView.get();
            if (leftOfView != null && mLeftOf == 0) {
                drakLeftOf(leftOfView);
            }
        }
    }

    /**
     * check whether the right-of-position of dark is set
     */
    private void checkPositionRight() {
        if (mRightOfPositionView != null) {
            View rightOfView = mRightOfPositionView.get();
            if (rightOfView != null && mRightOf == 0) {
                darkRightOf(rightOfView);
            }
        }
    }

    /**
     * check whether the below-position of dark is set
     */
    private void checkPositionBelow() {
        if (mBelowPositionView != null) {
            View belowView = mBelowPositionView.get();
            if (belowView != null && mBelow == 0) {
                darkBelow(belowView);
            }
        }
    }

    /**
     * check whether the above-position of dark is set
     */
    private void checkPositionAbove() {
        if (mAbovePositionView != null) {
            View aboveView = mAbovePositionView.get();
            if (aboveView != null && mAbove == 0) {
                darkAbove(aboveView);
            }
        }
    }

    /**
     * check whether the fill-position of dark is set
     */
    private void checkPositionFill() {
        if (mFillPositionView != null) {
            View fillView = mFillPositionView.get();
            if (fillView != null && (mLeftOf == 0 || mAbove == 0)) {
                drakFillView(fillView);
            }
        }
    }

    /**
     * reset dark position
     */
    private void computeDarkLayout() {
        mDarkLP.x = mRightOf;
        mDarkLP.y = mBelow;
        mDarkLP.width = mLeftOf - mRightOf;
        mDarkLP.height = mAbove - mBelow;
    }

    @Override
    public void dismiss() {
        if (isDarkShowing && isShowing() && getContentView() != null && mBackgroundLayout != null) {
            mWindowManager.removeViewImmediate(mBackgroundLayout);
            isDarkShowing = false;
            if (mBlurPopupEngine != null) {
                mBlurPopupEngine.onDismiss();
            }
        }
        super.dismiss();
    }

    /**
     * set dark color
     *
     * @param color
     */
    public void setDarkColor(int color) {
        if (mDarkView != null) {
            mDarkView.setBackgroundColor(color);
        }
    }

    public void resetDarkPosition() {
        darkFillScreen();
        if (mRightOfPositionView != null) {
            mRightOfPositionView.clear();
        }
        if (mLeftOfPositionView != null) {
            mLeftOfPositionView.clear();
        }
        if (mBelowPositionView != null) {
            mBelowPositionView.clear();
        }
        if (mAbovePositionView != null) {
            mAbovePositionView.clear();
        }
        if (mFillPositionView != null) {
            mFillPositionView.clear();
        }
        mRightOfPositionView = mLeftOfPositionView = mBelowPositionView = mAbovePositionView =
                mFillPositionView = null;
    }

    /**
     * fill screen
     */
    public void darkFillScreen() {
        mRightOf = 0;
        mLeftOf = mScreenWidth;
        mAbove = mScreenHeight;
        mBelow = 0;
    }

    /**
     * dark fill view
     *
     * @param view target view
     */
    public void drakFillView(View view) {
        mFillPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mRightOf = mLocationInWindowPosition[0];
        mLeftOf = mLocationInWindowPosition[0] + view.getWidth();
        mAbove = mLocationInWindowPosition[1] + view.getHeight();
        mBelow = mLocationInWindowPosition[1];
    }

    /**
     * dark right of target view
     *
     * @param view
     */
    public void darkRightOf(View view) {
        mRightOfPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mRightOf = mLocationInWindowPosition[0] + view.getWidth();
    }

    /**
     * dark left of target view
     *
     * @param view
     */
    public void drakLeftOf(View view) {
        mLeftOfPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mLeftOf = mLocationInWindowPosition[0];
    }

    /**
     * dark above target view
     *
     * @param view
     */
    public void darkAbove(View view) {
        mAbovePositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mAbove = mLocationInWindowPosition[1];
    }

    /**
     * dark below target view
     *
     * @param view
     */
    public void darkBelow(View view) {
        mBelowPositionView = new WeakReference<>(view);
        view.getLocationInWindow(mLocationInWindowPosition);
        mBelow = mLocationInWindowPosition[1] + view.getHeight();
    }

    /**
     * get dark anim style
     *
     * @return
     */
    public int getDarkAnimStyle() {
        return mDarkStyle;
    }

    /**
     * set dark anim style
     *
     * @param darkStyle
     */
    public void setDarkAnimStyle(int darkStyle) {
        this.mDarkStyle = darkStyle;
    }

    public boolean isDarkShowing() {
        return isDarkShowing;
    }

    public void setDarkShowing(boolean isDarkShowing) {
        this.isDarkShowing = isDarkShowing;
    }

    public void onDestroy() {
        if (mBlurPopupEngine != null) {
            mBlurPopupEngine.onDestroy();
        }
    }
}
