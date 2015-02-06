package org.cryse.lkong.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class QuickReturnUtils {
    public static final int ANIMATE_DIRECTION_UP = 12;
    public static final int ANIMATE_DIRECTION_DOWN = 13;
    private static final int TRANSLATE_DURATION_MILLIS = 200;

    private boolean mVisible = true;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private View mTargetView;
    private int mDirection;
    public QuickReturnUtils(View mTargetView, int direction) {
        this.mTargetView = mTargetView;
        this.mDirection = direction;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void show() {
        show(true);
    }

    public void hide() {
        hide(true);
    }

    public void show(boolean animate) {
        toggle(true, animate, false);
    }

    public void hide(boolean animate) {
        toggle(false, animate, false);
    }

    private void toggle(final boolean visible, final boolean animate, boolean force) {
        if (mVisible != visible || force) {
            mVisible = visible;
            int height = mTargetView.getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = mTargetView.getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = mTargetView.getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggle(visible, animate, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int translationYWithTarget;
            if(mDirection == ANIMATE_DIRECTION_DOWN) {
                translationYWithTarget = height + getMarginBottom();
            } else if(mDirection == ANIMATE_DIRECTION_UP) {
                translationYWithTarget = -(height + getMarginTop());
            } else
                throw new IllegalArgumentException("Unknown direction.");
            int translationY = visible ? 0 : translationYWithTarget;
            if (animate) {
                mTargetView.animate().setInterpolator(mInterpolator)
                        .setDuration(TRANSLATE_DURATION_MILLIS)
                        .translationY(translationY);
            } else {
                mTargetView.setTranslationY(translationY);
            }

            // On pre-Honeycomb a translated view is still clickable, so we need to disable clicks manually
            /*if (!hasHoneycombApi()) {
                setClickable(visible);
            }*/
        }
    }

    private int getMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = mTargetView.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    private int getMarginTop() {
        int marginTop = 0;
        final ViewGroup.LayoutParams layoutParams = mTargetView.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginTop = ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
        }
        return marginTop;
    }
}
