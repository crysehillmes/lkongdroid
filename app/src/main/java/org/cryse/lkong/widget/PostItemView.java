package org.cryse.lkong.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.cryse.lkong.R;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;

public class PostItemView extends FrameLayout implements Target {
    private CharSequence mMessageText = null;
    private CharSequence mAuthorName = null;
    private CharSequence mDateline = null;
    private CharSequence mAuthorInfo = null;
    private DynamicLayout mMessageLayout = null;
    private StaticLayout mAuthorInfoLayout = null;
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
        if((mMessageLayout != null && mMessageLayout.getWidth() - px_margin_16 * 2 != widthSize) || mMessageLayout == null) {
            generateMessageTextLayout();
        }
        if((mAuthorInfoLayout != null && !TextUtils.equals(mAuthorInfoLayout.getText(), mAuthorInfo)) || mAuthorInfoLayout == null) {
            generateAuthorTextLayout();
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
        int startTop = px_margin_72 + (mMessageLayout == null ? 0 : mMessageLayout.getHeight());
        int startMarginRight = px_margin_16;
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int left = viewWidth - (startMarginRight + child.getMeasuredWidth() + px_margin_8 * (count - 1 - i));
                int top = startTop + px_margin_6;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                startMarginRight = startMarginRight + child.getMeasuredWidth() + px_margin_8 * (count - 1 - i);
                child.layout(left, top, right, bottom);
            }
        }
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
        if(mAuthorInfoLayout != null) {
            canvas.save();
            int layoutHeight = mAuthorInfoLayout.getHeight();
            canvas.translate(px_margin_72, px_margin_16 + px_width_40 / 2 - layoutHeight / 2);
            mAuthorInfoLayout.draw(canvas);
            canvas.restore();
        }
        if(mMessageLayout != null) {
            canvas.save();
            canvas.translate(px_margin_16, px_margin_72);
            mMessageLayout.draw(canvas);
            canvas.restore();
        }
    }

    private int getDesiredHeight() {
        int height = px_margin_72;
        if(mMessageLayout != null) {
            height = height + mMessageLayout.getHeight();
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

    public void setAuthorInfo(CharSequence authorName, CharSequence dateline) {
        if(TextUtils.equals(mAuthorName, authorName) &&  TextUtils.equals(mDateline, dateline)) {
            return;
        }
        mAuthorName = authorName;
        mDateline = dateline;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(authorName).append('\n');
        int start = builder.length();
        int end = builder.length() + dateline.length();
        builder.append(dateline);
        builder.setSpan(new ForegroundColorSpan(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_text_color_secondary)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAuthorInfo = builder;
        generateAuthorTextLayout();
    }

    private void generateMessageTextLayout() {
        int width = getMeasuredWidth();
        if(width > 0) {
            mMessageLayout = makeNewLayout(width - px_margin_16 * 2);
            requestLayout();
            invalidate();
        } else {
            requestLayout();
            invalidate();
        }
    }

    private void generateAuthorTextLayout() {
        int width = getMeasuredWidth();
        if(width > 0) {
            mAuthorInfoLayout = new StaticLayout(mAuthorInfo, mTextPaint, width - px_margin_72 * 2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            invalidate(px_margin_72, px_margin_16, width - px_margin_72, px_margin_16 + mAuthorInfoLayout.getHeight());
        } else {
            requestLayout();
            invalidate();
        }
    }

    public void setAuthorAvatar(Drawable authorAvatar) {
        mAvatarDrawable = authorAvatar;
        mAvatarDrawable.setBounds(0, 0, px_width_40, px_width_40);
        invalidate(px_margin_16, px_margin_16, px_margin_16 + px_width_40, px_margin_16 + px_width_40);
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

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setAuthorAvatar(new BitmapDrawable(getContext().getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        setAuthorAvatar(errorDrawable);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        setAuthorAvatar(placeHolderDrawable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
