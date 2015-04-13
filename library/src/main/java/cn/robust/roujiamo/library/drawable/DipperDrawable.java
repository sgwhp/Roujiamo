package cn.robust.roujiamo.library.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import cn.robust.roujiamo.library.Point;
import cn.robust.roujiamo.library.Util;

/**
 * @see cn.robust.roujiamo.library.view.Dipper
 * Created by wuhongping on 15-4-10.
 */
public class DipperDrawable extends Drawable {
    private static final int PADDING = 2;
    private static final int STROKE = 3;
    private static final int LENGTH = 46;
    public static final int DURATION = 700;
    private static final int ALPHA = 80;
    private int arcDuration = 0;
    private static final float SQRT_2 = FloatMath.sqrt(2);
    private boolean open = false;
    private boolean animating = false;
    private int padding;
    private int length;
    private Paint paint;
    private Paint virtualPaint;
    private float radius, x0, y0;
    private RectF arc = new RectF();
    private Path path = new Path();
    private float arcStartAngle = 135;
    private float arcSweepAngle = 360;
    private Point shortLineEnd = new Point();
    private Point shortLineStart = new Point();
    private Point longLineEnd = new Point();
    private Point longLineStart = new Point();
    /**intersection of arc and line*/
    private Point intersection = new Point();
    private Point snakeHead = new Point();
    private Point snakeTail = new Point();
    private Interpolator interpolator = new OvershootInterpolator(1.6f);
    private float percent;

    private Runnable mInvalidateTask = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };

    public DipperDrawable(Context context){
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Util.dip2px(context, STROKE));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        virtualPaint = new Paint();
        virtualPaint.setStyle(Paint.Style.STROKE);
        virtualPaint.setStrokeWidth(Util.dip2px(context, STROKE));
        virtualPaint.setAntiAlias(true);
        virtualPaint.setARGB(ALPHA, 255, 255, 255);
        padding = Util.dip2px(context, PADDING);
        length = Util.dip2px(context, LENGTH);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawArc(arc, 0, 360, false, virtualPaint);
        canvas.drawArc(arc, arcStartAngle, arcSweepAngle, false, paint);
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
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
        x0 = measuredWidth / 2.0f;
        y0 = measuredHeight / 2.0f;
        radius = Math.min(x0, y0) - padding * 2;
        float longLineLength = radius * 0.9f;
        float shortLineLength = longLineLength / 2;
        float tmp = shortLineLength / SQRT_2;
        arc.set(x0 - radius, y0 - radius, x0 + radius, y0 + radius);
        shortLineEnd.set(x0 - 1.5f * tmp, y0);
        shortLineStart.set(x0 - 0.5f * tmp, y0 + tmp);
        longLineEnd.set(x0 - 0.5f * tmp, y0 + tmp);
        longLineStart.set(x0 + 0.75f * longLineLength / SQRT_2, y0 - tmp);
        // y = kx + l
        // k = (y2 - y1) / (x2 - x1)
        float k = (shortLineStart.y - shortLineEnd.y) / (shortLineStart.x - shortLineEnd.x);
        float l = shortLineStart.y - k * shortLineStart.x;
        // (-b +- sqrt(b^2 -4ac)) / 2a
        float a = 1 + k*k;
        float b = 2*k*(l-y0) - 2*x0;
        float c = x0*x0 + y0*y0 + l*l - 2*y0*l - radius*radius;
        float x = (-b - FloatMath.sqrt(b*b - 4 * a*c)) / 2 / a;
        float y = k * x + l;
        intersection.set(x, y);
        snakeHead.set(x, y);
        snakeTail.set(x, y);
        //this is not an accurate number, since the interpolator is not linear
        arcDuration = DURATION - (int) ((shortLineEnd.x - intersection.x) /
                ((shortLineStart.x - intersection.x) + (longLineStart.x - longLineEnd.x)) * DURATION);
        arcStartAngle = (float) (Math.asin((y0 - intersection.y) / radius) / Math.PI * 180) - 180;
        setPercentage(percent, false);
    }

    private void updateValue(float percent, float arcPercent){
        arcSweepAngle = 360 * (1 - arcPercent);
        float translateX = (longLineStart.x - intersection.x) * percent;
        float tmp = (float)arcDuration / DURATION;
        float delta;
        if(percent > tmp){
            delta = Math.max(0, translateX - tmp * (longLineStart.x - intersection.x));
        } else {
            delta = 0;
        }
//        snakeTail.x = intersection.x + (shortLineEnd.x - intersection.x) * tailPercent;
//        snakeTail.y = intersection.y + (shortLineEnd.x - intersection.x) * tailPercent;
        snakeTail.x = intersection.x + delta;
        snakeTail.y = intersection.y + delta;
        snakeHead.x = intersection.x + translateX;
        if(snakeHead.x < shortLineStart.x){
            snakeHead.y = intersection.y + translateX;
        } else {
            snakeHead.y = shortLineStart.y - translateX + (shortLineStart.x - intersection.x);
        }
        path.reset();
        path.moveTo(snakeTail.x, snakeTail.y);
        if(snakeHead.x > shortLineStart.x) {
            path.lineTo(shortLineStart.x, shortLineStart.y);
        }
        path.lineTo(snakeHead.x, snakeHead.y);
        path.reset();
        path.moveTo(snakeTail.x, snakeTail.y);
        if(snakeHead.x > shortLineStart.x) {
            path.lineTo(shortLineStart.x, shortLineStart.y);
        }
        path.lineTo(snakeHead.x, snakeHead.y);
    }


    /**
     * to set the percentage the animation should animate
     * @param percent the percentage the animation should animate
     * @param invalidate need to invalidate self? if true, must be called from ui thread
     */
    public void setPercentage(float percent, boolean invalidate){
        float arcPercent;
//        float tailPercent;
        this.percent = percent;
        percent = interpolator.getInterpolation(percent);
        arcPercent = Math.min(1, percent * DURATION / arcDuration);
        arcPercent = Math.max(0, arcPercent);
//        tailPercent = Math.min(1, (percent * DURATION - arcDuration) / (DURATION - arcDuration));
//        tailPercent = Math.max(0, tailPercent);
//        tailPercent = interpolator.getInterpolation(tailPercent);
        updateValue(percent, arcPercent);
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
        virtualPaint.setColor(color);
        virtualPaint.setAlpha(ALPHA);
    }

    private void toggleAnim(){
        float percent = open ? 0 : 1;
        float tmp;
        int timeLapse;
        long cur;
        long animStartTime= SystemClock.uptimeMillis();
        while(percent >= 0 && percent <= 1) {
            cur = SystemClock.uptimeMillis();
            if (open) {
                timeLapse = (int) (cur - animStartTime);
            } else {
                timeLapse = (int) (DipperDrawable.DURATION + animStartTime - cur);
            }
            percent = (float) timeLapse / DipperDrawable.DURATION;
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

    private void open(boolean checked){
        this.open = checked;
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
    public void setOpen(boolean open, boolean anim, boolean force) {
        if (!force && this.open == open) {
            return;
        }
        this.open = open;
        if (anim) {
            open(this.open);
            return;
        } else if (open) {
            setPercentage(1, false);
        } else {
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
