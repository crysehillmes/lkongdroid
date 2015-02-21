package org.cryse.lkong.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
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
import android.util.TypedValue;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;

public class PostItemView extends View {
    private CharSequence mMessageText = null;
    private CharSequence mAuthorName = null;
    private CharSequence mDateline = null;
    private DynamicLayout mDynamicLayout = null;
    private Drawable mAvatarDrawable = null;
    private TextPaint mTextPaint = null;

    private int px_margin_16 = 0;
    private int px_margin_72 = 0;
    private int px_width_40 = 0;

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
        px_margin_16 = UIUtils.dp2px(getContext(), 16f);
        px_margin_72 = UIUtils.dp2px(getContext(), 72f);
        px_width_40 = UIUtils.dp2px(getContext(), 40f);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        float textSize =  getResources().getDimension(R.dimen.text_size_subhead);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_text_color_primary));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Get the width measurement
        int widthSize = MeasureUtils.getMeasurement(widthMeasureSpec, getDesiredWidth());

        //Get the height measurement
        int heightSize = MeasureUtils.getMeasurement(heightMeasureSpec, getDesiredHeight());

        //MUST call this to store the measurements
        setMeasuredDimension(widthSize, heightSize);
        if((mDynamicLayout != null && mDynamicLayout.getWidth() != widthSize) || mDynamicLayout == null) {
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

    private int getDesiredWidth() {
        return 0;
    }

    public void setMessageText(CharSequence messageText) {
        if(TextUtils.equals(mMessageText, messageText)) {
            return;
        }

        mMessageText = messageText;
        generateMessageTextLayout();
    }

    private void generateMessageTextLayout() {
        Log.d("TAGTAG", "generateMessageTextLayout");
        int width = getWidth();
        if(width > 0) {
            mDynamicLayout = new DynamicLayout(mMessageText, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, false);
            requestLayout();
            invalidate();
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
            mAvatarDrawable.draw(canvas);
        }
        if(mDynamicLayout != null) {
            canvas.translate(0, px_margin_72);
            mDynamicLayout.draw(canvas);
            canvas.save();
            canvas.restore();
        }
    }
}
