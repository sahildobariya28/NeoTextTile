package com.example.texttile.presentation.ui.lib.bottom_navigation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.texttile.R;

import java.util.ArrayList;
import java.util.Iterator;

public class MeowBottomNavigation extends FrameLayout {

    public interface ClickListener {
        void onClickItem(Model item);
    }

    public interface ShowListener {
        void onShowItem(Model item);
    }

    public interface ReselectListener {
        void onReselectItem(Model item);
    }

    public ArrayList<Model> models;
    public ArrayList<MeowBottomNavigationCell> cells;
    private boolean callListenerWhenIsSelected;
    private int selectedId;
    private ClickListener onClickedListener;
    private ShowListener onShowListener;
    private ReselectListener onReselectListener;
    private int heightCell;
    private boolean isAnimating;
    private int defaultIconColor;
    private int selectedIconColor;
    private int backgroundBottomColor;
    private int circleColor;
    private int shadowColor;
    private int countTextColor;
    private int countBackgroundColor;

    private Typeface countTypeface;
    private int rippleColor;
    private boolean allowDraw;
    private LinearLayout ll_cells;
    private BezierView bezierView;

    public MeowBottomNavigation(Context context) {
        super(context);
        models = new ArrayList<>();
        cells = new ArrayList<>();
        selectedId = -1;
        defaultIconColor = Color.parseColor("#757575");
        selectedIconColor = Color.parseColor("#2196f3");
        backgroundBottomColor = Color.parseColor("#ffffff");
        circleColor = Color.parseColor("#ffffff");
        shadowColor = -4539718;
        countTextColor = Color.parseColor("#ffffff");
        countBackgroundColor = Color.parseColor("#ff0000");
        rippleColor = Color.parseColor("#757575");
        heightCell = Utils.dip(getContext(), 72);
        initializeViews();
    }

    public MeowBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        models = new ArrayList<>();
        cells = new ArrayList<>();
        selectedId = -1;
        defaultIconColor = Color.parseColor("#757575");
        selectedIconColor = Color.parseColor("#2196f3");
        backgroundBottomColor = Color.parseColor("#ffffff");
        circleColor = Color.parseColor("#ffffff");
        shadowColor = -4539718;
        countTextColor = Color.parseColor("#ffffff");
        countBackgroundColor = Color.parseColor("#ff0000");
        rippleColor = Color.parseColor("#757575");
        heightCell = Utils.dip(getContext(), 72);
        setAttributeFromXml(context, attrs);
        initializeViews();
    }

    public MeowBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        models = new ArrayList<>();
        cells = new ArrayList<>();
        selectedId = -1;
        defaultIconColor = Color.parseColor("#757575");
        selectedIconColor = Color.parseColor("#2196f3");
        backgroundBottomColor = Color.parseColor("#ffffff");
        circleColor = Color.parseColor("#ffffff");
        shadowColor = -4539718;
        countTextColor = Color.parseColor("#ffffff");
        countBackgroundColor = Color.parseColor("#ff0000");
        rippleColor = Color.parseColor("#757575");
        heightCell = Utils.dip(getContext(), 72);
        setAttributeFromXml(context, attrs);
        initializeViews();
    }

    public final ArrayList<MeowBottomNavigationCell> getCells() {
        return cells;
    }

    public final int getDefaultIconColor() {
        return defaultIconColor;
    }

    public final void setDefaultIconColor(int value) {
        defaultIconColor = value;
        updateAllIfAllowDraw();
    }

    public final int getSelectedIconColor() {
        return selectedIconColor;
    }

    public final void setSelectedIconColor(int value) {
        selectedIconColor = value;
        updateAllIfAllowDraw();
    }

    public final int getBackgroundBottomColor() {
        return backgroundBottomColor;
    }

    public final void setBackgroundBottomColor(int value) {
        backgroundBottomColor = value;
        updateAllIfAllowDraw();
    }

    public final int getCircleColor() {
        return circleColor;
    }

    public final void setCircleColor(int value) {
        circleColor = value;
        updateAllIfAllowDraw();
    }

    public final int getCountTextColor() {
        return countTextColor;
    }

    public final void setCountTextColor(int value) {
        countTextColor = value;
        updateAllIfAllowDraw();
    }

    public final int getCountBackgroundColor() {
        return countBackgroundColor;
    }

    public final void setCountBackgroundColor(int value) {
        countBackgroundColor = value;
        updateAllIfAllowDraw();
    }


    public final Typeface getCountTypeface() {
        return countTypeface;
    }

    public final void setCountTypeface(Typeface value) {
        countTypeface = value;
        updateAllIfAllowDraw();
    }


    private void setAttributeFromXml(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MeowBottomNavigation, 0, 0);

        try {
            setDefaultIconColor(array.getColor(R.styleable.MeowBottomNavigation_mbn_defaultIconColor, defaultIconColor));
            setSelectedIconColor(array.getColor(R.styleable.MeowBottomNavigation_mbn_selectedIconColor, selectedIconColor));
            setBackgroundBottomColor(array.getColor(R.styleable.MeowBottomNavigation_mbn_backgroundBottomColor, backgroundBottomColor));
            setCircleColor(array.getColor(R.styleable.MeowBottomNavigation_mbn_circleColor, circleColor));
            setCountTextColor(array.getColor(R.styleable.MeowBottomNavigation_mbn_countTextColor, countTextColor));
            setCountBackgroundColor(array.getColor(R.styleable.MeowBottomNavigation_mbn_countBackgroundColor, countBackgroundColor));
            rippleColor = array.getColor(R.styleable.MeowBottomNavigation_mbn_rippleColor, rippleColor);
            shadowColor = array.getColor(R.styleable.MeowBottomNavigation_mbn_shadowColor, shadowColor);
            String typeface = array.getString(R.styleable.MeowBottomNavigation_mbn_countTypeface);

            if (typeface != null) {
                if (((CharSequence) typeface).length() > 0) {
                    setCountTypeface(Typeface.createFromAsset(context.getAssets(), typeface));
                }
            }
        } finally {
            array.recycle();
        }

    }

    private void initializeViews() {
        ll_cells = new LinearLayout(getContext());

        LayoutParams params = new LayoutParams(-1, heightCell);
        params.gravity = 80;
        ll_cells.setLayoutParams(params);
        ll_cells.setOrientation(LinearLayout.HORIZONTAL);
        ll_cells.setClipChildren(false);
        ll_cells.setClipToPadding(false);
        bezierView = new BezierView(getContext());

        bezierView.setLayoutParams(new LayoutParams(-1, Utils.dip(getContext(), 72)));
        bezierView.setColor(backgroundBottomColor);
        bezierView.setShadowColor(shadowColor);

        addView(bezierView);
        addView(ll_cells);
        allowDraw = true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (selectedId == -1) {
            bezierView.setBezierX(Build.VERSION.SDK_INT >= 17 && getLayoutDirection() == View.LAYOUT_DIRECTION_RTL ? (float) getMeasuredWidth() + Utils.dip_to_float(getContext(), 72) : -Utils.dip_to_float(getContext(), 72));
        }else {
            show(selectedId, false);
        }
    }

    public final void add(final MeowBottomNavigation.Model model) {
        MeowBottomNavigationCell cell = new MeowBottomNavigationCell(getContext());
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(0, heightCell, 1.0F);
        cell.setLayoutParams(params);
        cell.setIcon(model.getIcon());
        cell.setCount(model.getCount());
        cell.setDefaultIconColor(defaultIconColor);
        cell.setSelectedIconColor(selectedIconColor);
        cell.setCircleColor(circleColor);
        cell.setCountTextColor(countTextColor);
        cell.setCountBackgroundColor(countBackgroundColor);
        cell.setCountTypeface(countTypeface);
        cell.setRippleColor(rippleColor);
        cell.setOnClickListener((ClickListener) item -> {
            if (isShowing(model.getId())) {
                onReselectListener.onReselectItem(model);
            }

            if (!cell.isEnabledCell() && !isAnimating) {
                show(model.getId(), true);
                onClickedListener.onClickItem(model);
            } else if (callListenerWhenIsSelected) {
                onClickedListener.onClickItem(model);
            }
        });
        cell.disableCell();

        ll_cells.addView(cell);
        cells.add(cell);
        models.add(model);
    }

    public final void set(final MeowBottomNavigation.Model model, int index) {
        MeowBottomNavigationCell cell = cells.get(index);

        cell.setIcon(model.getIcon());

        cells.set(index, cell);
        models.set(index, model);
    }

    private void updateAllIfAllowDraw() {
        if (allowDraw) {
            for (MeowBottomNavigationCell meowBottomNavigationCell : cells) {
                meowBottomNavigationCell.setDefaultIconColor(defaultIconColor);
                meowBottomNavigationCell.setSelectedIconColor(selectedIconColor);
                meowBottomNavigationCell.setCircleColor(circleColor);
                meowBottomNavigationCell.setCountTextColor(countTextColor);
                meowBottomNavigationCell.setCountBackgroundColor(countBackgroundColor);
                meowBottomNavigationCell.setCountTypeface(countTypeface);
            }
            bezierView.setColor(backgroundBottomColor);
        }
    }

    private void anim(MeowBottomNavigationCell cell, int id, boolean enableAnimation) {
        isAnimating = true;
        int pos = getModelPosition(id);
        int nowPos = getModelPosition(selectedId);
        int nPos = Math.max(nowPos, 0);
        int dif = Math.abs(pos - nPos);
        long d = (long) dif * 100L + 150L;
        long animDuration = enableAnimation ? d : 1L;
        FastOutSlowInInterpolator animInterpolator = new FastOutSlowInInterpolator();
        ValueAnimator anim = ValueAnimator.ofFloat(0.0F, 1.0F);
        anim.setDuration(animDuration + 500);
        anim.setInterpolator(animInterpolator);

        float beforeX = bezierView.getBezierX();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float f = valueAnimator.getAnimatedFraction();
                float newX = cell.getX() + (float) (cell.getMeasuredWidth() / 2);
                if (newX > beforeX) {
                    MeowBottomNavigation.this.bezierView.setBezierX(f * (newX - beforeX) + beforeX);
                } else {
                    MeowBottomNavigation.this.bezierView.setBezierX(beforeX - f * (beforeX - newX));
                }

                if (f == 1.0F) {
                    isAnimating = false;
                }
            }
        });
        anim.start();

//        if (Math.abs(pos - nowPos) > 1) {
            ValueAnimator progressAnim = ValueAnimator.ofFloat(0.0F, 1.0F);
            progressAnim.setDuration(animDuration + 500);
            progressAnim.setInterpolator(animInterpolator);
            progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float f = valueAnimator.getAnimatedFraction();
                    MeowBottomNavigation.this.bezierView.setProgress(f * 2.0F);
                }
            });
            progressAnim.start();
//        }

        cell.setFromLeft(pos > nowPos);

        for (MeowBottomNavigationCell meowBottomNavigationCell : cells) {
            meowBottomNavigationCell.setDuration(animDuration + 300);
        }

    }



    public final void show(int id, boolean enableAnimation) {

        for (int i = 0; i < models.size(); i++) {
            MeowBottomNavigation.Model model = models.get(i);
            MeowBottomNavigationCell cell = cells.get(i);

            if (model.getId() == id) {
                anim(cell, id, enableAnimation);
                cell.enableCell(enableAnimation);
                onShowListener.onShowItem(model);
            } else {
                cell.disableCell();
            }
        }

        selectedId = id;
    }

    public final boolean isShowing(int id) {
        return selectedId == id;
    }

    public final MeowBottomNavigation.Model getModelById(int id) {
        Iterator<Model> modelIterator = models.iterator();

        MeowBottomNavigation.Model it;
        do {
            if (!modelIterator.hasNext()) {
                return null;
            }

            it = modelIterator.next();
        } while (it.getId() != id);

        return it;
    }


    public final MeowBottomNavigationCell getCellById(int id) {
        return cells.get(getModelPosition(id));
    }

    public final int getModelPosition(int id) {

        for (int i = 0; i < models.size(); i++) {
            MeowBottomNavigation.Model item = models.get(i);
            if (item.getId() == id) {
                return i;
            }
        }

        return -1;
    }

    public final void setCount(int id, String count) {
        MeowBottomNavigation.Model model = getModelById(id);
        if (model != null) {
            int pos = getModelPosition(id);
            model.setCount(count);
            cells.get(pos).setCount(count);
        }
    }

    public final void clearCount(int id) {
        MeowBottomNavigation.Model model = getModelById(id);
        if (model != null) {
            int pos = getModelPosition(id);
            model.setCount("empty");
            cells.get(pos).setCount("empty");
        }
    }

    public final void clearAllCounts() {

        for (Model model : models) {
            clearCount(model.getId());
        }

    }

    public final void setOnShowListener(ShowListener listener) {
        onShowListener = listener;
    }

    public final void setOnClickMenuListener(ClickListener listener) {
        onClickedListener = listener;
    }

    public final void setOnReselectListener(ReselectListener listener) {
        onReselectListener = listener;
    }

    @SuppressWarnings("WeakerAccess")
    public static class Model {

        private String count;
        private int id;
        private int icon;

        public final String getCount() {
            return this.count;
        }

        public final void setCount(String count) {
            this.count = count;
        }

        public final int getId() {
            return id;
        }

        public final void setId(int id) {
            this.id = id;
        }

        public final int getIcon() {
            return icon;
        }

        public final void setIcon(int icon) {
            this.icon = icon;
        }

        public Model(int id, int icon) {
            this.id = id;
            this.icon = icon;
            count = "empty";
        }
    }

}