package org.cryse.lkong.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostDisplayCache;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.gesture.Pointer;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.ImageSpanContainer;
import org.cryse.lkong.utils.htmltextview.PendingImageSpan;
import org.cryse.utils.ColorUtils;

import java.util.ArrayList;

public class PostItemView extends ViewGroup implements ImageSpanContainer {
    private long mPostId;
    private CharSequence mMessageText = null;
    private CharSequence mAuthorName = null;
    private CharSequence mDateline = null;
    private CharSequence mAuthorInfo = null;
    private String mIdentityTag = null;
    private Object mPicassoTag = null;
    private StaticLayout mMessageLayout = null;
    private StaticLayout mAuthorInfoLayout = null;
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

    private OnSpanClickListener mOnSpanClickListener;
    private boolean mShowImages = true;

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
        setDrawingCacheEnabled(true);
        setChildrenDrawingCacheEnabled(true);
        setAnimationCacheEnabled(true);
        setChildrenDrawnWithCacheEnabled(true);
        setAlwaysDrawnWithCacheEnabled(true);
        px_margin_16 = UIUtils.dp2px(getContext(), 16f);
        px_margin_72 = UIUtils.dp2px(getContext(), 72f);
        px_width_40 = UIUtils.dp2px(getContext(), 40f);
        px_height_48 = UIUtils.dp2px(getContext(), 48f);
        px_margin_8 = UIUtils.dp2px(getContext(), 8f);
        px_margin_6 = UIUtils.dp2px(getContext(), 6f);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        float textSize =  UIUtils.getSpDimensionPixelSize(getContext(), R.dimen.text_size_subhead);
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
        if(mMessageLayout == null) {
            generateMessageTextLayout(widthSize, true);
        }

        int childHeight = px_height_48 - px_margin_6 * 2;
        int childLeft = this.getPaddingLeft();
        int childRight = widthSize - this.getPaddingRight();
        int childWidth = childRight - childLeft;

        measureChildren(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
        int height = heightSize;
        if(mMessageLayout != null) {
            height = height + mMessageLayout.getHeight();
        }
        Log.d("PostItemView", String.format("onMeasure for pid: %d, width = %d, height = %d", mPostId, widthSize, height));
        setMeasuredDimension(widthSize, height);
        if(mAuthorInfoLayout == null) {
            generateAuthorTextLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        Log.d("PostItemView", String.format("onLayout for pid: %d,l = %d, t = %d, r = %d, b = %d, item count = %d", mPostId, l, t, r, b, count));
        int startTop = px_margin_72 + (mMessageLayout == null ? 0 : mMessageLayout.getHeight());
        int startMarginRight = px_margin_16;
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if(i == 0) {
                    child.layout(px_margin_16, px_margin_16, px_margin_16 + px_width_40, px_margin_16 + px_width_40);
                } else {
                    int left = r - l - (startMarginRight + child.getMeasuredWidth() + px_margin_8 * (count - 1 - i));
                    int top = startTop + px_margin_6;
                    int right = left + child.getMeasuredWidth();
                    int bottom = top + child.getMeasuredHeight();
                    startMarginRight = startMarginRight + child.getMeasuredWidth() + px_margin_8 * (count - 1 - i);
                    child.layout(left, top, right, bottom);
                }
            }
        }
    }

    private Rect mOrdinalBounds = new Rect();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("PostItemView", String.format("onDraw for pid: %d", mPostId));
        int canvasWidth = canvas.getWidth();
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

    public void setShowImages(boolean show) {
        this.mShowImages = show;
    }

    public void setMessageText(CharSequence messageText) {
        if(TextUtils.equals(mMessageText, messageText)) {
            return;
        }

        mMessageText = messageText;
        if(mMessageLayout != null) {
            generateMessageTextLayout(getMeasuredWidth(), false);
        }
    }

    PostDisplayCache mPostDisplayCache;

    public void setPostDisplayCache(PostDisplayCache postDisplayCache) {
        mPostDisplayCache = postDisplayCache;
        if(mShowImages) {
            for(int i = postDisplayCache.getUrlSpanCount(); i < postDisplayCache.getImportantSpans().size(); i++) {
                PendingImageSpan pendingImageSpan = (PendingImageSpan) mPostDisplayCache.getImportantSpans().get(i);
                pendingImageSpan.loadImage(this);
            }
        }
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

    private void generateMessageTextLayout(int wantWidth, boolean fromMeasure) {
        if(wantWidth > 0) {
            long startTime = System.nanoTime();
            mMessageLayout = makeNewLayout(wantWidth - px_margin_16 * 2);
            long stopTime = System.nanoTime();
            Log.d("PostItemView", String.format("make TextLayout for pid: %d time: %d, from Measure? %b", mPostId, (stopTime - startTime) / 1000000, fromMeasure));
            if(!fromMeasure) {
                // requestLayout();
                // invalidate();
            }
        }
    }

    private void generateAuthorTextLayout() {
        int width = getMeasuredWidth();
        if(isInEditMode()) return;
        if(width > 0) {
            mAuthorInfoLayout = new StaticLayout(mAuthorInfo, mTextPaint, width - px_margin_72 * 2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            //invalidate(px_margin_72, px_margin_16, width - px_margin_72, px_margin_16 + mAuthorInfoLayout.getHeight());
        }
    }

    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        generateMessageTextLayout(getMeasuredWidth(), false);
    }

    public void setLinkColor(int linkColor) {
        mTextPaint.linkColor = linkColor;
        generateMessageTextLayout(getMeasuredWidth(), false);
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        generateMessageTextLayout(getMeasuredWidth(), false);
    }

    public void setOrdinal(CharSequence ordinal) {
        this.mOrdinalText = ordinal;
    }

    private StaticLayout makeNewLayout(int wantWidth) {
        if(isInEditMode()) return null;
        StaticLayout layout = new StaticLayout(mMessageText, mTextPaint, wantWidth, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, false);
        // setLayerType(LAYER_TYPE_NONE, null);
        /*if(layout.getLineCount() > 50)
            setLayerType(LAYER_TYPE_NONE, null);
        else
            setLayerType(LAYER_TYPE_HARDWARE, null);*/
        return layout;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }*/




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

        int textLayoutX = x - px_margin_16;
        int textLayoutY = y - px_margin_72;
        int touchLine = mMessageLayout.getLineForVertical(textLayoutY);
        int lineStart = mMessageLayout.getLineStart(touchLine);
        int lineEnd = mMessageLayout.getLineEnd(touchLine);
        Object[] spans = spanned.getSpans(lineStart, lineEnd, Object.class);
        for (Object span : spans) {
            //get the start and end points of url span
            if(span instanceof URLSpan || span instanceof ClickableImageSpan) {
                int start = spanned.getSpanStart(span);
                int end = spanned.getSpanEnd(span);

                Path dest = new Path();
                mMessageLayout.getSelectionPath(start, end, dest);

                RectF rectF = new RectF();
                dest.computeBounds(rectF, true);

                //Add the left and top margins of your staticLayout here.
                rectF.offset(px_margin_16, px_margin_72);

                if (rectF.contains(x, y)) {
                    if (span instanceof URLSpan) {
                        if (mOnSpanClickListener != null)
                            return mOnSpanClickListener.onUrlSpanClick(mPostId, (URLSpan) span, ((URLSpan) span).getURL());
                        return false;
                    } else if (span instanceof ClickableImageSpan) {
                        if (mOnSpanClickListener != null)
                            return mOnSpanClickListener.onImageSpanClick(mPostId, (ClickableImageSpan) span, mPostDisplayCache.getImageUrls(), ((ClickableImageSpan) span).getSource());
                        return false;
                    } else
                        return false;
                }
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

    Runnable mInvalidateRunnable;
    @Override
    public void notifyImageSpanLoaded(Object identityTag) {
        if(mIdentityTag != null && mIdentityTag.equals(identityTag)) {
            // TODO: re-layout and invalidate
            /*if(mInvalidateRunnable == null) {
                mInvalidateRunnable = () -> {
                    if(mIdentityTag != null && mIdentityTag.equals(identityTag)) {
                        invalidate();
                    }
                    mInvalidateRunnable = null;
                };
                mHandler.postDelayed(mInvalidateRunnable, 1000);
            }*/
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
