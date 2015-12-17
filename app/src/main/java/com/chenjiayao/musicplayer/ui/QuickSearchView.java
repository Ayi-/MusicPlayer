package com.chenjiayao.musicplayer.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.chenjiayao.musicplayer.utils.ToastUtils;


/**
 * Created by chen on 2015/12/17.
 */
public class QuickSearchView extends View {

    String[] letter = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    Context context;
    private Paint paint;

    /**
     * 每个单元格的宽度
     */
    private int cellWidth;
    /**
     * 每个单元格的高度
     */
    private int cellHeight;

    public QuickSearchView(Context context) {
        this(context, null);
    }

    public QuickSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect();
        for (int i = 0; i < letter.length; i++) {

            String text = letter[i];
            int x = (int) (cellWidth / 2 - paint.measureText(text) / 2);
            paint.getTextBounds(text, 0, text.length(), bounds);
            int y = cellHeight / 2 + bounds.height() / 2 + i * cellHeight;
            //画字的坐标系和我们数学中的一样
            canvas.drawText(text, x, y, paint);
//            canvas.drawLine(0, cellHeight * (i + 1), cellWidth, cellHeight * (i + 1), paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        cellWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        cellHeight = mHeight / letter.length;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int index = -1;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / cellHeight);
                if (index >= 0 && index < letter.length) {
                    ToastUtils.showToast(context, letter[index]);
                }
                break;
            case MotionEvent.ACTION_UP:
                index = -1;
                break;
        }
        return true;
    }
}
