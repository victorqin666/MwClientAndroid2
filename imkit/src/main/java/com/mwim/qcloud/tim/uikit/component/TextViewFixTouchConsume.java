package com.mwim.qcloud.tim.uikit.component;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.work.util.SLog;
import com.work.util.StringUtils;

/**
 * Created by tangyx
 * Date 2020/10/22
 * email tangyx@live.com
 */

public class TextViewFixTouchConsume extends AppCompatTextView {

    boolean dontConsumeNonUrlClicks = true;
    boolean linkHit;

    public TextViewFixTouchConsume(Context context) {
        super(context);
    }

    public TextViewFixTouchConsume(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewFixTouchConsume(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        linkHit = false;
        boolean res = super.onTouchEvent(event);
        if (dontConsumeNonUrlClicks)
            return linkHit;
        return res;

    }
    public static class LocalLinkMovementMethod extends LinkMovementMethod {
        static LocalLinkMovementMethod sInstance;
        private long lastClickTime;
        private static final long CLICK_DELAY = 500L;
        private boolean isClickLink = true;

        public static LocalLinkMovementMethod getInstance() {
            if (sInstance == null)
                sInstance = new LocalLinkMovementMethod();

            return sInstance;
        }

        @Override
        public boolean onTouchEvent(final TextView widget,
                                    Spannable buffer, MotionEvent event) {
            int action = event.getAction();
            SLog.w("action:"+action);
            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                final StringUtils.Clickable[] link = buffer.getSpans(
                        off, off, StringUtils.Clickable.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        SLog.i("onTouchEvent 离开:"+widget.getTag());
                        isClickLink = false;
                        if (System.currentTimeMillis() - lastClickTime < CLICK_DELAY) {
                            isClickLink = true;
                            link[0].onClick(widget);
                        }
                    } else {
                        SLog.i("onTouchEvent 按下");
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                        lastClickTime = System.currentTimeMillis();
                        isClickLink = false;
                        widget.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!isClickLink){
                                    link[0].onLongClick();
                                }
                            }
                        },CLICK_DELAY);
                    }
                    if (widget instanceof TextViewFixTouchConsume){
                        ((TextViewFixTouchConsume) widget).linkHit = true;
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }else if(action == MotionEvent.ACTION_CANCEL){
                isClickLink = true;
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }
    }
}
