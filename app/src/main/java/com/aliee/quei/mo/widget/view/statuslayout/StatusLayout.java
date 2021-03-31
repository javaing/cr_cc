package com.aliee.quei.mo.widget.view.statuslayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.aliee.quei.mo.R;
import com.aliee.quei.mo.ui.comic.activity.ComicDetailActivity;
import com.aliee.quei.mo.ui.comic.activity.ComicReadActivity;
import com.aliee.quei.mo.ui.main.activity.ContentActivity;
import com.umeng.analytics.MobclickAgent;
import io.reactivex.disposables.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: YangYang
 * @Date: 2018/1/15
 * @Version: 1.0.0
 * @Description:
 */
public class StatusLayout extends FrameLayout {
    private long showLoadingTime = System.currentTimeMillis();

    enum Status{
        LOADING,
        ERROR,
        CONTENT,
        NO_NETWORK,
        EMPTY
    }

    private Status curStatus = Status.LOADING;

    public static StatusLayout wrap(Activity activity) {
        return wrap(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
    }

    public static StatusLayout wrap(Fragment fragment) {
        return wrap(fragment.getView());
    }

    public static StatusLayout wrap(View view) {
        if (view == null) {
            throw new RuntimeException("content view can not be null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            throw new RuntimeException("parent view can not be null");
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int index = parent.indexOfChild(view);
        parent.removeView(view);

        StatusLayout layout = new StatusLayout(view.getContext());
        parent.addView(layout, index, lp);
        layout.addView(view);
        layout.setContentView(view);
        return layout;
    }

    public void setLoadingAnimation(AnimationDrawable animation){
        this.animationDrawable = animation;
    }

    private int mEmptyImage = NO_ID;
    private CharSequence mEmptyText = null;

    private int mErrorImage = NO_ID;
    private CharSequence mErrorText = null, mErrorRetryText = null;

    private int mNoNetworkImage = NO_ID;
    private CharSequence mNoNetworkText = null, mNoNetworkRetryText = null;

    private View.OnClickListener mErrorRetryButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mErrorRetryListener != null) {
                mErrorRetryListener.onClick(v);
            }
        }
    };

    private View.OnClickListener mNoNetworkRetryButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mNoNetworkRetryListener != null) {
                mNoNetworkRetryListener.onClick(v);
            }
        }
    };

    private View.OnClickListener mEmptyRetryButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mEmptyRetryListener != null)mEmptyRetryListener.onClick(v);
        }
    };

    private View.OnClickListener mRefreshButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mRefreshListener != null)mRefreshListener.onClick(v);
        }
    };
    private View.OnClickListener mErrorRetryListener;
    private View.OnClickListener mNoNetworkRetryListener;
    private View.OnClickListener mRefreshListener;
    private View.OnClickListener mEmptyRetryListener;

    private int mEmptyResId = NO_ID, mLoadingResId = NO_ID, mErrorResId = NO_ID, mNoNetworkResId = NO_ID;
    private int mContentId = NO_ID;

    private Map<Integer, View> mLayouts = new HashMap<>();

    private LayoutInflater mInflater;
    private AnimationDrawable animationDrawable;

    private int retryTime = 0;

    public StatusLayout(Context context) {
        this(context, null, 0);
    }

    public StatusLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);

        mLoadingResId = R.layout.statuslayout_loading_jump;
        mEmptyResId = R.layout.statuslayout_empty;
        mErrorResId = R.layout.statuslayout_error;
        mNoNetworkResId = R.layout.statuslayout_nonetwork;
        animationDrawable = (AnimationDrawable) context.getResources().getDrawable(R.drawable.loading_animation_jump);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            return;
        }
        if (getChildCount() > 1) {
            removeViews(1, getChildCount() - 1);
        }
        View view = getChildAt(0);
        setContentView(view);

        switch (curStatus) {
            case EMPTY:
                showEmpty();
                break;
            case ERROR:
                showError(mErrorRetryListener);
                break;
            case CONTENT:
                showContent();
                break;
            case LOADING:
                showLoading();
                break;
            case NO_NETWORK:
                showNoNetwork(mNoNetworkRetryListener);
                break;
        }
    }

    public void setContentView(View view) {
        mContentId = view.getId();
        mLayouts.put(mContentId, view);
    }

    public StatusLayout setLoading(@LayoutRes int id) {
        if (mLoadingResId != id) {
            remove(mLoadingResId);
            mLoadingResId = id;
        }
        return this;
    }

    public StatusLayout setEmpty(@LayoutRes int id) {
        if (mEmptyResId != id) {
            remove(mEmptyResId);
            mEmptyResId = id;
        }
        return this;
    }

    public StatusLayout setEmptyImage(@DrawableRes int resId) {
        mEmptyImage = resId;
        image(mEmptyResId, R.id.empty_img, mEmptyImage);
        return this;
    }

    public StatusLayout setEmptyText(String value) {
        mEmptyText = value;
        text(mEmptyResId, R.id.empty_text, mEmptyText);
        return this;
    }

    public StatusLayout setEmptyRetryListener(View.OnClickListener listener){
        this.mEmptyRetryListener = listener;
        return this;
    }

    public StatusLayout setError(@LayoutRes int id) {
        if (mErrorResId != id) {
            remove(mErrorResId);
            mErrorResId = id;
        }
        return this;
    }

    public StatusLayout setErrorImage(@DrawableRes int resId) {
        mErrorImage = resId;
        image(mErrorResId, R.id.error_img, mErrorImage);
        return this;
    }

    public StatusLayout setErrorText(String value) {
        mErrorText = value;
        text(mErrorResId, R.id.error_text, mErrorText);
        return this;
    }

    public StatusLayout setErrorRetryText(String value) {
        mErrorRetryText = value;
        text(mErrorResId, R.id.error_reload_btn, mErrorRetryText);
        return this;
    }

    public StatusLayout setErrorRetryListener(OnClickListener listener) {
        mErrorRetryListener = listener;
        return this;
    }

    public StatusLayout setRefreshListener(OnClickListener listener){
        mRefreshListener = listener;
        return this;
    }

    public StatusLayout setNoNetwork(@LayoutRes int id) {
        if (mNoNetworkResId != id) {
            remove(mNoNetworkResId);
            mNoNetworkResId = id;
        }
        return this;
    }

    public StatusLayout setNoNetworkImage(@DrawableRes int resId) {
        mNoNetworkImage = resId;
        image(mNoNetworkResId, R.id.no_network_img, mNoNetworkImage);
        return this;
    }

    public StatusLayout setNoNetworkText(String value) {
        mNoNetworkText = value;
        text(mNoNetworkResId, R.id.no_network_text, mNoNetworkText);
        return this;
    }

    public StatusLayout setNoNetworkRetryText(String value) {
        mNoNetworkRetryText = value;
        text(mNoNetworkResId, R.id.no_network_reload_btn, mNoNetworkRetryText);
        return this;
    }

    public StatusLayout setNoNetworkRetryListener(OnClickListener listener) {
        mNoNetworkRetryListener = listener;
        return this;
    }

    public void showLoading() {
        curStatus = Status.LOADING;
        show(mLoadingResId);
        animationDrawable.start();
        showLoadingTime = System.currentTimeMillis();
        if(showContentDisposable != null){
            showContentDisposable.dispose();
            showContentDisposable = null;
        }
    }

    public void showEmpty() {
        curStatus = Status.EMPTY;
        show(mEmptyResId);
    }

    public void showError(OnClickListener listener) {
        curStatus = Status.ERROR;
        setErrorRetryListener(listener);
        show(mErrorResId);
        if (retryTime < 1) {
            postDelayed(()-> {
                listener.onClick(this);
                retryTime ++;
            },1000);
        }
        Context context = getContext();
        if (context instanceof ContentActivity) {
            MobclickAgent.onEvent(getContext(),"首页报错");
        }
        if (context instanceof ComicDetailActivity) {
            MobclickAgent.onEvent(getContext(),"详情页报错");
        }
        if (context instanceof ComicReadActivity) {
            MobclickAgent.onEvent(getContext(),"阅读页报错");
        }
    }

    public void showNoNetwork(OnClickListener listener) {
        curStatus = Status.NO_NETWORK;
        setNoNetworkRetryListener(listener);
        show(mNoNetworkResId);
        if (retryTime < 1) {
            postDelayed(()-> {
                listener.onClick(this);
                retryTime ++;
            },1000);
        }
    }

    private Disposable showContentDisposable;
    public void showContent() {
        curStatus = Status.CONTENT;
        show(mContentId);
    }

    private void show(int layoutId) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(GONE);
        }
        layout(layoutId).setVisibility(VISIBLE);
    }

    private void remove(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            View vg = mLayouts.remove(layoutId);
            removeView(vg);
        }
    }

    private View layout(int layoutId) {
        if (mLayouts.containsKey(layoutId)) {
            return mLayouts.get(layoutId);
        }
        View layout = mInflater.inflate(layoutId, this, false);
        layout.setVisibility(GONE);
        addView(layout);
        mLayouts.put(layoutId, layout);

        if (layoutId == mEmptyResId) {
            ImageView img = layout.findViewById(R.id.empty_img);
            if (img != null && mEmptyImage != NO_ID) {
                img.setImageResource(mEmptyImage);
            }
            TextView view = layout.findViewById(R.id.empty_text);
            if (view != null && mEmptyText != null) {
                view.setText(mEmptyText);
            }
            TextView btn  = layout.findViewById(R.id.empty_btn);
            if (btn != null){
                if(mEmptyRetryListener != null){
                    btn.setOnClickListener(mEmptyRetryButtonClickListener);
                    layout.setOnClickListener(mEmptyRetryButtonClickListener);
                }
            }
        } else if (layoutId == mErrorResId) {
            ImageView img = layout.findViewById(R.id.error_img);
            if (img != null && mErrorImage != NO_ID) {
                img.setImageResource(mErrorImage);
            }
            TextView txt = layout.findViewById(R.id.error_text);
            if (txt != null && mErrorText != null) {
                txt.setText(mErrorText);
            }
            TextView btn = layout.findViewById(R.id.error_reload_btn);
            if (btn != null) {
                if (mErrorRetryText != null)
                    btn.setText(mErrorRetryText);
                btn.setOnClickListener(mErrorRetryButtonClickListener);
                layout.setOnClickListener(mErrorRetryButtonClickListener);
            }
        } else if (layoutId == mNoNetworkResId) {
            ImageView img = layout.findViewById(R.id.no_network_img);
            if (img != null && mNoNetworkImage != NO_ID) {
                img.setImageResource(mErrorImage);
            }
            TextView txt = layout.findViewById(R.id.no_network_text);
            if (txt != null && mNoNetworkText != null) {
                txt.setText(mErrorText);
            }
            TextView btn = layout.findViewById(R.id.no_network_reload_btn);
            if (btn != null) {
                if (mNoNetworkRetryText != null)
                    btn.setText(mNoNetworkRetryText);
                btn.setOnClickListener(mNoNetworkRetryButtonClickListener);
                layout.setOnClickListener(mNoNetworkRetryButtonClickListener);
            }
        } else if(layoutId == mLoadingResId){
            ImageView img = layout.findViewById(R.id.progressBar);
            img.setImageDrawable(animationDrawable);
        }

        if(layoutId != mLoadingResId){
            animationDrawable.stop();
        }
        return layout;
    }

    private void text(int layoutId, int ctrlId, CharSequence value) {
        if (mLayouts.containsKey(layoutId)) {
            TextView view = mLayouts.get(layoutId).findViewById(ctrlId);
            if (view != null) {
                view.setText(value);
            }
        }
    }

    private void image(int layoutId, int ctrlId, int resId) {
        if (mLayouts.containsKey(layoutId)) {
            ImageView view = mLayouts.get(layoutId).findViewById(ctrlId);
            if (view != null) {
                view.setImageResource(resId);
            }
        }
    }
}