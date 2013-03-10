package com.example.origami;

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

    public View getContent() {
        return content;
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
}
