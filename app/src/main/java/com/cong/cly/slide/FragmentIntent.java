package com.cong.cly.slide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.cong.cly.MainActivity;
import com.cong.cly.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cong on 15/12/22.
 */
public class FragmentIntent {
    private static LinkedHashMap<String, BitmapItem> mCachedBitmaps;
    private static FragmentIntent instance;

    public static FragmentIntent getInstance() {
        if (instance == null) {
            instance = new FragmentIntent();
        }
        return instance;
    }

    public FragmentIntent() {
        if (mCachedBitmaps == null)
            mCachedBitmaps = new LinkedHashMap<String, BitmapItem>(0, 0.75f, true);
    }

    public void clear() {
        for (Map.Entry<String, BitmapItem> entry : mCachedBitmaps.entrySet()) {
            entry.getValue().clear();
        }
        mCachedBitmaps.clear();
    }

    public void setIsDisplayed(String id, boolean isDisplayed) {
        BitmapItem item = mCachedBitmaps.get(id);
        if (null != item) {
            item.setIsDisplayed(isDisplayed);
        }
    }

    public BitmapItem getBitmapItem(int width, int height) {
        int size = mCachedBitmaps.size();

        if (size > 0) {
            BitmapItem reuseItem = null;
            for (Map.Entry<String, BitmapItem> entry : mCachedBitmaps.entrySet()) {
                BitmapItem item = entry.getValue();
                if (item.getReferenceCount() <= 0) {
                    reuseItem = item;
                }
            }

            if (null != reuseItem) {
                return reuseItem;
            } else {
                return crateItem(width, height);
            }
        } else {
            return crateItem(width, height);
        }
    }

    private BitmapItem crateItem(int width, int height) {
        BitmapItem item = BitmapItem.create(width, height);
        String id = "id_" + System.currentTimeMillis();
        item.setId(id);
        mCachedBitmaps.put(id, item);
        return item;
    }

    public Bitmap getBitmap(String id) {
        return mCachedBitmaps.get(id).getBitmap();
    }

    public void startActivity(final Context context, final Intent intent) {
        final View v = ((Activity) context).findViewById(android.R.id.content);
        BitmapItem item = getBitmapItem(v.getWidth(), v.getHeight());
        final Bitmap bitmap = item.getBitmap();
        intent.putExtra("bitmap_id", item.getId());

        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.draw(new Canvas(bitmap));
                context.startActivity(intent);
            }
        }, 100);
    }

    public static void addFragment(final Activity activity, final Fragment fragment) {
        final View v = activity.findViewById(R.id.container_layout);
        BitmapItem item = FragmentIntent.getInstance().getBitmapItem(v.getWidth(), v.getHeight());
        final Bitmap bitmap = item.getBitmap();
        final Bundle bundle = new Bundle();
        bundle.putString("bitmap_id", item.getId());
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.draw(new Canvas(bitmap));
                fragment.setArguments(bundle);
                ((MainActivity) activity).replace(fragment);
            }
        }, 100);
    }
}
