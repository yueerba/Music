package com.music.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.music.R;

/**
 * Created by dingfeng on 2016/4/16.
 */
public class ScanAnimation extends RelativeLayout {

    public static final String TAG = "SearchDevicesView";

    public Context context;

    private float offsetArgs = 0;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    Rect rMoon;

    private boolean isSearching = false;


    public ScanAnimation(Context context) {
        super(context);
        this.context = context;
        initBitmap();
    }

    public ScanAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initBitmap();
    }

    public ScanAnimation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initBitmap();
    }

    private void initBitmap() {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gplus_search_bg));
        }
        if (bitmap1 == null) {
            bitmap1 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.locus_round_click));
        }
        if (bitmap2 == null) {
            bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gplus_search_args));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, null);
        rMoon = new Rect(getWidth() / 2 - bitmap2.getWidth(), getHeight() / 2, getWidth() / 2, getHeight() / 2 + bitmap2.getHeight());
        if (isSearching) {
            canvas.rotate(offsetArgs, getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(bitmap2, null, rMoon, null);
            offsetArgs = offsetArgs + 3;
        } else {
            canvas.rotate(offsetArgs, getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(bitmap2, null, rMoon, null);
        }
        canvas.drawBitmap(bitmap1, getWidth() / 2 - bitmap1.getWidth() / 2, getHeight() / 2 - bitmap1.getHeight() / 2, null);

        if (isSearching) {
            invalidate();
        }
    }

    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(boolean isSearching) {
        this.isSearching = isSearching;
        invalidate();
    }
}
