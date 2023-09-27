package com.example.texttile.presentation.ui.lib.bottom_navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.texttile.R;

public class BezierView extends View {
    private Paint mainPaint;
    private Paint shadowPaint;
    private Path mainPath;
    private Path shadowPath;
    private PointF[] outerArray;
    private PointF[] innerArray;
    private PointF[] progressArray;
    private float width;
    private float height;
    private float bezierInnerWidth;
    private float bezierInnerHeight;
    private float shadowHeight;
    private int color;
    private int shadowColor;
    private float bezierX;
    private float progress;
    Context context;

    @SuppressWarnings("unused")
    @SuppressLint({"NewApi"})
    public BezierView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initializeViews();
    }

    public BezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initializeViews();
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeViews();
    }

    public BezierView(Context context) {
        super(context);
        this.context = context;
        initializeViews();
    }

    @SuppressWarnings("unused")
    public final int getColor() {
        return color;
    }

    public final void setColor(int value) {
        color = value;
        if (mainPaint != null) {
            mainPaint.setColor(color);
        }

        invalidate();
    }

    @SuppressWarnings("unused")
    public final int getShadowColor() {
        return shadowColor;
    }

    public final void setShadowColor(int value) {
        shadowColor = value;
        if (shadowPaint != null) {
            int color = ContextCompat.getColor(context, R.color.theme_extra_light_opacity);
            shadowPaint.setShadowLayer(Utils.dip_to_float(getContext(), 10), 0.0F, 0.0F, color);
        }
        invalidate();
    }

    public final float getBezierX() {
        return bezierX;
    }

    public final void setBezierX(float value) {
        if (value != bezierX) {
            bezierX = value;
            invalidate();
        }
    }

    @SuppressWarnings("unused")
    public final float getProgress() {
        return progress;
    }

    public final void setProgress(float value) {
        if (value != progress) {
            progress = value;
            if (progressArray == null)
                return;


            progressArray[1].x = bezierX - bezierInnerWidth / (float)2;
            progressArray[2].x = bezierX - bezierInnerWidth / (float)4;
            progressArray[3].x = bezierX - bezierInnerWidth / (float)4;
            progressArray[4].x = bezierX;
            progressArray[5].x = bezierX + bezierInnerWidth / (float)4;
            progressArray[6].x = bezierX + bezierInnerWidth / (float)4;
            progressArray[7].x = bezierX + bezierInnerWidth / (float)2;



            for(int i = 2; i <= 6; ++i) {
                if (progress <= 1.0F) {
                    progressArray[i].y = calculate(innerArray[i].y, outerArray[i].y);
                } else {
                    progressArray[i].y = calculate(outerArray[i].y, innerArray[i].y);
                }
            }

            if (progress == 2.0F) {
                progress = 0.0F;
            }

            invalidate();
        }
    }

    private void initializeViews() {
        shadowHeight = Utils.dip_to_float(getContext(), 8);
        setWillNotDraw(false);

        mainPath = new Path();
        shadowPath = new Path();
        outerArray = new PointF[11];
        innerArray = new PointF[11];
        progressArray = new PointF[11];

        for (int i= 0;i<11;i++) {
            outerArray[i] = new PointF();
            innerArray[i] = new PointF();
            progressArray[i] = new PointF();
        }


        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainPaint.setStrokeWidth(0f);
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.FILL);
        mainPaint.setColor(color);

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setShadowLayer(Utils.dip_to_float(getContext(), 4), 0f, 0f, shadowColor);

        setColor(color);
        setShadowColor(shadowColor);
        setLayerType(View.LAYER_TYPE_SOFTWARE, shadowPaint);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = (float)MeasureSpec.getSize(widthMeasureSpec);
        height = (float)MeasureSpec.getSize(heightMeasureSpec);

        float bezierOuterWidth = Utils.dip_to_float(getContext(), 72);
        float bezierOuterHeight = Utils.dip_to_float(getContext(), 8);
        bezierInnerWidth = Utils.dip_to_float(getContext(), 124);
        bezierInnerHeight = Utils.dip_to_float(getContext(), 16);
        float extra = shadowHeight;

        if (outerArray == null)
            return;

        outerArray[0] = new PointF(0.0F, bezierOuterHeight + extra);
        outerArray[1] = new PointF(bezierX - bezierOuterWidth / (float)2, bezierOuterHeight + extra);
        outerArray[2] = new PointF(bezierX - bezierOuterWidth / (float)4, bezierOuterHeight + extra);
        outerArray[3] = new PointF(bezierX - bezierOuterWidth / (float)4, extra);
        outerArray[4] = new PointF(bezierX, extra);
        outerArray[5] = new PointF(bezierX + bezierOuterWidth / (float)4, extra);
        outerArray[6] = new PointF(bezierX + bezierOuterWidth / (float)4, bezierOuterHeight + extra);
        outerArray[7] = new PointF(bezierX + bezierOuterWidth / (float)2, bezierOuterHeight + extra);
        outerArray[8] = new PointF(width, bezierOuterHeight + extra);
        outerArray[9] = new PointF(width, height);
        outerArray[10] = new PointF(0.0F, height);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mainPath == null) {
            return;
        }
        mainPath.reset();

        if (shadowPaint == null) {
            return;
        }
        shadowPath.reset();

        if (progress == 0.0F) {
            drawInner(canvas, true);
            drawInner(canvas, false);
        } else {
            drawProgress(canvas, true);
            drawProgress(canvas, false);
        }

    }

    private void drawInner(Canvas canvas, boolean isShadow) {
        Paint paint = isShadow ? shadowPaint : mainPaint;
        Path path = isShadow ? shadowPath : mainPath;
        calculateInner();
        if (path == null || innerArray == null) {
            return;
        }


        path.lineTo(innerArray[0].x, innerArray[0].y);
        path.lineTo(innerArray[1].x, innerArray[1].y);
        path.cubicTo(innerArray[2].x, innerArray[2].y, innerArray[3].x, innerArray[3].y, innerArray[4].x, innerArray[4].y);
        path.cubicTo(innerArray[5].x, innerArray[5].y, innerArray[6].x, innerArray[6].y, innerArray[7].x, innerArray[7].y);
        path.lineTo(innerArray[8].x , innerArray[8].y);
        path.lineTo(innerArray[9].x, innerArray[9].y);
        path.lineTo(innerArray[10].x, innerArray[10].y );

        progressArray = innerArray.clone();
        canvas.drawPath(path, paint);
    }

    private void calculateInner() {
        float extra = shadowHeight;
        innerArray[0] = new PointF(0f, bezierInnerHeight + extra);
        innerArray[1] = new PointF((bezierX - bezierInnerWidth / 2), bezierInnerHeight + extra);
        innerArray[2] = new PointF(bezierX - bezierInnerWidth / 4, bezierInnerHeight + extra);
        innerArray[3] = new PointF(bezierX - bezierInnerWidth / 4, height - extra);
        innerArray[4] = new PointF(bezierX, height - extra);
        innerArray[5] = new PointF(bezierX + bezierInnerWidth / 4, height - extra);
        innerArray[6] = new PointF(bezierX + bezierInnerWidth / 4, bezierInnerHeight + extra);
        innerArray[7] = new PointF(bezierX + bezierInnerWidth / 2, bezierInnerHeight + extra);
        innerArray[8] = new PointF(width, bezierInnerHeight + extra);
        innerArray[9] = new PointF(width, height);
        innerArray[10] = new PointF(0f, height);
    }

    private void drawProgress(Canvas canvas, boolean isShadow) {
        Paint paint = isShadow ? shadowPaint : mainPaint;
        Path path = isShadow ? shadowPath : mainPath;

        path.lineTo(progressArray[0].x, progressArray[0].y);
        path.lineTo(progressArray[1].x, progressArray[1].y);
        path.cubicTo(progressArray[2].x, progressArray[2].y, progressArray[3].x, progressArray[3].y, progressArray[4].x, progressArray[4].y);
        path.cubicTo(progressArray[5].x, progressArray[5].y, progressArray[6].x, progressArray[6].y, progressArray[7].x, progressArray[7].y);
        path.lineTo(progressArray[8].x, progressArray[8].y);
        path.lineTo(progressArray[9].x, progressArray[9].y);
        path.lineTo(progressArray[10].x, progressArray[10].y);

        canvas.drawPath(path, paint);
    }

    private float calculate(float start, float end) {
        float p = progress;
        if (p > 1.0F) {
            p = progress - 1.0F;
        }

        if (p >= 0.9F && p <= 1.0F) {
            calculateInner();
        }

        return p * (end - start) + start;
    }

}