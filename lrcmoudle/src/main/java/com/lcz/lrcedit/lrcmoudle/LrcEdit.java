package com.lcz.lrcedit.lrcmoudle;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LrcEdit extends View {


    private boolean isNewAdd = false;
    private List<LrcString> lrcStrings;
    private TextPaint textPaint;
    private String TAG = "view";
    private int lastY;
    private int viewHeight;
    private float lrcOffset;
    private int endLine;
    private float offsetE;
    private GestureDetector gestureDetector;

    public LrcEdit(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }
    public LrcEdit(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }
    public LrcEdit(Context context) {
        super(context);
    }
    public List<LrcString> getLrcStrings() {
        return lrcStrings;
    }
    private void init(){
        lrcStrings = new ArrayList<>();
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.LEFT);
        gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(endLine>0){
            textDraw(lrcStrings,canvas,textPaint);
        }

    }

    private void textDraw(List<LrcString> strings, Canvas canvas, Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float top=fontMetrics.top;
        float bottom=fontMetrics.bottom;
        float offset=bottom - top;
        offsetE = offset;
        float totleOffset = 0;
        int stringLen = strings.size();
        for(int i=0;i<stringLen;i++){
            if (i > 0) {
                totleOffset += strings.get(i - 1).getHeight() + offset;
            }
            if(i==stringLen-1) {
                lrcOffset = canvas.getHeight() - totleOffset - 2*offset -strings.get(stringLen-1).getHeight();
                strings.get(i).setOffset(lrcOffset);
            }
            drawText(canvas, strings.get(i).getStaticLayout(), totleOffset);
        }
        if(isNewAdd){
            scrollTo(0, -(int)(strings.get(stringLen-1).getOffset()));
            isNewAdd = false;
        }



    }

    private void drawText(Canvas canvas, StaticLayout staticLayout, float y) {
        canvas.save();
        canvas.translate(0, y);
        staticLayout.draw(canvas);
        canvas.restore();
    }
    //添加歌词函数
    public void addLrcString(String string){
        LrcString lrcString= new LrcString(string);
        lrcStrings.add(lrcString);
        endLine = lrcStrings.size();
        lrcStrings.get(endLine-1).init(textPaint, getWidth());
        isNewAdd = true;
        invalidate();
    }
    //删除最后一句歌词
    public void removeLrcString(){
        lrcStrings.remove(endLine-1);
        isNewAdd = true;
        endLine = lrcStrings.size();
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        viewHeight = heightSize;
        Log.e(TAG, "onMeasure--widthMode-->" + widthMode);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        Log.e(TAG, "onMeasure--widthSize-->" + widthSize);
        Log.e(TAG, "onMeasure--heightMode-->" + heightMode);
        Log.e(TAG, "onMeasure--heightSize-->" + heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    public boolean onTouchEvent(MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(!lrcStrings.isEmpty()){
                removeLrcString();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int y = (int)distanceY;
            if(!lrcStrings.isEmpty()){
                scrollBy(0,y);
                //这条式子真是反人类，重构的时候优先改善
                float a = -lrcStrings.get(lrcStrings.size()-1).getOffset()+getHeight()-lrcStrings.get(lrcStrings.size()-1).getHeight()-3*offsetE;
                if(getScrollY()<-lrcStrings.get(0).getOffset()-offsetE){
                    scrollTo(0, -(int)(lrcStrings.get(0).getOffset()+offsetE));
                }
                if(getScrollY()>a){
                    scrollTo(0, (int)(a));
                }
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    };
}
