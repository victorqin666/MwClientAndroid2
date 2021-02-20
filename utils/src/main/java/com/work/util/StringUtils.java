package com.work.util;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by tangyx on 16/8/10.
 */
public class StringUtils {

    /**
     * 复制文本内容
     *
     * @param context
     * @param textView
     */
    public static void CopyString(Context context, String textView) {
        if (Build.VERSION.SDK_INT < 11) {
            // 从API11开始android推荐使用android.content.ClipboardManager
            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(textView);
        } else {
            // 从API11开始android推荐使用android.content.ClipboardManager
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", textView);//‘Label’这是任意文字标签
            // ClipData.newRawUri("Label",Uri.parse("http://www.baidu.com"));//创建URL型ClipData：
            // ClipData.newIntent("Label", intent);//创建Intent型ClipData：
            // 将ClipData内容放到系统剪贴板里。
            clipboardManager.setPrimaryClip(mClipData);
        }
    }


    /**
     * @param str     文字
     * @param size    要改变的大小
     * @param s_start 开始位置
     * @param s_end   结束位置
     */
    public static SpannableStringBuilder changText(String str, int size, int s_start, int s_end) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(size), s_start, s_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }
    public static SpannableStringBuilder changText(String str, int size, int s_start, int s_end,int start,int end) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(size), s_start, s_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(size), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * @param str     文字
     * @param color   要改变的颜色
     * @param s_start 开始位置
     * @param s_end   结束位置
     */
    public static SpannableStringBuilder changTextColor(String str, int color, int s_start, int s_end) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(color), s_start, s_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder textChange(@NonNull String startStr, @NonNull String centerStr, @NonNull String endStr, int S_C, String type) {
        int start;
        int end;
        StringBuffer buffer = new StringBuffer();
        buffer.append(startStr);
        start = buffer.length();
        buffer.append(centerStr);
        end = buffer.length();
        buffer.append(endStr);
        SpannableStringBuilder spannableStringBuilder;
        if ("size".equals(type)) {
            spannableStringBuilder = changText(buffer.toString(), S_C, start, end);
        } else {
            spannableStringBuilder = changTextColor(buffer.toString(), S_C, start, end);
        }
        return spannableStringBuilder;
    }

    /**
     * @param str     文字
     * @param size    要改变的大小
     * @param s_start 开始位置
     * @param s_end   结束位置
     * @return 改变部分文字颜色及大小
     */
    public static SpannableStringBuilder changText(String str, int size, int color, int s_start, int s_end, int c_start, int c_end) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan(size), s_start, s_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(color), c_start, c_end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /***
     * 只有价格样式
     *
     * @param price
     * @param textView
     * @return
     */
    public static SpannableStringBuilder PriceTextChange(double price, TextView textView) {
        int sizeStart;
        int sizeEnd;
        StringBuffer buffer = new StringBuffer();
        buffer.append("￥");
        sizeStart = buffer.length();
        String strings[] = new DecimalFormat("##0.00").format(price).split("\\.");
        buffer.append(strings[0]);
        sizeEnd = buffer.length();
        buffer.append(".");
        buffer.append(strings[1]);
        SpannableStringBuilder stringBuilder = changText(buffer.toString(), (int) (textView.getTextSize() * 1.2), sizeStart, sizeEnd);
        return stringBuilder;
    }

    /**
     * 价格范围的的价格样式
     * @param priceMin
     * @param priceMax
     * @param textView
     * @return
     */
    public static SpannableStringBuilder PriceTextChange(double priceMin,double priceMax, TextView textView) {
        int sizeStart;
        int sizeEnd;
        int start;
        int end;
        StringBuffer buffer = new StringBuffer();
        buffer.append("￥");
        sizeStart = buffer.length();
        String stringMin[] = new DecimalFormat("##0.00").format(priceMin).split("\\.");
        String stringMax[] = new DecimalFormat("##0.00").format(priceMax).split("\\.");
        buffer.append(stringMin[0]);
        sizeEnd = buffer.length();
        buffer.append(".");
        buffer.append(stringMin[1]);
        buffer.append("~");
        start = buffer.length();
        buffer.append(stringMax[0]);
        end = buffer.length();
        buffer.append(".");
        buffer.append(stringMax[1]);
        SpannableStringBuilder stringBuilder = changText(buffer.toString(), (int) (textView.getTextSize() * 1.2), sizeStart, sizeEnd,start,end);
        return stringBuilder;
    }



    /***
     * 只有价格样式
     *
     * @param textView
     * @return
     */
    public static void PriceTextChange(TextView textView) {
        int sizeStart;
        int sizeEnd;
        StringBuffer buffer = new StringBuffer();
        sizeStart = buffer.length();
        double d = Double.valueOf(textView.getText().toString());
        String strings[] = new DecimalFormat("##0.00").format(d).split("\\.");
        buffer.append(strings[0]);
        sizeEnd = buffer.length();
        buffer.append(".");
        buffer.append(strings[1]);
        SpannableStringBuilder stringBuilder = changText(buffer.toString(), (int) (textView.getTextSize() * 1.2), sizeStart, sizeEnd);
        textView.setText(stringBuilder);
    }

    /**
     * 价格样式及颜色
     *
     * @param price
     * @param textView
     * @param color
     * @return
     */
    public static SpannableStringBuilder PriceTextChange(double price, TextView textView, int color) {
        int colorStart;
        int colorEnd;
        int sizeStart;
        int sizeEnd;
        StringBuffer buffer = new StringBuffer();
        colorStart = buffer.length();
        buffer.append("￥");
        sizeStart = buffer.length();
        String strings[] = new DecimalFormat("##0.00").format(price).split("\\.");
        buffer.append(strings[0]);
        sizeEnd = buffer.length();
        buffer.append(".");
        buffer.append(strings[1]);
        colorEnd = buffer.length();
        SpannableStringBuilder stringBuilder = changText(buffer.toString(), (int) (textView.getTextSize() * 1.2), color, sizeStart, sizeEnd, colorStart, colorEnd);
        return stringBuilder;
    }

    /**
     * 价格样式及颜色+前后字符串拼接
     *
     * @param price
     * @param textView
     * @param color
     * @return
     */
    public static SpannableStringBuilder PriceTextChange(String startString, double price, TextView textView, int color, String endString) {
        int colorStart;
        int colorEnd;
        int sizeStart;
        int sizeEnd;
        StringBuffer buffer = new StringBuffer();
        buffer.append(startString);
        colorStart = buffer.length();
        buffer.append("￥");
        sizeStart = buffer.length();
        String strings[] = new DecimalFormat("##0.00").format(price).split("\\.");
        buffer.append(strings[0]);
        sizeEnd = buffer.length();
        buffer.append(".");
        buffer.append(strings[1]);
        colorEnd = buffer.length();
        buffer.append(endString);
        SpannableStringBuilder stringBuilder = changText(buffer.toString(), (int) (textView.getTextSize() * 1.2), color, sizeStart, sizeEnd, colorStart, colorEnd);
        return stringBuilder;
    }


    /**
     * 隐藏部分显示
     */
    public static String hideStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = str.length() - 3 - 4;
        StringBuffer sb = new StringBuffer();
        sb.append(str.substring(0, 3));
        for (int i = 0; i < count; i++) {
            sb.append("*");
        }
        sb.append(str.substring(str.length() - 4));
        return sb.toString();
    }

    /**
     * 截取字符串
     * content 字符串内容
     * begin 开始位置
     * end 结束为止
     * isback 是否把字符串倒过来 0表示不需要 1表示需要
     *
     * @return
     */
    public static String subStr(String content, String begin, String end, int isback) {
        content = isback > 0 ? getRev(content) : content;
        begin = isback > 0 ? getRev(begin) : begin;
        end = isback > 0 ? getRev(end) : end;
        String temp = begin.length() == 0 ? content : (content.contains(begin) ? content.substring(content.indexOf(begin) + begin.length()) : "");
        String news = end.length() == 0 ? temp : ((end.length() > 0 && temp.indexOf(end) >= 0) ? news = temp.substring(0, temp.indexOf(end)) : "");
        return isback > 0 ? getRev(news) : news;
    }

    public static String subStr(String content, int begin, int end) {
        if (TextUtils.isEmpty(content))
            return content;
        return content.substring(begin, end);
    }

    public static String getRev(String str) {
        return new StringBuffer(str).reverse().toString();
    }

    /**
     * 字符串高亮显示
     *
     * @param str
     * @param text
     * @param color
     * @return
     */
    public static SpannableString getTextHeight(String str, String text, int color) {
        if(TextUtils.isEmpty(str)){
            return new SpannableString("");
        }
        SpannableString builder = new SpannableString(str);
        int start = str.toLowerCase().indexOf(text.toLowerCase());
        if (start != -1) {
            int end = start + text.length();
            builder.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return builder;
    }

    public static SpannableString getTextHeight(String str, String text, String color) {
        return getTextHeight(str, text, Color.parseColor(color));
    }

    public static SpannableStringBuilder getClickSpan(String str, String texts, OnSpanClickListener listener, int color){
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        int start = str.indexOf(texts);
        int end = start+texts.length();
        builder.setSpan(new Clickable(listener,texts), start, end, 0);
        builder.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder getClickSpan(String str, List<String> texts, OnSpanClickListener listener,int color){
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        for (String n:texts) {
            int start = str.indexOf(n);
            int end = start+n.length();
            if(listener!=null){
                builder.setSpan(new Clickable(listener,n), start, end, 0);
                builder.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            builder.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return builder;
    }

    public static SpannableStringBuilder getClickSpan(String str, String[] texts, OnSpanClickListener listener) {
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        for (int i = 0; i < texts.length; i++) {
            String n = texts[i];
            int start = str.indexOf(n);
            int end = start + n.length();
            builder.setSpan(new Clickable(listener, n), start, end, 0);
        }
        return builder;
    }

    public static class Clickable extends ClickableSpan {
        private final OnSpanClickListener clickListener;
        private String text;

        public Clickable(OnSpanClickListener clickListener, String text) {
            this.clickListener = clickListener;
            this.text = text;
        }

        @Override
        public void onClick(View v) {
            this.clickListener.onClickSpan(text);
        }

        public void onLongClick(){
            SLog.e("长按事件。。。");
            this.clickListener.onLongClickSpan();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.clearShadowLayer();
        }
    }
    public interface OnSpanClickListener{
        void onClickSpan(String content);
        void onLongClickSpan();
    };
}
