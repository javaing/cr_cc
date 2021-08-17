package com.aliee.quei.mo.widget.view.bottombar;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliee.quei.mo.utils.SharedPreUtils;
import com.bumptech.glide.Glide;
import com.aliee.quei.mo.R;
import com.aliee.quei.mo.utils.extention.ImageViewEXKt;


/**
 * @Author: YangYang
 * @Date: 2017/6/9
 * @Version: 1.0.0
 * @Description:
 */
public class BottomBarTab extends RelativeLayout {
    private ImageView mIcon;
    private TextView mTvTitle;
    private View viewMessageNew;
    private Context mContext;
    private int mTabPosition = -1;

    private int iconNormal = -1;
    private int iconSelected = -1;

    private String iconNormalUrl = null;
    private String iconSelectedUrl = null;

    public BottomBarTab(Context context, String iconNormal, String iconSelected, CharSequence title) {
        this(context, null, -1, -1, title);
        this.iconNormalUrl = iconNormal;
        this.iconSelectedUrl = iconSelected;
        if (this.iconNormal == -1) {
            ImageViewEXKt.load(mIcon, iconNormalUrl);
        } else {
            mIcon.setImageResource(this.iconNormal);
        }
    }

    public BottomBarTab(Context context, @DrawableRes int iconNormal, @DrawableRes int iconSelected, CharSequence title) {
        this(context, null, iconNormal, iconSelected, title);
    }

    public BottomBarTab(Context context, AttributeSet attrs, @DrawableRes int iconNormal, @DrawableRes int iconSelected, CharSequence title) {
        this(context, attrs, 0, iconNormal, iconSelected, title);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, @DrawableRes int iconNormal, @DrawableRes int iconSelected, CharSequence title) {
        super(context, attrs, defStyleAttr);
        init(context, iconNormal, iconSelected, title);
    }

    private void init(Context context, int iconNormal, int iconSelected, CharSequence title) {
        inflate(getContext(), R.layout.view_bottom_tap, this);
        mContext = context;
        this.iconNormal = iconNormal;
        this.iconSelected = iconSelected;

        mIcon = findViewById(R.id.imageView);
        mTvTitle = findViewById(R.id.textView);
        viewMessageNew = findViewById(R.id.view_message_new);

        if (iconNormal == -1) {
            ImageViewEXKt.load(mIcon, iconNormalUrl);
        } else {
            mIcon.setImageResource(iconNormal);
        }
        mTvTitle.setText(title);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (mTabPosition == 2){
            return;
        }
        if (selected) {
            if (iconNormal == -1) {
                ImageViewEXKt.load(mIcon, iconSelectedUrl);
            } else {
                mIcon.setImageResource(iconSelected);
            }
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_tab_selected));
        } else {
            if (iconNormal == -1) {
                ImageViewEXKt.load(mIcon, iconNormalUrl);
            } else {
                mIcon.setImageResource(iconNormal);
            }
            mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_tab_normal));
        }
    }

    public void setTabPosition(int position) {
        mTabPosition = position;
        if (position == 0) {
            setSelected(true);
        }
        if (position == 2) {
            ViewGroup.LayoutParams params = mIcon.getLayoutParams();
            params.width = 100;
            params.height = 100;
            mIcon.setLayoutParams(params);
            mTvTitle.setVisibility(View.GONE);
            Glide.with(this).asGif().load(R.mipmap.video_icon).into(mIcon);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    /**
     * 显示小红点
     */
    public void showMessageNew() {
        viewMessageNew.setVisibility(VISIBLE);
    }

    /**
     * 隐藏小红点
     */
    public void hideMessageNew() {
        viewMessageNew.setVisibility(GONE);
    }
}
