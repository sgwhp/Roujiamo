package cn.robust.roujiamo.library.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import cn.robust.roujiamo.library.Point;
import cn.robust.roujiamo.library.Util;

/**
 * Implementation of Play & Pause from dribbble.
 * See the <a href="https://dribbble.com/shots/1681359-Play-Pause?list=users&offset=52" />
 *  A\         AD
 *  | C  ==>   ||
 *  B/         BE
 * Created by wuhongping on 15-5-5.
 */
public class SandwichDrawable extends AbsRoujiamoDrawable {
    private static final int PADDING = 4;
    private static final int STROKE = 3;
    public static final int DURATION = 800;
    private static final int LENGTH = 44;
    private static final float SQRT_2 = FloatMath.sqrt(2);
    private static final float I_AM_360 = 359.9f;
    private int padding;
    private int length;
    private float lineLength;// line ab's length
    private float radius, x0, y0, arcLength;
    private float arcStartAngle;
    private RectF arc = new RectF();
    private Point pa = new Point();
    private Point pb = new Point();
    private Point pc = new Point();
    private Point pd = new Point();
    private Point pe = new Point();
    /**intersection of arc and line*/
    private Point intersection = new Point();
    private Path cab = new Path();
    private PathMeasure cabMeasure = new PathMeasure();
    private Path abed = new Path();
    private PathMeasure abedMeasure = new PathMeasure();
    private Path bcArc = new Path();
    private PathMeasure bcArcMeasure = new PathMeasure();
    private Path la = new Path();// line pa
    private Path lb = new Path();// line pb
    private Path lc = new Path();// line pc
    private Interpolator interpolator = new AnticipateOvershootInterpolator();

    public SandwichDrawable(Context context) {
        super(context);
        padding = Util.dip2px(context, PADDING);
        length = Util.dip2px(context, LENGTH);
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
    public void setPercentage(float percent, boolean invalidate) {
        this.percent = percent;
        percent = interpolator.getInterpolation(percent);
        lb.reset();
        float ca = lineLength / 2 * SQRT_2;
        cabMeasure.getSegment(percent * ca, percent * lineLength + ca, lb, true);
        lc.reset();
        float be = pc.x - pa.x;
        abedMeasure.getSegment(percent * (lineLength + be)
                , percent * (lineLength + be) + lineLength, lc, true);
        la.reset();
        float length = FloatMath.sqrt((intersection.x - pb.x) * (intersection.x - pb.x)
                + (intersection.y - pb.y) * (intersection.y - pb.y));
        float tmp = percent * (arcLength + length - ca) / length;
        tmp = tmp > 1 ? 1 : tmp;
        bcArcMeasure.getSegment((1-percent) * (arcLength + length - ca)
                , (1-tmp) * length + arcLength, la, true);
        if(invalidate){
            invalidateSelf();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int measuredWidth = bounds.width();
        int measuredHeight = bounds.height();
        x0 = measuredWidth / 2.0f;
        y0 = measuredHeight / 2.0f;
        radius = Math.min(x0, y0) - padding;
        lineLength = radius * 2 * 0.5f;
        arc.set(x0 - radius, y0 - radius, x0 + radius, y0 + radius);
        arcLength = (float) (2 * Math.PI * radius);
        pa.set(x0 - lineLength / 4, y0 - lineLength / 2);
        pb.set(x0 - lineLength / 4, y0 + lineLength / 2);
        pc.set(x0 + lineLength / 4, y0);
        pd.set(x0 + lineLength / 4, y0 - lineLength / 2);
        pe.set(x0 + lineLength / 4, y0 + lineLength / 2);

        cab.reset();
        cab.moveTo(pc.x, pc.y);
        cab.lineTo(pa.x, pa.y);
        cab.lineTo(pb.x, pb.y);
        cabMeasure.setPath(cab, false);

        abed.reset();
        abed.moveTo(pa.x, pa.y);
        abed.lineTo(pb.x, pb.y);
        abed.lineTo(pe.x, pe.y);
        abed.lineTo(pd.x, pd.y);
        abedMeasure.setPath(abed, false);

        // y = kx + l
        // k = (y2 - y1) / (x2 - x1)
        float k = (pc.y - pb.y) / (pc.x - pb.x);
        float l = pc.y - k * pc.x;
        // (-b +- sqrt(b^2 -4ac)) / 2a
        float a = 1 + k*k;
        float b = 2*k*(l-y0) - 2*x0;
        float c = x0*x0 + y0*y0 + l*l - 2*y0*l - radius*radius;
        float x = (-b + FloatMath.sqrt(b*b - 4 * a*c)) / 2 / a;
        float y = k * x + l;
        intersection.set(x, y);
        //interception point is in quadrant 4, so the angle is negative
        arcStartAngle = -(float) (Math.asin((y0 - intersection.y) / radius) / Math.PI * 180);

        bcArc.reset();
        // sweep angle can not be 360, otherwise,
        // the point that is lined to (pb.x, pb.y) is not the point at start angle,
        // but the point at 0бу.
        bcArc.addArc(arc, arcStartAngle, I_AM_360);
        bcArc.lineTo(pb.x, pb.y);
        bcArcMeasure.setPath(bcArc, false);
        setPercentage(percent, false);
    }

    @Override
    protected int getStroke() {
        return STROKE;
    }

    @Override
    protected int getDuration() {
        return DURATION;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(lb, paint);
        canvas.drawPath(lc, paint);
        canvas.drawPath(la, paint);
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
}
