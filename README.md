# BackgroundBlurPopupWindow
Android PopupWindow with dark & blur background

Thanks to https://github.com/tvbarthel/BlurDialogFragment for blur engine

## Screenshot
![](https://github.com/BakerJQ/BackgroundBlurPopupWindow/blob/master/Screenshots/show.gif)

## How to use
### Init
Just use it as the same as the original PopupWindow
```java
mPopupWindow = new BackgroundDarkPopupWindow(mTextView, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
mPopupWindow.setFocusable(true);
mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
```
#### Set dark and blur layer
```java
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
```
