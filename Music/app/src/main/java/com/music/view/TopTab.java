package com.music.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.music.R;

/**
 * Created by dingfeng on 2016/4/15.
 */
public class TopTab extends RelativeLayout {

    private TextView tv;
    private View line;
    private int tv_color_unselect;
    private int tv_color_selected;
    private ImageView dot;
    private int line_color_unselect;
    private int line_color_selected;
    private boolean isSelected;

    public TopTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public TopTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public TopTab(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(Context context) {
        tv_color_selected = context.getResources().getColor(R.color.blue);
        tv_color_unselect = context.getResources().getColor(R.color.text_grey);

        line_color_unselect = Color.TRANSPARENT;
        line_color_selected = context.getResources().getColor(R.color.blue);

        tv = new TextView(context);
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.big_text_size));
        tv.setTextColor(tv_color_unselect);
        LayoutParams p_tv = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        p_tv.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(tv, p_tv);
        tv.setId(2);

        line = new View(context);
        line.setBackgroundColor(line_color_unselect);
        LayoutParams p_line = new LayoutParams(LayoutParams.MATCH_PARENT, 4);
        p_line.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        p_line.bottomMargin = 2;
        addView(line, p_line);

        dot = new ImageView(context);
        dot.setImageResource(R.drawable.icon_reddot);
        LayoutParams p_dot = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        p_dot.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        p_dot.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        p_dot.topMargin = 20;
        addView(dot, p_dot);
        dot.setVisibility(View.GONE);

        isSelected = false;
    }

    public void setText(int resId) {
        tv.setText(resId);
    }

    public void showDot() {
        dot.setVisibility(View.VISIBLE);
    }

    public void hideDot() {
        dot.setVisibility(View.GONE);
    }

    public boolean isDotShow() {
        return dot.getVisibility() == View.VISIBLE;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        // TODO Auto-generated method stub
        super.setSelected(selected);
        if (selected) {
            isSelected = true;
            tv.setTextColor(tv_color_selected);
            line.setBackgroundColor(line_color_selected);
        } else {
            isSelected = false;
            tv.setTextColor(tv_color_unselect);
            line.setBackgroundColor(line_color_unselect);
        }
    }

}
