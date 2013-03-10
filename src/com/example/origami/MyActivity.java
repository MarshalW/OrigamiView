package com.example.origami;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MyActivity extends Activity {

    private RelativeLayout targetLayout;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.targetLayout = (RelativeLayout) this.findViewById(R.id.targetLayout);

        /**
         * 创建折纸视图
         */
        OrigamiView origamiView = new OrigamiView(this, 150, 300);
        origamiView.setBackgroundColor(Color.GRAY);

        OrigamiItem item=new OrigamiItem();

        //第1个item
        View head=new View(this);
        head.setBackgroundColor(Color.GREEN);
        item.setHead(head);

        View content=new View(this);
        content.setBackgroundColor(Color.YELLOW);
        item.setContent(content);

        origamiView.addOrigamiItem(item);

        //第2个item
        item=new OrigamiItem();
        head=new View(this);
        head.setBackgroundColor(Color.GREEN);
        item.setHead(head);

        content=new View(this);
        content.setBackgroundColor(Color.MAGENTA);
        item.setContent(content);

        origamiView.addOrigamiItem(item);

        //将origami view加入到根布局中
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 600);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        this.targetLayout.addView(origamiView, layoutParams);
    }
}