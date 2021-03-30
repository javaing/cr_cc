package com.aliee.quei.mo.widget.view.page;

/**
 * Created by liyang on 2018/5/11 0011.
 */

public enum LineSpace {
    LINE_SPACE_SMALL(0.8f),
    LINE_SPACE_NORMAL(1f),
    LINE_SPACE_BIG(1.5f);


    private float factor;
    LineSpace(float factor){
        this.factor = factor;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
}
