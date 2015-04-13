package cn.robust.roujiamo.library.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import cn.robust.roujiamo.library.Point;
import cn.robust.roujiamo.library.Util;
import cn.robust.roujiamo.library.view.Burger;

/**
 * @see cn.robust.roujiamo.library.view.Burger
 * Created by wuhongping on 15-4-9.
 */
public class BurgerDrawable extends Drawable {
    private static final int PADDING = 2;
    private static final int STROKE = 3;
    private static final int LENGTH = 46;
    private static final int ROTATE_DEGREE = 45;
    private static final int ARC_ROTATE_DEGREE = -135;
    public static final int DURATION = 1000;
    private static final int MIDDLE_LINE_DURATION = 700;
    // the arc animation is suppose to be right after the middle line reaching the right of arc.
    // this is not an accurate number
    private static final int ARC_START_TIME_OFFSET = 585;
    private static final int ARC_DURATION = DURATION - ARC_START_TIME_OFFSET;
    private static final int ARC_ROTATE_DURATION = DURATION - MIDDLE_LINE_DURATION;
    private boolean open = false;
    private boolean animating = false;
    private int padding;
    private int length;
    private float lineLength;
    private Paint paint = new Paint();
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
    private AnticipateOvershootInterpolator anticipateOvershootInterpolator = new AnticipateOvershootInterpolator(3.0f);
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
    private AnticipateInterpolator anticipateInterpolator = new AnticipateInterpolator();
    private float percent;

    private Runnable mInvalidateTask = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };

    public BurgerDrawable(Context context){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Util.dip2px(context, STROKE));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        padding = Util.dip2px(context, PADDING);
        length = Util.dip2px(context, LENGTH);
    }

    @Override
    public void draw(Canvas canvas) {
        // translate and rotate
        canvas.drawLine(topStartRotated.x - translateX, topStartRotated.y, topEnd.x - translateX, topEnd.y, paint);
        // just translate
        canvas.drawLine(middleTranslateStart.x, middleTranslateStart.y, middleTranslateEnd.x, middleTranslateEnd.y, paint);
        canvas.drawArc(arc, arcStartAngle, arcSweepAngle, false, paint);
        // translate and rotate
        canvas.drawLine(bottomStartRotated.x - translateX, bottomStartRotated.y, bottomEnd.x - translateX, bottomEnd.y, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return length;
    }

    @Override
    public int getIntrinsicHeight() {
        return length;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int measuredWidth = bounds.width();
        int measuredHeight = bounds.height();
        x = measuredWidth / 2.0f;
        y = measuredHeight / 2.0f;
        radius = Math.min(x, y) - padding * 2;
        lineLength = radius * 0.85f;
        arc.set(x - radius, y - radius, x + radius, y + radius);
        middleLineTranslateX = radius + lineLength;
        float paddingHeight = lineLength * FloatMath.sin((float) Math.PI * ROTATE_DEGREE / 180) / 2;
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
        setPercentage(percent, false);
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

    /**
     * to set the percentage the animation should animate
     * @param percent the percentage the animation should animate
     * @param invalidate need to invalidate self? if true, must be called from ui thread
     */
    public void setPercentage(float percent, boolean invalidate){
        float radians;
        float linePercent;
        float middleLinePercent;
        float arcRotatePercent;
        float arcPercent;
        this.percent = percent;
        linePercent = anticipateOvershootInterpolator.getInterpolation(percent);
        radians = (float) Math.PI * ROTATE_DEGREE * linePercent / 180;
        middleLinePercent = percent * DURATION / MIDDLE_LINE_DURATION;
        middleLinePercent = anticipateInterpolator.getInterpolation(middleLinePercent);
        arcPercent = Math.max(0, (percent * DURATION - ARC_START_TIME_OFFSET) / ARC_DURATION);
        arcPercent = accelerateDecelerateInterpolator.getInterpolation(arcPercent);
        arcRotatePercent = Math.max(0, (percent * DURATION - MIDDLE_LINE_DURATION) / ARC_ROTATE_DURATION);
        updateValue(radians, middleLinePercent, arcRotatePercent, arcPercent);
        if(invalidate){
            invalidateSelf();
        }
    }

    public float getPercentage(){
        return percent;
    }

    /**
     * to set the icon's color
     * @param color the color you want
     */
    public void setColor(int color){
        paint.setColor(color);
        invalidateSelf();
    }

    private void toggleAnim(){
        float percent = open ? 0 : 1;
        int timeLapse;
        long cur;
        float tmp;
        long animStartTime = SystemClock.uptimeMillis();
        while(percent <= 1 && percent >= 0) {
            cur = SystemClock.uptimeMillis();
            if (open) {
                timeLapse = (int) (cur - animStartTime);
            } else {
                timeLapse = (int) (BurgerDrawable.DURATION + animStartTime - cur);
            }
            percent = (float) timeLapse / BurgerDrawable.DURATION;
            tmp = Math.min(1, percent);
            tmp = Math.max(0, tmp);
            setPercentage(tmp, false);
            scheduleSelf(mInvalidateTask, cur);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        animating = false;
    }

    public void open(boolean open){
        this.open = open;
        animating = true;
        new Thread(){
            public void run(){
                toggleAnim();
            }
        }.start();
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
        this.open = open;
        if(anim){
            open(open);
            return;
        } else if(open){
            setPercentage(1, false);
        } else{
            setPercentage(0, false);
        }
        invalidateSelf();
    }

    public boolean isOpen(){
        return open;
    }

    public boolean isAnimating(){
        return animating;
    }
}
