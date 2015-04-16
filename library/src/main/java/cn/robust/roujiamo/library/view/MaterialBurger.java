package cn.robust.roujiamo.library.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.robust.roujiamo.library.OnOpenListener;
import cn.robust.roujiamo.library.drawable.MaterialBurgerDrawable;

/**
 * @see cn.robust.roujiamo.library.drawable.MaterialBurgerDrawable
 * Created by wuhongping on 15-4-15.
 */
public class MaterialBurger extends ImageView implements View.OnClickListener {
    private MaterialBurgerDrawable drawable;
    private OnOpenListener mListener;

    public MaterialBurger(Context context) {
        super(context);
        init(context);
    }

    public MaterialBurger(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialBurger(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        drawable = new MaterialBurgerDrawable(context);
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
