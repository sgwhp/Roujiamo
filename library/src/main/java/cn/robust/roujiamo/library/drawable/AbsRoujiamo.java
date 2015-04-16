package cn.robust.roujiamo.library.drawable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import cn.robust.roujiamo.library.Util;

/**
 * provide a thread to control animation and a paint to draw
 * Created by wuhongping on 15-4-16.
 */
public abstract class AbsRoujiamo extends Drawable {
    protected float percent;
    protected boolean open = false;
    protected boolean animating = false;
    protected Paint paint = new Paint();

    private Runnable mInvalidateTask = new Runnable() {
        @Override
        public void run() {
            invalidateSelf();
        }
    };

    public AbsRoujiamo(Context context){
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Util.dip2px(context, getStroke()));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }

    /**
     * to set the percentage the animation should animate
     * @param percent the percentage the animation should animate
     * @param invalidate need to invalidate self? if true, must be called from ui thread
     */
    public abstract void setPercentage(float percent, boolean invalidate);

    /**
     * get paint's stroke width in dp
     * @return paint's stroke width in dp
     */
    protected abstract int getStroke();

    /**
     * get animation's duration
     * @return duration
     */
    protected abstract int getDuration();

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
        int duration = getDuration();
        while(percent <= 1 && percent >= 0) {
            cur = SystemClock.uptimeMillis();
            if (open) {
                timeLapse = (int) (cur - animStartTime);
            } else {
                timeLapse = (int) (duration + animStartTime - cur);
            }
            percent = (float) timeLapse / duration;
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
    public void open(boolean open, boolean anim, boolean force){
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

    public void setOpen(boolean open){
        this.open = open;
    }

    public boolean isAnimating(){
        return animating;
    }
}
