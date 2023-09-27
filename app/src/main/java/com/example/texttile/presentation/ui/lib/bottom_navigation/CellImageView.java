package com.example.texttile.presentation.ui.lib.bottom_navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.texttile.R;

public class CellImageView extends AppCompatImageView {

    private boolean isBitmap;
    private boolean useColor;
    private int resource;
    private int color;
    private int size;
    private boolean actionBackgroundAlpha;
    private boolean changeSize;
    private boolean fitImage;
    private boolean allowDraw;

    public CellImageView(Context context) {
        super(context);
        useColor = true;
        size = Utils.dip(getContext(), 24);
        changeSize = true;
        initializeView();
    }

    public CellImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        useColor = true;
        size = Utils.dip(getContext(), 24);
        changeSize = true;
        setAttributeFromXml(context, attrs);
        initializeView();
    }

    public CellImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        useColor = true;
        size = Utils.dip(getContext(), 24);
        changeSize = true;
        setAttributeFromXml(context, attrs);
        initializeView();
    }


    public final void setBitmap(boolean value) {
        isBitmap = value;
        draw();
    }


    public final void setUseColor(boolean value) {
        useColor = value;
        draw();
    }

    public final int getResource() {
        return resource;
    }

    public final void setResource(int value) {
        resource = value;
        draw();
    }

    public final int getColor() {
        return color;
    }

    public final void setColor(int value) {
        color = value;
        draw();
    }

    public final int getSize() {
        return size;
    }

    public final void setSize(int value) {
        size = value;
        requestLayout();
    }

    private void setAttributeFromXml(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CellImageView, 0, 0);

        try {
            setBitmap(a.getBoolean(R.styleable.CellImageView_meow_imageview_isBitmap, isBitmap));
            setUseColor(a.getBoolean(R.styleable.CellImageView_meow_imageview_useColor, useColor));
            setResource(a.getResourceId(R.styleable.CellImageView_meow_imageview_resource, resource));
            setColor(a.getColor(R.styleable.CellImageView_meow_imageview_color, color));
            setSize(a.getDimensionPixelSize(R.styleable.CellImageView_meow_imageview_size, size));
            actionBackgroundAlpha = a.getBoolean(R.styleable.CellImageView_meow_imageview_actionBackgroundAlpha, actionBackgroundAlpha);
            changeSize = a.getBoolean(R.styleable.CellImageView_meow_imageview_changeSize, changeSize);
            fitImage = a.getBoolean(R.styleable.CellImageView_meow_imageview_fitImage, fitImage);
        } finally {
            a.recycle();
        }

    }

    private void initializeView() {
        allowDraw = true;
        draw();
    }

    private void draw() {
        if (allowDraw) {
            if (resource != 0) {
                if (isBitmap) {
                    Drawable drawable;
                    if (color == 0) {
                        drawable = Utils.getDrawableCompat(getContext(), resource);
                    } else {
                        drawable = Utils.changeColorDrawableRes(getContext(), resource, color);
                    }
                    setImageDrawable(drawable);

                } else if (!useColor || color != 0) {
                    int c = useColor ? color : -2;
                    setImageDrawable(Utils.changeColorDrawableVector(getContext(), resource, c));
                }
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fitImage) {
            Drawable d = getDrawable();
            if (d != null) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) Math.ceil((double)((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth()));
                setMeasuredDimension(width, height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

        } else if (!isBitmap && changeSize) {
            int newSize = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
            super.onMeasure(newSize, newSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}