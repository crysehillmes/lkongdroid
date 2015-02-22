package org.cryse.lkong.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import org.cryse.lkong.R;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;

public class PostItemView extends FrameLayout {
    private CharSequence mMessageText = null;
    private CharSequence mAuthorName = null;
    private CharSequence mDateline = null;
    private DynamicLayout mDynamicLayout = null;
    private Drawable mAvatarDrawable = null;
    private TextPaint mTextPaint = null;
    private Handler mHandler;
    private int px_margin_16 = 0;
    private int px_margin_72 = 0;
    private int px_width_40 = 0;
    private int px_height_48 = 0;
    private int px_margin_8 = 0;
    private int px_margin_6 = 0;

    public PostItemView(Context context) {
        super(context);
        init();
    }

    public PostItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PostItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PostItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        px_margin_16 = UIUtils.dp2px(getContext(), 16f);
        px_margin_72 = UIUtils.dp2px(getContext(), 72f);
        px_width_40 = UIUtils.dp2px(getContext(), 40f);
        px_height_48 = UIUtils.dp2px(getContext(), 48f);
        px_margin_8 = UIUtils.dp2px(getContext(), 8f);
        px_margin_6 = UIUtils.dp2px(getContext(), 6f);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        float textSize =  getResources().getDimension(R.dimen.text_size_subhead);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_text_color_primary));
        mTextPaint.linkColor = Color.RED;
        mHandler = new Handler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Log.d("ViewSize", String.format("getMeasuredWidth %d, getMeasuredHeight %d", getMeasuredWidth(), getMeasuredHeight()));
        //Get the width measurement

        int widthSize = getMeasuredWidth();

        //Get the height measurement
        int heightSize = getDesiredHeight() + getMeasuredHeight() + (getMeasuredHeight() != 0 ? px_margin_6 * 2 : 0);

        //MUST call this to store the measurements
        setMeasuredDimension(widthSize, heightSize);
        Log.d("ViewSize", String.format("getMeasuredWidth %d, getMeasuredHeight %d", getMeasuredWidth(), getMeasuredHeight()));
        if((mDynamicLayout != null && mDynamicLayout.getWidth() - px_margin_16 * 2 != widthSize) || mDynamicLayout == null) {
            Log.d("TAGTAG", String.format("onMeasure will go to generateMessageTextLayout: widthSize: %d", widthSize));
            generateMessageTextLayout();
        }
    }

    private int getDesiredHeight() {
        int height = px_margin_72;
        if(mDynamicLayout != null) {
            height = height + mDynamicLayout.getHeight();
        }
        return height;
    }

    public void setMessageText(CharSequence messageText) {
        if(TextUtils.equals(mMessageText, messageText)) {
            return;
        }

        mMessageText = messageText;
        generateMessageTextLayout();
    }

    private void generateMessageTextLayout() {
        int width = getMeasuredWidth();
        Log.d("TAGTAG", String.format("generateMessageTextLayout: width: %d", width));
        if(width > 0) {
            Log.d("TAGTAG", "generateMessageTextLayout11111");
            mDynamicLayout = makeNewLayout(width - px_margin_16 * 2);
            Log.d("TAGTAG", String.format("generateMessageTextLayout height %d", mDynamicLayout.getHeight()));
        } else {
            Log.d("TAGTAG", "generateMessageTextLayout22222");
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            mHandler.post(this::requestLayout);
        }
        final int count = getChildCount();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Log.d("ViewSize", String.format("width %d, height %d", viewWidth, viewHeight));
        int startTop = px_margin_72 + (mDynamicLayout == null ? 0 : mDynamicLayout.getHeight());
        int startMarginRight = px_margin_16;
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int left = viewWidth - (startMarginRight + child.getMeasuredWidth() + px_margin_8 * (count - 1 - i));
                int top = startTop + px_margin_6;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                Log.d("Position", String.format("left %d, top %d, right %d, bottom %d", left, top, right, bottom));
                startMarginRight = startMarginRight + child.getMeasuredWidth() + px_margin_8 * (count - 1 - i);
                child.layout(left, top, right, bottom);
            }
        }
    }

    public void setAuthor(CharSequence authorName, Drawable authorAvatar) {
        mAuthorName = authorName;
        mAvatarDrawable = authorAvatar;
        mAvatarDrawable.setBounds(0, 0, px_width_40, px_width_40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mAvatarDrawable != null) {
            canvas.save();
            canvas.translate(px_margin_16, px_margin_16);
            mAvatarDrawable.draw(canvas);
            canvas.restore();
        }
        if(mDynamicLayout != null) {
            canvas.save();
            canvas.translate(px_margin_16, px_margin_72);
            mDynamicLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        generateMessageTextLayout();
    }

    public void setLinkColor(int linkColor) {
        mTextPaint.linkColor = linkColor;
        generateMessageTextLayout();
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        generateMessageTextLayout();
    }

    private DynamicLayout makeNewLayout(int wantWidth) {
        if(isInEditMode()) return null;
        return new DynamicLayout(mMessageText, mTextPaint, wantWidth, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, false);
    }
}
