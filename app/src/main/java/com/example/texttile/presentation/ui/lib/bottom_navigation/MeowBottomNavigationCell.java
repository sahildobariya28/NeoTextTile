package com.example.texttile.presentation.ui.lib.bottom_navigation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.texttile.R;


class MeowBottomNavigationCell extends RelativeLayout {

    private int defaultIconColor;
    private int selectedIconColor;
    private int circleColor;
    private int icon;

    private String count;
    private int iconSize;
    private int countTextColor;
    private int countBackgroundColor;

    private Typeface countTypeface;
    private int rippleColor;
    private boolean isFromLeft;
    private long duration;
    private float progress;
    private boolean isEnabledCell;

    private MeowBottomNavigation.ClickListener onClickListener;

    public View containerView;
    private boolean allowDraw;

    public static final String EMPTY_VALUE = "empty";

    FrameLayout frameLayout;
    CellImageView cellImageView;
    View circle_view;
    TextView textView;

    public MeowBottomNavigationCell(Context context) {
        super(context);
        count = "empty";
        iconSize = Utils.dip(getContext(), 48);
        initializeView();
    }

    public MeowBottomNavigationCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        count = "empty";
        iconSize = Utils.dip(getContext(), 48);
        initializeView();
    }

    public MeowBottomNavigationCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        count = "empty";
        iconSize = Utils.dip(getContext(), 48);
        initializeView();
    }

    public final int getDefaultIconColor() {
        return this.defaultIconColor;
    }

    public final void setDefaultIconColor(int value) {
        defaultIconColor = value;
        if (allowDraw) {
            cellImageView.setColor(!isEnabledCell ? defaultIconColor : selectedIconColor);
        }
    }

    public final int getSelectedIconColor() {
        return selectedIconColor;
    }

    public final void setSelectedIconColor(int value) {
        selectedIconColor = value;
        if (allowDraw) {

            cellImageView.setColor(isEnabledCell ? selectedIconColor : defaultIconColor);
        }
    }

    public final int getCircleColor() {
        return circleColor;
    }

    public final void setCircleColor(int value) {
        circleColor = value;
        if (allowDraw) {
            setEnabledCell(isEnabledCell);
        }
    }

    public final int getIcon() {
        return icon;
    }

    public final void setIcon(int value) {
        icon = value;
        if (allowDraw) {
            cellImageView.setResource(value);
        }
    }

    public final String getCount() {
        return count;
    }

    public final void setCount(String value) {
        count = value;
        if (allowDraw) {
            if (count != null && count.equals("empty")) {
                textView.setText("");
                textView.setVisibility(INVISIBLE);
            } else {
                float var13 = 1.0F;

                textView.setText(count);
                textView.setVisibility(VISIBLE);
                textView.setScaleX(var13);
                textView.setScaleY(var13);
            }
        }
    }

    private void setIconSize(int value) {
        iconSize = value;
        if (allowDraw) {

            cellImageView.setSize(value);
            cellImageView.setPivotX((float) iconSize / 2.0F);
            cellImageView.setPivotY((float) iconSize / 2.0F);
        }
    }

    public final int getCountTextColor() {
        return countTextColor;
    }

    public final void setCountTextColor(int value) {
        countTextColor = value;
        if (allowDraw) {
            textView.setTextColor(countTextColor);
        }
    }

    public final int getCountBackgroundColor() {
        return countBackgroundColor;
    }

    public final void setCountBackgroundColor(int value) {
        countBackgroundColor = value;
        if (allowDraw) {
            GradientDrawable gradient = new GradientDrawable();
            gradient.setColor(countBackgroundColor);
            gradient.setShape(GradientDrawable.OVAL);
            ViewCompat.setBackground(textView, gradient);
        }
    }


    public final Typeface getCountTypeface() {
        return countTypeface;
    }

    public final void setCountTypeface(Typeface value) {
        countTypeface = value;
        if (allowDraw && countTypeface != null) {

            textView.setTypeface(countTypeface);
        }
    }

    public final int getRippleColor() {
        return rippleColor;
    }

    public final void setRippleColor(int value) {
        rippleColor = value;
        if (allowDraw) {
            setEnabledCell(isEnabledCell);
        }
    }

    public final boolean isFromLeft() {
        return isFromLeft;
    }

    public final void setFromLeft(boolean var1) {
        isFromLeft = var1;
    }

    public final long getDuration() {
        return duration;
    }

    public final void setDuration(long var1) {
        duration = var1;
    }

    private void setProgress(float value) {
        progress = value;

        frameLayout.setY((1.0F - progress) * (float) Utils.dip(getContext(), 18) + (float) Utils.dip(getContext(), -2));

        cellImageView.setColor(progress == 1.0F ? selectedIconColor : defaultIconColor);
        float scale = (1.0F - progress) * -0.2F + 1.0F;
        cellImageView.setScaleX(scale);
        cellImageView.setScaleY(scale);

        GradientDrawable gradient = new GradientDrawable();
        gradient.setColor(circleColor);
        gradient.setShape(GradientDrawable.OVAL);
        ViewCompat.setBackground(circle_view, gradient);
        ViewCompat.setElevation(circle_view, progress > 0.7F ? Utils.dip_to_float(getContext(), progress * 4.0F) : 0.0F);

        int m = Utils.dip(getContext(), 24);

        circle_view.setX((1.0F - progress) * (float) (isFromLeft ? -m : m) + (float) (getMeasuredWidth() - Utils.dip(getContext(), 48)) / 2.0F);
        circle_view.setY((1.0F - progress) * (float) getMeasuredHeight() + (float) Utils.dip(getContext(), 6));
    }

    public final boolean isEnabledCell() {
        return isEnabledCell;
    }

    public final void setEnabledCell(boolean value) {
        isEnabledCell = value;
        GradientDrawable d = new GradientDrawable();
        d.setColor(circleColor);
        d.setShape(GradientDrawable.OVAL);

        if (Build.VERSION.SDK_INT >= 21 && !isEnabledCell) {
            frameLayout.setBackground(new RippleDrawable(ColorStateList.valueOf(rippleColor), null, d));
        } else {
            frameLayout.postDelayed(() -> frameLayout.setBackgroundColor(0), 200L);
        }
    }

    public final MeowBottomNavigation.ClickListener getOnClickListener() {
        return onClickListener;
    }

    public final void setOnClickListener(MeowBottomNavigation.ClickListener value) {
        onClickListener = value;

        if (cellImageView != null) {
            cellImageView.setOnClickListener(it -> getOnClickListener().onClickItem(new MeowBottomNavigation.Model(0, 0)));
        }
    }

    private void initializeView() {
        allowDraw = true;
        containerView = LayoutInflater.from(getContext()).inflate(R.layout.meow_navigation_cell, this);

        frameLayout = findViewById(R.id.fl);
        cellImageView = findViewById(R.id.iv);
        circle_view = findViewById(R.id.v_circle);
        textView = findViewById(R.id.tv_count);

        draw();
    }

    private void draw() {
        if (allowDraw) {
            setIcon(icon);
            setCount(count);
            setIconSize(iconSize);
            setCountTextColor(countTextColor);
            setCountBackgroundColor(countBackgroundColor);
            setCountTypeface(countTypeface);
            setRippleColor(rippleColor);
            setOnClickListener(onClickListener);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setProgress(progress);
    }

    public final void disableCell() {
        if (isEnabledCell) {
            animateProgress(false, true);
        }
        setEnabledCell(false);
    }

    public final void enableCell(boolean isAnimate) {
        if (!isEnabledCell) {
            animateProgress(true, isAnimate);
        }
        setEnabledCell(true);
    }

    private void animateProgress(boolean enableCell, boolean isAnimate) {
        long d = enableCell ? duration : 250L;
        ValueAnimator anim = ValueAnimator.ofFloat(0.0F, 1.0F);
        anim.setStartDelay(enableCell ? d / (long) 4 : 0L);
        anim.setDuration(isAnimate ? d : 1L);
        anim.setInterpolator((new FastOutSlowInInterpolator()));
        anim.addUpdateListener(valueAnimator -> {
            float f = valueAnimator.getAnimatedFraction();
            setProgress(enableCell ? f : 1.0F - f);
        });
        anim.start();
    }
}