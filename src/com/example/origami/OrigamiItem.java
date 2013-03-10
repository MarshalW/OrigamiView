package com.example.origami;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-3-8
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
public class OrigamiItem {

    private View head;

    private View content;

    private Bitmap headBitmap, contentBitmap;

    public View getContent() {
        return content;
    }

    public Bitmap getContentBitmap() {
        return contentBitmap;
    }

    public Bitmap getHeadBitmap() {
        return headBitmap;
    }

    public void setContent(View content) {
        this.content = content;
    }

    public View getHead() {
        return head;
    }

    public void setHead(View head) {
        this.head = head;
    }

    public void drawBitmaps(final int width) {
        //这里不能创建新线程来做
        //因为：Only the original thread that created a view hierarchy can touch its views.
        headBitmap = loadBitmapFromView(head, width);
        contentBitmap = loadBitmapFromView(content, width);
    }

    private static Bitmap loadBitmapFromView(View v, int width) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Bitmap b = Bitmap.createBitmap(width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        v.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(v.getLayoutParams().height, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }
}
