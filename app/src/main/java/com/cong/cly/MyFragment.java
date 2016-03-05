package com.cong.cly;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cong.cly.slide.FragmentIntent;
import com.cong.cly.slide.SlidingLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cong on 16/3/5.
 */
public class MyFragment extends Fragment implements SlidingLayout.SlidingListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_preview)
    View ivPreview;
    private View parentView;
    private final Object empty = new Object();
    private float mInitOffset;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("MyFragment", "onCreateView");
        if (parentView == null) {
            parentView = inflater.inflate(R.layout.fragment_my, container, false);
            Log.e("MyFragment", "onCreateView1");
        }
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle("MyFragment");
        initSlide(view);
    }

    private void initSlide(View view) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mInitOffset = -(1.f / 3) * metrics.widthPixels;
        SlidingLayout slideLayout = (SlidingLayout) view.findViewById(R.id.slide_layout);
//        slideLayout.setShadowResource(R.drawable.sliding_back_shadow);
        slideLayout.setSlidingListener(this);
        slideLayout.setEdgeSize((int) (metrics.density * 20));
        String mBitmapId = getArguments().getString("bitmap_id");
        Bitmap bitmap = FragmentIntent.getInstance().getBitmap(mBitmapId);
        if (null != bitmap) {
            if (Build.VERSION.SDK_INT >= 16) {
                ivPreview.setBackground(new BitmapDrawable(bitmap));
            } else {
                ivPreview.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            FragmentIntent.getInstance().setIsDisplayed(mBitmapId, true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Object obj = parentView.getTag();
        if (empty != obj) {
            FrameLayout container = (FrameLayout) parentView.findViewById(R.id.container_layout);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_my, null);
            View view1 = view.findViewById(R.id.text);
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                   FragmentIntent.getInstance().addFragment(getActivity(),new HomeFragment());
                }
            });
            container.addView(view);
            parentView.setTag(empty);
            Log.e("MyFragment", "onStart");
        } else {
            Log.e("MyFragment", "onStart1");
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (slideOffset <= 0) {
        } else if (slideOffset < 1) {
            ivPreview.setTranslationX(mInitOffset * (1 - slideOffset));
        } else {
            ivPreview.setTranslationX(0);
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
