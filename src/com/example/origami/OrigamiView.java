package com.example.origami;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-3-8
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
public class OrigamiView extends LinearLayout {

    private List<OrigamiItem> origamiItems;

    private int headHeight, contentHeight;

    private View blankView;

    public OrigamiView(Context context, int headHeight, int contentHeight) {
        super(context);
        origamiItems = new ArrayList<OrigamiItem>();
        this.headHeight = headHeight;
        this.contentHeight = contentHeight;

        this.setOrientation(LinearLayout.VERTICAL);

        this.blankView = new View(getContext());
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentHeight);
        this.addView(blankView, layoutParams);
    }

    public List<OrigamiItem> getOrigamiItems() {
        return origamiItems;
    }

    public void addOrigamiItem(final OrigamiItem item) {
        this.origamiItems.add(item);

        //设置头部视图
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headHeight);
        this.addView(item.getHead(), layoutParams);

        item.getHead().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                blankView.setVisibility(View.GONE);

                for (OrigamiItem i : origamiItems) {
                    if (i != item) {
                        i.getContent().setVisibility(View.GONE);
                    } else {
                        if (i.getContent().getVisibility() == View.VISIBLE) {
                            i.getContent().setVisibility(View.GONE);
                            blankView.setVisibility(View.VISIBLE);
                        } else {
                            i.getContent().setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        //设置内容视图
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, contentHeight);
        item.getContent().setVisibility(LinearLayout.GONE);
        this.addView(item.getContent(), layoutParams);
    }

    public int getAllHeight() {
        int height = 0;
        for (int i = 0; i < origamiItems.size(); i++) {
            height += (headHeight + contentHeight);
        }
        return height;
    }
}
