package cn.robust.roujiamo.library.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.FloatMath;
import android.view.animation.AccelerateDecelerateInterpolator;

import cn.robust.roujiamo.library.Point;
import cn.robust.roujiamo.library.Util;

/**
 * Implementation of Google Material Design from dribbble.
 * see the <a href="https://dribbble.com/shots/1621920-Google-Material-Design-Free-AE-Project-File?list=shots&sort=popular&timeframe=year&offset=19"/>
 * Created by wuhongping on 15-4-15.
 */
public class MaterialBurgerDrawable extends AbsRoujiamoDrawable {
    private static final int PADDING = 4;
    private static final int STROKE = 4;
    private static final int LENGTH = 44;
    public static final int DURATION = 500;
    private static final int ROTATE_DEGREE = 180;
    private int stroke;
    private int padding;
    private int length;
    private float x, y;
    private Point topStart = new Point();
    private Point topTarget = new Point();
    private Point topEnd = new Point();
    private Point middleStart = new Point();
    private Point middleEnd = new Point();
    private Point bottomStart = new Point();
    private Point bottomTarget = new Point();
    private Point bottomEnd = new Point();
    private Path path = new Path();
    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

    public MaterialBurgerDrawable(Context context) {
        super(context);
        padding = Util.dip2px(context, PADDING);
        length = Util.dip2px(context, LENGTH);
        stroke = Util.dip2px(context, STROKE);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        float degree;
        if(open){
            degree = ROTATE_DEGREE * percent;
        } else {
            degree = 180 + ROTATE_DEGREE * (1 - percent);
        }
        canvas.rotate(degree, x, y);
        // setStrokeCap(Paint.Cap.ROUND) is useless for drawLine if hardwareAccelerated is true
        // However, drawPath works just fine.
        // So, we just use drawPath instead of setting activity or application's hardwareAccelerated false
        // , in case this is added to ActionBar.
        path.reset();
        path.moveTo(middleStart.x, middleStart.y);
        path.lineTo(middleEnd.x, middleEnd.y);
        canvas.drawPath(path, paint);
//        canvas.drawLine(middleStart.x, middleStart.y, middleEnd.x, middleEnd.y, paint);
//        if(percent == 1f){
//            canvas.drawPath(path, paint);
//        } else {
            float interpolation = interpolator.getInterpolation(percent);
        path.reset();
        path.moveTo(topStart.x + (topTarget.x - topStart.x) * interpolation
                , topStart.y + (topTarget.y - topStart.y) * interpolation);
        path.lineTo(topEnd.x, topEnd.y + (middleEnd.y - topEnd.y) * interpolation);
        canvas.drawPath(path, paint);
//            canvas.drawLine(topStart.x + (topTarget.x - topStart.x) * interpolation
//                    , topStart.y + (topTarget.y - topStart.y) * interpolation
//                    , topEnd.x, topEnd.y + (middleEnd.y - topEnd.y) * interpolation, paint);
        path.reset();
        path.moveTo(bottomStart.x + (bottomTarget.x - bottomStart.x) * interpolation
                , bottomStart.y + (bottomTarget.y - bottomStart.y) * interpolation);
        path.lineTo(bottomEnd.x, bottomEnd.y + (middleEnd.y - bottomEnd.y) * interpolation);
        canvas.drawPath(path, paint);
//            canvas.drawLine(bottomStart.x + (bottomTarget.x - bottomStart.x) * interpolation
//                    , bottomStart.y + (bottomTarget.y - bottomStart.y) * interpolation
//                    , bottomEnd.x, bottomEnd.y + (middleEnd.y - bottomEnd.y) * interpolation, paint);
//        }
        canvas.restore();
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
        x = measuredWidth / 2.0f;
        y = measuredHeight / 2.0f;
        float lineLength = (Math.min(measuredWidth, measuredHeight) - padding * 2) * 0.7f;
        float paddingHeight = (lineLength - stroke * 3) / 2 + 2;
        middleStart.set(x - lineLength / 2, y);
        middleEnd.set(x + lineLength / 2, y);
        topStart.set(x - lineLength / 2, y - paddingHeight);
        topTarget.set(middleEnd.x - lineLength / FloatMath.sqrt(2) * 0.67f, middleEnd.y - lineLength / FloatMath.sqrt(2) * 0.67f);
        topEnd.set(x + lineLength / 2, y - paddingHeight);
        bottomStart.set(x - lineLength / 2, y + paddingHeight);
        bottomTarget.set(middleEnd.x - lineLength / FloatMath.sqrt(2) * 0.67f, middleEnd.y + lineLength / FloatMath.sqrt(2) * 0.67f);
        bottomEnd.set(x + lineLength / 2, y + paddingHeight);
        path.moveTo(topTarget.x, topTarget.y);
        path.lineTo(middleEnd.x, middleEnd.y);
        path.lineTo(bottomTarget.x, bottomTarget.y);
    }

    /**
     * to set the percentage the animation should animate
     * @param percent the percentage the animation should animate
     * @param invalidate need to invalidate self? if true, must be called from ui thread
     */
    @Override
    public void setPercentage(float percent, boolean invalidate){
        this.percent = percent;
        if(invalidate){
            invalidateSelf();
        }
    }

    @Override
    protected int getStroke() {
        return STROKE;
    }

    @Override
    protected int getDuration() {
        return DURATION;
    }
}
