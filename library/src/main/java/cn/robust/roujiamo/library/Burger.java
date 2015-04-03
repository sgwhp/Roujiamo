package cn.robust.roujiamo.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

/**
 * Implementation of Open & Close from dribbble
 * @link https://dribbble.com/shots/1623679-Open-Close?list=shots&sort=popular&timeframe=year&offset=0
 * Created by sgwhp on 15-3-30.
 */
public class Burger extends View implements View.OnClickListener {
    private static final int PADDING = 2;
    private static final int STROKE = 3;
    private static final int LENGTH = 48;
    private static final int ROTATE_DEGREE = 45;
    private static final int ARC_ROTATE_DEGREE = -135;
    private static final int DURATION = 1000;
    private static final int MIDDLE_LINE_DURATION = 700;
    //this is not an accurate number
    private static final int ARC_START_TIME_OFFSET = 585;
    private static final int ARC_DURATION = DURATION - ARC_START_TIME_OFFSET;
    private static final int ARC_ROTATE_DURATION = DURATION - MIDDLE_LINE_DURATION;
    private int padding;
    private int length;
    private float lineLength = 30;
    private boolean open = false;
    private boolean animating = false;
    private Paint paint;
    private float radius, x, y;
    private RectF arc = new RectF();
    private float arcStartAngle;
    private float arcSweepAngle;
    private float translateX;
    private float middleLineTranslateX;
    private Point topStart = new Point();
    private Point topStartRotated = new Point();
    private Point topEnd = new Point();
    private Point middleStart = new Point();
    private Point middleTranslateStart = new Point();
    private Point middleEnd = new Point();
    private Point middleTranslateEnd = new Point();
    private Point bottomStart = new Point();
    private Point bottomStartRotated = new Point();
    private Point bottomEnd = new Point();
    private long animStartTime;
    private AnticipateOvershootInterpolator anticipateOvershootInterpolator = new AnticipateOvershootInterpolator();
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    private AnticipateInterpolator anticipateInterpolator = new AnticipateInterpolator();
    private OnOpenListener mListener;

    public Burger(Context context) {
        super(context);
        init(context);
    }

    public Burger(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Burger(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setClickable(true);
        setOnClickListener(this);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Util.dip2px(context, STROKE));
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        padding = Util.dip2px(context, PADDING);
        length = Util.dip2px(context, LENGTH);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.open = this.open;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        final SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.open = ss.open;
        post(new Runnable(){

            @Override
            public void run() {
                setOpen(open, false, true);
            }
        });
    }

    @Override
     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth;
        int measuredHeight;
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if(wMode != MeasureSpec.EXACTLY){
            measuredWidth = length;
        } else {
            measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        if(hMode != MeasureSpec.EXACTLY){
            measuredHeight = length;
        } else {
            measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(!changed){
            return;
        }
        int measuredWidth = right - left;
        int measuredHeight = bottom - top;
        x = measuredWidth / 2.0f;
        y = measuredHeight / 2.0f;
        radius = Math.min(x, y) - padding * 2;
        lineLength = radius * 0.85f;
        arc.set(x - radius, y - radius, x + radius, y + radius);
        middleLineTranslateX = radius + lineLength;
        float paddingHeight = lineLength * FloatMath.sin((float)Math.PI * ROTATE_DEGREE / 180) / 2;
        topStart.set(x - lineLength / 2, y - paddingHeight);
        topStartRotated.set(x - lineLength / 2, y - paddingHeight);
        topEnd.set(x + lineLength / 2, y - paddingHeight);
        middleStart.set(x - lineLength / 2, y);
        middleTranslateStart.set(x - lineLength / 2, y);
        middleEnd.set(x + lineLength / 2, y);
        middleTranslateEnd.set(x + lineLength / 2, y);
        bottomStart.set(x - lineLength / 2, y + paddingHeight);
        bottomStartRotated.set(x - lineLength / 2, y + paddingHeight);
        bottomEnd.set(x + lineLength / 2, y + paddingHeight);
    }

    @Override
     protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // translate and rotate
        canvas.drawLine(topStartRotated.x - translateX, topStartRotated.y, topEnd.x - translateX, topEnd.y, paint);
        // just translate
        canvas.drawLine(middleTranslateStart.x, middleTranslateStart.y, middleTranslateEnd.x, middleTranslateEnd.y, paint);
        canvas.drawArc(arc, arcStartAngle, arcSweepAngle, false, paint);
        // translate and rotate
        canvas.drawLine(bottomStartRotated.x - translateX, bottomStartRotated.y, bottomEnd.x - translateX, bottomEnd.y, paint);
    }

    private void updateValue(float radians, float middleLinePercent, float arcRotatePercent, float arcPercent){
        float cos = FloatMath.cos(radians);
        translateX = lineLength * (1 - cos) / 2;
        // rotate
        // (x0,y0) is after (x,y) rotating around (rx0, ry0)
        // x0= (x - rx0)*cos(a) - (y - ry0)*sin(a) + rx0 ;
        // y0= (x - rx0)*sin(a) + (y - ry0)*cos(a) + ry0 ;
        topStartRotated.x = (topStart.x - topEnd.x) * FloatMath.cos(-radians)
                - (topStart.y - topEnd.y) * FloatMath.sin(-radians) + topEnd.x;
        topStartRotated.y = (topStart.x - topEnd.x) * FloatMath.sin(-radians)
                + (topStart.y - topEnd.y) * FloatMath.cos(-radians) + topEnd.y;

        middleTranslateStart.x = Math.min(middleStart.x + middleLinePercent * middleLineTranslateX, arc.right);
        middleTranslateEnd.x = Math.min(middleEnd.x + middleLinePercent * middleLineTranslateX, arc.right);

        arcStartAngle = ARC_ROTATE_DEGREE * arcRotatePercent;
        arcSweepAngle = -360 * arcPercent;

        bottomStartRotated.x = (bottomStart.x - bottomEnd.x) * cos
                - (bottomStart.y - bottomEnd.y) * FloatMath.sin(radians) + bottomEnd.x;
        bottomStartRotated.y = (bottomStart.x - bottomEnd.x) * FloatMath.sin(radians)
                - (bottomStart.y - bottomEnd.y) * cos + bottomEnd.y;
    }

    private void toggleAnim(){
        float percent = 0;
        float middleLinePercent = 0;
        float arcPercent = 0;
        float arcRotatePercent = 0;
        float radians;
        float linePercent;
        if(!open){
            percent = 0.9999f;
            middleLinePercent = 1;
            arcPercent = 1;
        }
        int timeLapse = 0;
        long cur;
        while(percent < 1 && percent >= 0) {
            cur = System.currentTimeMillis();
            if (animStartTime == 0) {
                animStartTime = cur;
            } else {
                if (open) {
                    timeLapse = (int) (cur - animStartTime);
                } else {
                    timeLapse = (int) (DURATION + animStartTime - cur);
                }
                percent = Math.min(1, (float) timeLapse / DURATION);
                middleLinePercent = Math.min(1, (float) timeLapse / MIDDLE_LINE_DURATION);
            }
            linePercent = anticipateOvershootInterpolator.getInterpolation(percent);
            middleLinePercent = anticipateInterpolator.getInterpolation(middleLinePercent);
            radians = (float) Math.PI * ROTATE_DEGREE * linePercent / 180;

//            if (middleTranslateEnd.x >= arc.right && ARC_START_TIME_OFFSET == 0) {
//                ARC_START_TIME_OFFSET = (int) (cur - animStartTime);
//                ARC_DURATION = DURATION - ARC_START_TIME_OFFSET;
//            } else
            if (timeLapse != 0) {
                arcPercent = Math.min(1, (float)(timeLapse - ARC_START_TIME_OFFSET) / ARC_DURATION);
                arcPercent = Math.max(0, arcPercent);
                arcPercent = accelerateDecelerateInterpolator.getInterpolation(arcPercent);
            }
            if (timeLapse != 0){
                arcRotatePercent = Math.min(1, (float)(timeLapse - MIDDLE_LINE_DURATION) / ARC_ROTATE_DURATION);
                arcRotatePercent = Math.max(0, arcRotatePercent);
                arcRotatePercent = accelerateDecelerateInterpolator.getInterpolation(arcRotatePercent);
            }
            updateValue(radians, middleLinePercent, arcRotatePercent, arcPercent);
            postInvalidate();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        animating = false;
        animStartTime = 0;
    }

    private void open(boolean open){
        this.open = open;
        animating = true;
        new Thread(){
            public void run(){
                toggleAnim();
            }
        }.start();
        if(mListener != null){
            mListener.onOpen(open);
        }
    }

    @Override
    public void onClick(View v) {
        if(!animating){
            open(!open);
        }
    }


    /**
     * to set the status. Must called after onLayout. You can use the post method
     * @see android.view.View#post(Runnable)
     * @param open status to be set
     * @param anim need animation or not
     * @param force force to update even the status is the same
     */
    public void setOpen(boolean open, boolean anim, boolean force){
        if(!force && this.open == open){
            return;
        }
        if(mListener != null && this.open != open){
            mListener.onOpen(open);
        }
        this.open = open;
        if(anim){
            open(open);
            return;
        } else if(open){
            updateValue((float) Math.PI * ROTATE_DEGREE / 180, 1, 1, 1);
        } else{
            updateValue(0, 0, 0, 0);
        }
        invalidate();
    }

    /**
     * to set the status. Must called after onLayout. You can use the post method
     * @see android.view.View#post(Runnable)
     * @param open status to be set
     * @param anim need animation or not
     */
    public void setOpen(boolean open, boolean anim){
        setOpen(open, anim, false);
    }

    /**
     * to set the icon's color
     * @param color the color you want
     */
    public void setColor(int color){
        paint.setColor(color);
    }

    public void setOnOpenListener(OnOpenListener listener){
        mListener = listener;
    }

    /**
     * Listening to the status of Burger
     */
    public static interface OnOpenListener {
        /**
         * being called when burger's status changed
         * @param open open:三 --> X, close:X --> 三
         */
        public void onOpen(boolean open);
    }

    private static class SavedState extends BaseSavedState {
        boolean open;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.open = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.open ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
