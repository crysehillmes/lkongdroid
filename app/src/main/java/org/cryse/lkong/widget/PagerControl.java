package org.cryse.lkong.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;

import org.cryse.lkong.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PagerControl extends CardView {
    private OnPagerControlListener mOnPagerControlListener;

    @InjectView(R.id.widget_pager_control_button_page_indicator)
    Button mPageIndicatorButton;
    @InjectView(R.id.widget_pager_control_button_backward)
    ImageButton mPrevPageButton;
    @InjectView(R.id.widget_pager_control_button_forward)
    ImageButton mNextPageButton;
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

    public interface OnPagerControlListener {
        public void onBackwardClick();
        public void onPageIndicatorClick();
        public void onForwardClick();
    }
}
