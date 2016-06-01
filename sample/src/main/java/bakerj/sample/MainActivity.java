package bakerj.sample;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bakerj.backgroundblurpopupwindow.R;

import bakerj.backgroundblurpopupwindow.BackgroundBlurPopupWindow;

public class MainActivity extends Activity implements View.OnClickListener{
    private BackgroundBlurPopupWindow mPopupWindow;
    private TextView mTextView;
    private View mBtnTop, mBtnBottom, mBtnLeft, mBtnRight, mBtnCenter, mBtnAll, mBtnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnTop = findViewById(R.id.top);
        mBtnTop.setOnClickListener(this);
        mBtnBottom = findViewById(R.id.bottom);
        mBtnBottom.setOnClickListener(this);
        mBtnLeft = findViewById(R.id.left);
        mBtnLeft.setOnClickListener(this);
        mBtnRight = findViewById(R.id.right);
        mBtnRight.setOnClickListener(this);
        mBtnCenter = findViewById(R.id.center);
        mBtnCenter.setOnClickListener(this);
        mBtnAll = findViewById(R.id.all);
        mBtnAll.setOnClickListener(this);
        mBtnView = findViewById(R.id.view);
        mBtnView.setOnClickListener(this);
        mTextView = new TextView(this);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTextView.setBackgroundResource(R.drawable.poppup_bg);
        mTextView.setPadding(10, 10, 10, 10);
        mTextView.setGravity(Gravity.CENTER);
        mPopupWindow = new BackgroundBlurPopupWindow(mTextView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, this, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top:
                mPopupWindow.setBlurRadius(BackgroundBlurPopupWindow.DEFAULT_BLUR_RADIUS);
                mPopupWindow.setDownScaleFactor(BackgroundBlurPopupWindow
                        .DEFAULT_BLUR_DOWN_SCALE_FACTOR);
                mTextView.setText("This is a popupwindow\n\nblur & dark on bottom");
                mPopupWindow.setDarkColor(Color.parseColor("#a0000000"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.darkBelow(mBtnTop);
                mPopupWindow.showAsDropDown(mBtnTop, mBtnTop.getRight() / 2, 0);
                break;
            case R.id.left:
                mPopupWindow.setBlurRadius(BackgroundBlurPopupWindow.DEFAULT_BLUR_RADIUS);
                mPopupWindow.setDownScaleFactor(BackgroundBlurPopupWindow
                        .DEFAULT_BLUR_DOWN_SCALE_FACTOR);
                mTextView.setText("This is a popupwindow\n\nblur & dark on right");
                mPopupWindow.setDarkColor(Color.parseColor("#a0000000"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.darkRightOf(mBtnLeft);
                mPopupWindow.showAtLocation(mBtnLeft, Gravity.CENTER_VERTICAL | Gravity.LEFT, 0, 0);
                break;
            case R.id.right:
                mPopupWindow.setBlurRadius(BackgroundBlurPopupWindow.DEFAULT_BLUR_RADIUS);
                mPopupWindow.setDownScaleFactor(BackgroundBlurPopupWindow
                        .DEFAULT_BLUR_DOWN_SCALE_FACTOR);
                mTextView.setText("This is a popupwindow\n\nblur & dark on left");
                mPopupWindow.setDarkColor(Color.parseColor("#a0000088"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.drakLeftOf(mBtnRight);
                mPopupWindow.showAtLocation(mBtnRight, Gravity.CENTER_VERTICAL | Gravity.RIGHT, 0, 0);
                break;
            case R.id.bottom:
                mPopupWindow.setBlurRadius(BackgroundBlurPopupWindow.DEFAULT_BLUR_RADIUS);
                mPopupWindow.setDownScaleFactor(BackgroundBlurPopupWindow
                        .DEFAULT_BLUR_DOWN_SCALE_FACTOR);
                mTextView.setText("This is a popupwindow\n\nblur & dark on top");
                mPopupWindow.setDarkColor(Color.parseColor("#a0008800"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.darkAbove(mBtnBottom);
                mPopupWindow.showAtLocation(mBtnBottom, Gravity.CENTER_HORIZONTAL, 0, mBtnBottom.getTop());
                break;
            case R.id.center:
                mPopupWindow.setBlurRadius(4);
                mPopupWindow.setDownScaleFactor(1.5f);
                mTextView.setText("This is a popupwindow\n\nblur & dark in center");
                mPopupWindow.setDarkColor(Color.parseColor("#a0880000"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.drakLeftOf(mBtnRight);
                mPopupWindow.darkRightOf(mBtnLeft);
                mPopupWindow.darkAbove(mBtnBottom);
                mPopupWindow.darkBelow(mBtnTop);
                mPopupWindow.showAtLocation(mBtnCenter, Gravity.CENTER, 0, 0);
                break;
            case R.id.all:
                mPopupWindow.setBlurRadius(10);
                mPopupWindow.setDownScaleFactor(1.2f);
                mTextView.setText("This is a popupwindow\n\nblur & dark fill all");
                mPopupWindow.setDarkColor(Color.parseColor("#a0000000"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.darkFillScreen();
                mPopupWindow.showAtLocation(mBtnAll, Gravity.CENTER, 0, 0);
                break;
            case R.id.view:
                mPopupWindow.setBlurRadius(BackgroundBlurPopupWindow.DEFAULT_BLUR_RADIUS);
                mPopupWindow.setDownScaleFactor(BackgroundBlurPopupWindow
                        .DEFAULT_BLUR_DOWN_SCALE_FACTOR);
                mTextView.setText("This is a popupwindow\n\nblur & dark fill view");
                mPopupWindow.setDarkColor(Color.parseColor("#a0000000"));
                mPopupWindow.resetDarkPosition();
                mPopupWindow.drakFillView(mBtnView);
                mPopupWindow.showAtLocation(mBtnView, Gravity.CENTER, 0, 0);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPopupWindow.dismiss();
        mPopupWindow.onDestroy();
    }
}
