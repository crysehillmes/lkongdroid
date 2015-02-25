package org.cryse.lkong.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.cryse.lkong.R;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.gesture.Pointer;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.ImageSpanContainer;
import org.cryse.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostItemView extends FrameLayout implements Target, ImageSpanContainer {
    private long mPostId;
    private CharSequence mMessageText = null;
    private CharSequence mAuthorName = null;
    private CharSequence mDateline = null;
    private CharSequence mAuthorInfo = null;
    private String mIdentityTag = null;
    private Object mPicassoTag = null;
    private DynamicLayout mMessageLayout = null;
    private StaticLayout mAuthorInfoLayout = null;
    private Drawable mAvatarDrawable = null;
    private TextPaint mTextPaint = null;
    private Handler mHandler;
    private CharSequence mOrdinalText = null;
    private TextPaint mOrdinalPaint = null;
    Paint.FontMetrics mOrdinalFontMetrics = null;
    private int px_margin_16 = 0;
    private int px_margin_72 = 0;
    private int px_width_40 = 0;
    private int px_height_48 = 0;
    private int px_margin_8 = 0;
    private int px_margin_6 = 0;

    private ArrayList<Object> mCachedClickableSpans;
    private ArrayList<String> mImageUrls;
    private OnSpanClickListener mOnSpanClickListener;

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
        mCachedClickableSpans = new ArrayList<>();
        mImageUrls = new ArrayList<>();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        float textSize =  getResources().getDimension(R.dimen.text_size_subhead);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_text_color_primary));
        mTextPaint.linkColor = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
        mHandler = new Handler();

        mOrdinalPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mOrdinalPaint.setTextSize(getResources().getDimension(R.dimen.text_size_caption));
        mOrdinalPaint.setColor(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_text_color_secondary));
        mOrdinalFontMetrics = mOrdinalPaint.getFontMetrics();
        initTouchHandler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureUtils.getMeasurement(widthMeasureSpec, 0);
        int heightSize = MeasureUtils.getMeasurement(heightMeasureSpec, getDesiredHeight());
        if((mMessageLayout != null && mMessageLayout.getWidth() + px_margin_16 * 2 != widthSize) || mMessageLayout == null) {
            generateMessageTextLayout(widthSize);
        }

        int childHeight = px_height_48 - px_margin_6 * 2;
        int childLeft = this.getPaddingLeft();
        int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        int childWidth = childRight - childLeft;

        measureChildren(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
        int height = heightSize;
        if(mMessageLayout != null) {
            height = height + mMessageLayout.getHeight();
        }
        setMeasuredDimension(widthSize, height);
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

    private Rect mOrdinalBounds = new Rect();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int canvasWidth = canvas.getWidth();
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
        if(!TextUtils.isEmpty(mOrdinalText)) {
            mOrdinalPaint.getTextBounds(mOrdinalText.toString(), 0, mOrdinalText.length(), mOrdinalBounds);
            canvas.drawText(mOrdinalText, 0, mOrdinalText.length(), canvasWidth - px_margin_16 - mOrdinalBounds.width(), px_margin_16 + (-mOrdinalFontMetrics.top), mOrdinalPaint );
        }
    }

    private int getDesiredHeight() {
        return px_margin_72 + px_height_48;
    }


    public long getPostId() {
        return mPostId;
    }

    public void setPostId(long postId) {
        this.mPostId = postId;
    }

    public void setMessageText(CharSequence messageText) {
        if(TextUtils.equals(mMessageText, messageText)) {
            return;
        }


        mMessageText = replaceImageSpan(messageText);
        URLSpan[] urlSpans = ((Spanned)mMessageText).getSpans(0, mMessageText.length(), URLSpan.class);
        ClickableImageSpan[] clickableImageSpans = ((Spanned)mMessageText).getSpans(0, mMessageText.length(), ClickableImageSpan.class);
        mCachedClickableSpans.clear();
        mCachedClickableSpans.addAll(Arrays.asList(urlSpans));
        mCachedClickableSpans.addAll(Arrays.asList(clickableImageSpans));
        mImageUrls.clear();
        for(ClickableImageSpan span : clickableImageSpans) {
            mImageUrls.add(span.getSource());
        }
        if(mMessageLayout != null) {
            generateMessageTextLayout(getMeasuredWidth());
        }
        // generateMessageTextLayout();
    }

    private CharSequence replaceImageSpan(CharSequence sequence) {
        Spannable spannable;
        if(sequence instanceof SpannableString)
            spannable = (SpannableString)sequence;
        else
            spannable = new SpannableString(sequence);
        ImageSpan[] imageSpans = spannable.getSpans(0, sequence.length(), ImageSpan.class );
        for(ImageSpan imageSpan : imageSpans) {
            int spanStart = spannable.getSpanStart(imageSpan);
            int spanEnd = spannable.getSpanEnd(imageSpan);
            int spanFlags = spannable.getSpanFlags(imageSpan);
            if (!TextUtils.isEmpty(imageSpan.getSource()) && !imageSpan.getSource().contains("http://img.lkong.cn/bq/")) {
                Log.d("replaceImageSpan", imageSpan.getSource());
                spannable.removeSpan(imageSpan);
                spannable.setSpan(new ClickableImageSpan(
                                getContext(),
                                this,
                                getIdentityTag(),
                                getPicassoTag(),
                                imageSpan.getSource(),
                                R.drawable.image_placeholder,
                                R.drawable.image_placeholder,
                                256,
                                256,
                                DynamicDrawableSpan.ALIGN_BOTTOM),
                        spanStart,
                        spanEnd,
                        spanFlags);
            }
        }
        return spannable;
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

    private void generateMessageTextLayout(int wantWidth) {
        if(wantWidth > 0) {
            mMessageLayout = makeNewLayout(wantWidth - px_margin_16 * 2);
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
        generateMessageTextLayout(getMeasuredWidth());
    }

    public void setLinkColor(int linkColor) {
        mTextPaint.linkColor = linkColor;
        generateMessageTextLayout(getMeasuredWidth());
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        generateMessageTextLayout(getMeasuredWidth());
    }

    public void setOrdinal(CharSequence ordinal) {
        this.mOrdinalText = ordinal;
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }




    // The amount of time (in milliseconds) a gesture has to be performed.
    private static final int TIME_LIMIT = 300;

    // The amount of distance (in density-independent pixels) a Pointer has to move to trigger a gesture.
    private static final int MOVEMENT_LIMIT_DP = 12;

    // The gesture id for an invalid gesture.
    public static final int INVALID_GESTURE = -1;

    // Gesture ids for one-finger gestures.
    public static final int TAP = 0;
    public static final int SWIPE_UP = 1;
    public static final int SWIPE_DOWN = 2;
    public static final int SWIPE_LEFT = 3;
    public static final int SWIPE_RIGHT = 4;

    // Gesture ids for two-finger gestures.
    public static final int TWO_FINGER_TAP = 5;
    public static final int TWO_FINGER_SWIPE_UP = 6;
    public static final int TWO_FINGER_SWIPE_DOWN = 7;
    public static final int TWO_FINGER_SWIPE_LEFT = 8;
    public static final int TWO_FINGER_SWIPE_RIGHT = 9;
    public static final int TWO_FINGER_PINCH_IN = 10;
    public static final int TWO_FINGER_PINCH_OUT = 11;

    // The amount of distance (in pixels) a Pointer has to move, to trigger a gesture.
    private float mMovementLimitPx;

    // A list of Pointers involved in a gesture.
    private ArrayList<Pointer> mPointers;

    public void initTouchHandler() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float mDisplayDensity = displayMetrics.density;

        mMovementLimitPx = MOVEMENT_LIMIT_DP * mDisplayDensity;
    }

    private int getGestureId() {
        int mTotalPointerCount = mPointers.size();

        if (mTotalPointerCount == 1) {
            Pointer mPointer = mPointers.get(0);

            if (mPointer.existedWithinTimeLimit(TIME_LIMIT)) {
                if (mPointer.tapped() && mPointer.existedWithinTimeLimit(100)) {
                    return TAP;
                } else if (mPointer.swipedUp()) {
                    return SWIPE_UP;
                } else if (mPointer.swipedDown()) {
                    return SWIPE_DOWN;
                } else if (mPointer.swipedLeft()) {
                    return SWIPE_LEFT;
                } else if (mPointer.swipedRight()) {
                    return SWIPE_RIGHT;
                } else {
                    return INVALID_GESTURE;
                }
            } else {
                return INVALID_GESTURE;
            }
        } else if (mTotalPointerCount == 2) {
            Pointer mPointerI = mPointers.get(0);
            Pointer mPointerII = mPointers.get(1);

            if (mPointerI.existedWithinTimeLimit(TIME_LIMIT) &&
                    mPointerII.existedWithinTimeLimit(TIME_LIMIT)) {

                if (mPointerI.tapped() &&
                        mPointerII.tapped()) {

                    return TWO_FINGER_TAP;
                } else if (mPointerI.swipedUp() &&
                        mPointerII.swipedUp()) {

                    return TWO_FINGER_SWIPE_UP;
                } else if (mPointerI.swipedDown() &&
                        mPointerII.swipedDown()) {

                    return TWO_FINGER_SWIPE_DOWN;
                } else if (mPointerI.swipedLeft() &&
                        mPointerII.swipedLeft()) {

                    return TWO_FINGER_SWIPE_LEFT;
                } else if (mPointerI.swipedRight() &&
                        mPointerII.swipedRight()) {

                    return TWO_FINGER_SWIPE_RIGHT;
                } else if (mPointerI.pinchedIn(mPointerII, mMovementLimitPx)) {
                    return TWO_FINGER_PINCH_IN;
                } else if (mPointerI.pinchedOut(mPointerII, mMovementLimitPx)) {
                    return TWO_FINGER_PINCH_OUT;
                } else {
                    return INVALID_GESTURE;
                }
            } else {
                return INVALID_GESTURE;
            }
        } else {
            return INVALID_GESTURE;
        }
    }

    public boolean onGesture(int gestureId, MotionEvent motionEvent) {
        if(gestureId == TAP) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int x = (int)motionEvent.getX();
            int y = (int)motionEvent.getY();
            if((x > (px_margin_16 ) && x < (viewWidth - px_margin_16)) && (y > (px_margin_72) && y < (px_margin_72 + (mMessageLayout == null ? 0 : mMessageLayout.getHeight())))) {
                return onTextTouched(x, y);
            }
        }
        return false;
    }

    public boolean onTextTouched(int x, int y)
    {
        //If the text contains an url, we check its location and if it is touched: fire the click event.
        Spanned spanned = (Spanned)mMessageText;

        for(int i = 0; i < mCachedClickableSpans.size(); i++)
        {
            Object span = mCachedClickableSpans.get(i);
            //get the start and end points of url span
            int start=spanned.getSpanStart(span);
            int end=spanned.getSpanEnd(span);

            Path dest = new Path();
            mMessageLayout.getSelectionPath(start, end, dest);

            RectF rectF = new RectF();
            dest.computeBounds(rectF, true);

            //Add the left and top margins of your staticLayout here.
            rectF.offset(px_margin_16 , px_margin_72);

            if(rectF.contains(x, y))
            {
                if(span instanceof URLSpan) {
                    if(mOnSpanClickListener != null)
                        return mOnSpanClickListener.onUrlSpanClick(mPostId, (URLSpan)span, ((URLSpan) span).getURL());
                    return false;
                } else if(span instanceof ClickableImageSpan){
                    if(mOnSpanClickListener != null)
                        return mOnSpanClickListener.onImageSpanClick(mPostId, (ClickableImageSpan)span, mImageUrls, ((ClickableImageSpan)span).getSource());
                    return false;
                } else
                    return false;
            }
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int mActionIndex = motionEvent.getActionIndex();

        int mPointerId = motionEvent.getPointerId(mActionIndex);
        long mEventTime = motionEvent.getEventTime();
        float mX = motionEvent.getX(mActionIndex);
        float mY = motionEvent.getY(mActionIndex);

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPointers = new ArrayList<Pointer>();

                mPointers.add(new Pointer(mPointerId,
                        mEventTime,
                        mX, mY,
                        mMovementLimitPx));
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointers.add(new Pointer(mPointerId,
                        mEventTime,
                        mX, mY,
                        mMovementLimitPx));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                for (int pIndex = mPointers.size() - 1 ; pIndex >= 0; pIndex--) {
                    if (mPointers.get(pIndex).getId() == mPointerId) {
                        mPointers.get(pIndex).setUpTime(mEventTime);
                        mPointers.get(pIndex).setUpX(mX);
                        mPointers.get(pIndex).setUpY(mY);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for (int pIndex = mPointers.size() - 1 ; pIndex >= 0; pIndex--) {
                    if (mPointers.get(pIndex).getId() == mPointerId) {
                        mPointers.get(pIndex).setUpTime(mEventTime);
                        mPointers.get(pIndex).setUpX(mX);
                        mPointers.get(pIndex).setUpY(mY);
                        break;
                    }
                }

                return onGesture(getGestureId(), motionEvent);
        }
        return motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN || motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN;
    }

    public void setIdentityTag(String identityTag) {
        this.mIdentityTag = identityTag;
    }

    public String getIdentityTag() {
        return mIdentityTag;
    }

    @Override
    public void notifyImageSpanLoaded(Object identityTag) {
        if(mIdentityTag != null && mIdentityTag.equals(identityTag)) {
            // TODO: re-layout and invalidate
            invalidate();
        }
    }

    public void setOnSpanClickListener(OnSpanClickListener listener) {
        this.mOnSpanClickListener = listener;
    }

    public Object getPicassoTag() {
        return mPicassoTag;
    }

    public void setPicassoTag(Object picassoTag) {
        this.mPicassoTag = picassoTag;
    }

    public interface OnSpanClickListener {
        public boolean onImageSpanClick(long postId, ClickableImageSpan span, ArrayList<String> urls, String initUrl);
        public boolean onUrlSpanClick(long postId, URLSpan span, String target);
    }
}
