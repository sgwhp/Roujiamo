package cn.robust.roujiamo.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.robust.roujiamo.library.OnOpenListener;
import cn.robust.roujiamo.library.R;
import cn.robust.roujiamo.library.drawable.AbsRoujiamoDrawable;
import cn.robust.roujiamo.library.drawable.BurgerDrawable;
import cn.robust.roujiamo.library.drawable.DipperDrawable;
import cn.robust.roujiamo.library.drawable.MaterialBurgerDrawable;
import cn.robust.roujiamo.library.drawable.SandwichDrawable;

/**
 * Created by wuhongping on 15-5-5.
 */
public class Roujiamo extends ImageView implements View.OnClickListener {
    public static final int DRAWABLE_TYPE_BURGER = 0;
    public static final int DRAWABLE_TYPE_DIPPER = 1;
    public static final int DRAWABLE_TYPE_MATERIAL_BURGER = 2;
    public static final int DRAWABLE_TYPE_SANDWICH = 3;
    private OnOpenListener mListener;
    private AbsRoujiamoDrawable drawable;

    public Roujiamo(Context context) {
        super(context);
        init(context, null);
    }

    public Roujiamo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Roujiamo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if(attrs == null) {
            drawable = new BurgerDrawable(context);
        } else {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Roujiamo);
            final int type = typedArray.getInt(R.styleable.Roujiamo_drawable, 0);
            switch (type){
                case DRAWABLE_TYPE_DIPPER:
                    drawable = new DipperDrawable(context);
                    break;
                case DRAWABLE_TYPE_MATERIAL_BURGER:
                    drawable = new MaterialBurgerDrawable(context);
                    break;
                case DRAWABLE_TYPE_SANDWICH:
                    drawable = new SandwichDrawable(context);
                    break;
                default:
                    drawable = new BurgerDrawable(context);
            }
            typedArray.recycle();
        }
        super.setImageDrawable(drawable);
        setClickable(true);
        setOnClickListener(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.open = drawable.isOpen();

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

//        this.open = ss.open;
        post(new Runnable() {

            @Override
            public void run() {
                setOpen(ss.open, false, true);
            }
        });
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int measuredWidth;
//        int measuredHeight;
//        int wMode = MeasureSpec.getMode(widthMeasureSpec);
//        int hMode = MeasureSpec.getMode(heightMeasureSpec);
//        Drawable d = getDrawable();
//        if(wMode != MeasureSpec.EXACTLY){
//            measuredWidth = d.getIntrinsicWidth();
//        } else {
//            measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
//        }
//        if(hMode != MeasureSpec.EXACTLY){
//            measuredHeight = d.getIntrinsicHeight();
//        } else {
//            measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
//        }
//
//        setMeasuredDimension(measuredWidth, measuredHeight);
//    }

    /**
     * nothing will be done
     * @param drawable doesn't matter
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
    }

    @Override
    public void onClick(View v) {
        if(!drawable.isAnimating()){
            setOpen(!drawable.isOpen(), true);
        }
    }

    public void setDrawable(AbsRoujiamoDrawable drawable){
        this.drawable = drawable;
    }

    /**
     * to set the status. Must called after onLayout. You can use the post method
     * @see android.view.View#post(Runnable)
     * @param open status to be set
     * @param anim need animation or not
     * @param force force to update even the status is the same
     */
    public void setOpen(boolean open, boolean anim, boolean force){
        if(mListener != null && drawable.isOpen() != open){
            mListener.onOpen(this, open);
        }
        drawable.open(open, anim, force);
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

    public boolean isOpen(){
        return drawable.isOpen();
    }

    /**
     * to set the icon's color
     * @param color the color you want
     */
    public void setColor(int color){
        drawable.setColor(color);
    }

    public void setOnOpenListener(OnOpenListener listener){
        mListener = listener;
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
