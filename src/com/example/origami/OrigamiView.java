package com.example.origami;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-3-8
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
public class OrigamiView extends FrameLayout {

    private List<OrigamiItem> origamiItems;

    private int headHeight, contentHeight;

    private View blankView;

    private LinearLayout targetLayout;

    private GLSurfaceView origamiAnimationView;

    private OrigamiRenderer renderer;

    private int chooseIndex = -1;

    public OrigamiView(Context context, int headHeight, int contentHeight) {
        super(context);
        origamiItems = new ArrayList<OrigamiItem>();
        this.headHeight = headHeight;
        this.contentHeight = contentHeight;

        this.targetLayout = new LinearLayout(context);
        targetLayout.setOrientation(LinearLayout.VERTICAL);

        this.blankView = new View(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentHeight);
        this.targetLayout.addView(blankView, layoutParams);

        //在第一次布局后执行，获取当时布局的宽度，并截取视图的bitmap
        this.targetLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //TODO 如果没能执行这步怎么办，比如竖屏后马上横屏，是否会有问题?
                OrigamiView.this.targetLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = OrigamiView.this.targetLayout.getWidth();

                for (OrigamiItem item : origamiItems) {
                    item.drawBitmaps(width);
                }
            }
        });

        this.addView(this.targetLayout);

        origamiAnimationView = new GLSurfaceView(context);

        //使用OpenGL ES 2.0
        origamiAnimationView.setEGLContextClientVersion(2);

        origamiAnimationView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        origamiAnimationView.setZOrderOnTop(true);
        origamiAnimationView.getHolder().setFormat(PixelFormat.TRANSPARENT);

//        origamiAnimationView.setVisibility(View.GONE);
        this.renderer = new OrigamiRenderer();
        origamiAnimationView.setRenderer(renderer);
        origamiAnimationView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        this.addView(origamiAnimationView);

//        this.targetLayout.setVisibility(View.INVISIBLE);
    }

    public int getChooseIndex() {
        return chooseIndex;
    }

    public GLSurfaceView getOrigamiAnimationView() {
        return origamiAnimationView;
    }

    public LinearLayout getTargetLayout() {
        return targetLayout;
    }

    public List<OrigamiItem> getOrigamiItems() {
        return origamiItems;
    }

    public void addOrigamiItem(final OrigamiItem item) {
        this.origamiItems.add(item);

        //设置头部视图
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headHeight);
        this.targetLayout.addView(item.getHead(), layoutParams);

        item.getHead().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                blankView.setVisibility(View.GONE);
                OrigamiItem chooseItem = null;

                for (OrigamiItem i : origamiItems) {
                    if (i != item) {
                        i.getContent().setVisibility(View.GONE);
                    } else {
                        chooseItem = i;
                        if (i.getContent().getVisibility() == View.VISIBLE) {
                            i.getContent().setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                        } else {
                            i.getContent().setVisibility(View.VISIBLE);
                        }
                    }
                }

                int _chooseIndex = origamiItems.indexOf(chooseItem);
                if (_chooseIndex != chooseIndex) {
                    chooseIndex = _chooseIndex;
                } else {
                    chooseIndex = -1;
                }
                renderer.chooseItem(OrigamiView.this);
            }
        });

        //设置内容视图
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentHeight);
        item.getContent().setVisibility(LinearLayout.GONE);
        this.targetLayout.addView(item.getContent(), layoutParams);
    }

    public int getAllHeight() {
        int height = 0;
        for (int i = 0; i < origamiItems.size(); i++) {
            height += (headHeight + contentHeight);
        }
        return height;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public int getHeadHeight() {
        return headHeight;
    }
}
