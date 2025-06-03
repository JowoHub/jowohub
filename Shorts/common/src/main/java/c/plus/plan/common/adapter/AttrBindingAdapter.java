package c.plus.plan.common.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.blankj.utilcode.util.TimeUtils;

public class AttrBindingAdapter {
    @BindingAdapter({"backgroundRes"})
    public static void backgroundRes(View view, int resId) {
        view.setBackgroundResource(resId);
    }

    @BindingAdapter({"time"})
    public static void time(TextView view, long time) {
        if(time > 0){
            view.setText(TimeUtils.millis2String(time));
        } else {
            view.setText(null);
        }
    }

    @BindingAdapter({"fileSize"})
    public static void fileSize(TextView view, long size) {
        view.setText(Formatter.formatFileSize(view.getContext(), size));
    }

    @BindingAdapter({"duration"})
    public static void duration(TextView view, long duration) {
        view.setText(TimeUtils.millis2String(duration, "mm:ss"));
    }

    @BindingAdapter({"html"})
    public static void html(TextView view, String text) {
        if(TextUtils.isEmpty(text)) return;
        view.setText(Html.fromHtml(text));
    }

    @BindingAdapter({"count"})
    public static void count(TextView view, long count) {
        String value;
        if(count < 1000){
            value = String.valueOf(count);
        } else if(count > 1000 && count < 10000) {
            value = (float)(Math.round(count*10/1000f))/10 + "k";
        } else {
            value = (float)(Math.round(count*10/10000f))/10 + "w";
        }
        view.setText(value);
    }

    @BindingAdapter({"msg"})
    public static void msg(TextView view, int count) {
        if(count > 99){
            view.setText("99+");
        } else {
            view.setText(String.valueOf(count));
        }

    }
}
