package com.example.origami;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MyActivity extends Activity {

    private RelativeLayout targetLayout;

    private OrigamiView origamiView;

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
        origamiView = new OrigamiView(this, 150, 300);
        origamiView.setBackgroundColor(Color.TRANSPARENT);

        OrigamiItem item = new OrigamiItem();

        //第1个item
//        View head = new View(this);

        View head = this.getLayoutInflater().inflate(R.layout.head0, null);
        head.setBackgroundColor(Color.DKGRAY);
        item.setHead(head);

        View content = new View(this);
        content.setBackgroundColor(Color.TRANSPARENT);
        item.setContent(content);

        origamiView.addOrigamiItem(item);

        //第2个item
        item = new OrigamiItem();

        head = this.getLayoutInflater().inflate(R.layout.head, null);

        head.setBackgroundColor(Color.DKGRAY);
        item.setHead(head);

        //模拟复杂的自定义界面
        FrameLayout contentLayout = new FrameLayout(this);
        this.getLayoutInflater().inflate(R.layout.content, contentLayout);

        content = contentLayout;
        content.setBackgroundColor(Color.TRANSPARENT);
        item.setContent(content);


        origamiView.addOrigamiItem(item);

        //将origami view加入到根布局中
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 600);
        //TODO 600是手工计算的，要改为自动得到的
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        this.targetLayout.addView(origamiView, layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
