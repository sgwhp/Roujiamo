package cn.robust.roujiamo.library;

import android.view.View;

/**
 * Created by wuhongping on 15-4-8.
 */
public interface OnOpenListener {
    /**
     * being called when view's status changed
     * @param open
     */
    void onOpen(View v, boolean open);
}
