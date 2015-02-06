package org.cryse.lkong.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.cryse.lkong.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PagerControl extends FrameLayout {
    private static final int TRANSLATE_DURATION_MILLIS = 200;

    @InjectView(R.id.widget_pager_control_button_page_indicator)
    Button mPageIndicatorButton;
    @InjectView(R.id.widget_pager_control_button_backward)
    ImageButton mPrevPageButton;
    @InjectView(R.id.widget_pager_control_button_forward)
    ImageButton mNextPageButton;

    private boolean mVisible = true;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private OnPagerControlListener mOnPagerControlListener;

    public PagerControl(Context context) {
        super(context);
    }

    public PagerControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        mPageIndicatorButton.setOnClickListener(view -> {
            if(mOnPagerControlListener != null) {
                mOnPagerControlListener.onPageIndicatorClick();
            }
        });
        mPrevPageButton.setOnClickListener(view -> {
            if(mOnPagerControlListener != null) {
                mOnPagerControlListener.onBackwardClick();
            }
        });
        mNextPageButton.setOnClickListener(view -> {
            if(mOnPagerControlListener != null) {
                mOnPagerControlListener.onForwardClick();
            }
        });
    }

    public void setOnPagerControlListener(OnPagerControlListener onPagerControlListener) {
        this.mOnPagerControlListener = onPagerControlListener;
    }

    public void setPageIndicatorText(CharSequence charSequence) {
        this.mPageIndicatorButton.setText(charSequence);
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
            int height = getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = getViewTreeObserver();
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
            int translationY = visible ? 0 : height + getMarginBottom();
            if (animate) {
                animate().setInterpolator(mInterpolator)
                        .setDuration(TRANSLATE_DURATION_MILLIS)
                        .translationY(translationY);
            } else {
                this.setTranslationY(translationY);
            }

            // On pre-Honeycomb a translated view is still clickable, so we need to disable clicks manually
            /*if (!hasHoneycombApi()) {
                setClickable(visible);
            }*/
        }
    }

    private int getMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    public interface OnPagerControlListener {
        public void onBackwardClick();
        public void onPageIndicatorClick();
        public void onForwardClick();
    }
}
