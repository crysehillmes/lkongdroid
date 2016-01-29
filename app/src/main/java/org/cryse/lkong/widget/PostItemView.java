package org.cryse.lkong.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.style.UpdateLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostDisplayCache;
import org.cryse.lkong.utils.ThemeUtils;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.gesture.Pointer;
import org.cryse.lkong.utils.htmltextview.AsyncDrawableType;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.ImageSpanContainer;
import org.cryse.lkong.utils.htmltextview.PendingImageSpan;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;

import java.util.ArrayList;

public class PostItemView extends View implements ImageSpanContainer {
    private long mPostId;
    private boolean mIsNightMode = false;
    private String mIdentityTag = null;
    private CharSequence mOrdinalText = null;
    private TextPaint mOrdinalPaint = null;
    Paint.FontMetrics mOrdinalFontMetrics = null;
    private static int px_margin_16 = 0;
    private static int px_margin_72 = 0;
    private static int px_width_40 = 0;
    private static int px_height_68 = 0;
    private static int px_margin_8 = 0;


    private OnSpanClickListener mOnSpanClickListener;
    private OnTextLongPressedListener mOnTextLongPressedListener;
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
        setLayerType(LAYER_TYPE_NONE, null);
        setDrawingCacheEnabled(false);

        if(isInEditMode()) return;
        mIsNightMode = Prefs.getBoolean(
                PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE,
                PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE
        );

        if(px_width_40 == 0) {
            px_margin_16 = UIUtils.dp2px(getContext(), 16f);
            px_margin_72 = UIUtils.dp2px(getContext(), 72f);
            px_width_40 = UIUtils.dp2px(getContext(), 40f);
            px_height_68 = UIUtils.dp2px(getContext(), 68f);
            px_margin_8 = UIUtils.dp2px(getContext(), 8f);
        }

        mOrdinalPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mOrdinalPaint.setTextSize(getResources().getDimension(R.dimen.text_size_caption));
        mOrdinalPaint.setColor(ThemeUtils.textColorSecondary(getContext()));
        mOrdinalFontMetrics = mOrdinalPaint.getFontMetrics();
        initTouchHandler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height = getDesiredHeight();
        if(!isInEditMode() && mPostDisplayCache.getTextLayout() != null) {
            height = height + mPostDisplayCache.getTextLayout().getHeight();
        }
        setMeasuredDimension(widthSize, height);
    }

    private Rect mOrdinalBounds = new Rect();
    private Rect mClipBounds = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(mClipBounds);
        int canvasWidth = canvas.getWidth();
        if(isInEditMode()) return;
        if(mPostDisplayCache.getAuthorLayout() != null) {
            canvas.save();
            canvas.translate(px_margin_72, px_margin_16);
            mPostDisplayCache.getAuthorLayout().draw(canvas);
            canvas.restore();
        }
        if(mPostDisplayCache.getTextLayout() != null) {
            canvas.save();
            canvas.translate(px_margin_16, px_margin_72);
            mPostDisplayCache.getTextLayout().draw(canvas);
            canvas.restore();
        }
        if(!TextUtils.isEmpty(mOrdinalText)) {
            mOrdinalPaint.getTextBounds(mOrdinalText.toString(), 0, mOrdinalText.length(), mOrdinalBounds);
            canvas.drawText(mOrdinalText, 0, mOrdinalText.length(), canvasWidth - px_margin_16 - mOrdinalBounds.width(), px_margin_16 + (-mOrdinalFontMetrics.top), mOrdinalPaint );
        }
    }

    private int getDesiredHeight() {
        return px_margin_72 + px_height_68;
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

    PostDisplayCache mPostDisplayCache;

    public void setPostDisplayCache(PostDisplayCache postDisplayCache) {
        mPostDisplayCache = postDisplayCache;
        /*for(int i = 0; i < postDisplayCache.getEmoticonSpans().size(); i++) {
            PendingImageSpan pendingImageSpan = (PendingImageSpan) mPostDisplayCache.getEmoticonSpans().get(i);
            pendingImageSpan.loadImage(this);
        }*/
        requestLayout();
        if(mShowImages) {
            post(() -> {
                int viewImageMaxWidth = getMeasuredWidth() - px_margin_16 * 2;
                for (int i = mPostDisplayCache.getUrlSpanCount(); i < mPostDisplayCache.getImportantSpans().size(); i++) {
                    PendingImageSpan pendingImageSpan = (PendingImageSpan) mPostDisplayCache.getImportantSpans().get(i);
                    pendingImageSpan.loadImage(
                            this,
                            viewImageMaxWidth,
                            mIsNightMode ? Color.argb(255, 15, 15, 15) : Color.argb(255, 229, 229, 229)
                    );
                }
            });
        }
    }

    public void setOrdinal(CharSequence ordinal) {
        this.mOrdinalText = ordinal;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    // The amount of time (in milliseconds) a gesture has to be performed.
    private static final int TIME_LIMIT = 300;

    // The amount of distance (in density-independent pixels) a Pointer has to move to trigger a gesture.
    private static final int MOVEMENT_LIMIT_DP = 12;

    // The gesture id for an invalid gesture.
    public static final int INVALID_GESTURE = -1;

    // Gesture ids for one-finger gestures.
    public static final int TAP = 0;
    public static final int LONG_TAP = 1112;
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

            if (mPointer.existedWithinTimeLimitMost(TIME_LIMIT)) {
                if (mPointer.tapped() && mPointer.existedWithinTimeLimitMost(100)) {
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
            } else if (mPointer.tapped() && mPointer.existedWithinTimeLimitLeast(1000)) {
                return LONG_TAP;
            } else {
                return INVALID_GESTURE;
            }
        } else if (mTotalPointerCount == 2) {
            Pointer mPointerI = mPointers.get(0);
            Pointer mPointerII = mPointers.get(1);

            if (mPointerI.existedWithinTimeLimitMost(TIME_LIMIT) &&
                    mPointerII.existedWithinTimeLimitMost(TIME_LIMIT)) {

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
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();
        if(gestureId == TAP) {
            if((x > (px_margin_16 ) && x < (viewWidth - px_margin_16)) && (y > (px_margin_72) && y < (px_margin_72 + (mPostDisplayCache.getTextLayout() == null ? 0 : mPostDisplayCache.getTextLayout().getHeight())))) {
                return onTextTouched(x, y);
            }
        } else if(gestureId == LONG_TAP) {
            if(mOnTextLongPressedListener != null && (x > (px_margin_16 ) && x < (viewWidth - px_margin_16)) && (y > (px_margin_72) && y < (px_margin_72 + (mPostDisplayCache.getTextLayout() == null ? 0 : mPostDisplayCache.getTextLayout().getHeight())))) {
                mOnTextLongPressedListener.onTextPressed(this);
            }
        }
        return false;
    }

    public boolean onTextTouched(int x, int y)
    {
        //If the text contains an url, we check its location and if it is touched: fire the click event.
        Spanned spanned = mPostDisplayCache.getSpannableStringBuilder();

        int textLayoutX = x - px_margin_16;
        int textLayoutY = y - px_margin_72;
        int touchLine = mPostDisplayCache.getTextLayout().getLineForVertical(textLayoutY);
        int lineStart = mPostDisplayCache.getTextLayout().getLineStart(touchLine);
        int lineEnd = mPostDisplayCache.getTextLayout().getLineEnd(touchLine);
        Object[] spans = spanned.getSpans(lineStart, lineEnd, Object.class);
        for (Object span : spans) {
            //get the start and end points of url span
            if(span instanceof URLSpan || span instanceof ClickableImageSpan) {
                int start = spanned.getSpanStart(span);
                int end = spanned.getSpanEnd(span);

                Path dest = new Path();
                mPostDisplayCache.getTextLayout().getSelectionPath(start, end, dest);

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

    public void setOnSpanClickListener(OnSpanClickListener listener) {
        this.mOnSpanClickListener = listener;
    }

    public void setOnTextLongPressedListener(OnTextLongPressedListener listener) {
        this.mOnTextLongPressedListener = listener;
    }

    @Override
    public void notifyImageSpanLoaded(Object identityTag, Drawable drawable, AsyncDrawableType type) {
        if(mIdentityTag != null && mIdentityTag.equals(identityTag)) {
            // TODO: re-layout and invalidate
            if(mInvalidateRunnable == null) {
                mInvalidateRunnable = () -> {
                    if(mIdentityTag != null && mIdentityTag.equals(identityTag)) {
                        if(type == AsyncDrawableType.NORMAL) {
                            SpannableStringBuilder charSequence = (SpannableStringBuilder) mPostDisplayCache.getTextLayout().getText();
                            notifyDynamicLayoutChange(charSequence);
                            requestLayout();
                            invalidate();
                        } else {
                            invalidate();
                        }
                    }
                    mInvalidateRunnable = null;
                };
                postDelayed(mInvalidateRunnable, 1000);
            }
        }
    }

    public interface OnSpanClickListener {
        boolean onImageSpanClick(long postId, ClickableImageSpan span, ArrayList<String> urls, String initUrl);
        boolean onUrlSpanClick(long postId, URLSpan span, String target);
    }

    public interface OnTextLongPressedListener {
        void onTextPressed(View iew);
    }

    private static void notifyDynamicLayoutChange(SpannableStringBuilder stringBuilder) {
        stringBuilder.setSpan(new EmptySpan(), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        EmptySpan[] emptySpans = stringBuilder.getSpans(0, stringBuilder.length(), EmptySpan.class);
        for (EmptySpan emptySpan : emptySpans) {
            stringBuilder.removeSpan(emptySpan);
        }
    }

    private static class EmptySpan implements UpdateLayout {}
}
