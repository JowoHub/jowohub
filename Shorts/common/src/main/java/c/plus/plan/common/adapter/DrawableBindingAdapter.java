package c.plus.plan.common.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class DrawableBindingAdapter {
    @BindingAdapter({"imageRes"})
    public static void loadImage(ImageView view, int res) {
        Glide.with(view.getContext()).load(res).into(view);
    }

    @BindingAdapter({"imageBitmap"})
    public static void loadImageBitmap(ImageView view, Bitmap bitmap) {
        Glide.with(view.getContext()).load(bitmap).into(view);
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }


    @BindingAdapter({"imageRadiusUrl", "imageRadius"})
    public static void loadImageRadius(ImageView view, String url, int radius) {
        Glide.with(view.getContext()).load(url)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(SizeUtils.dp2px(radius))))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }

    @BindingAdapter({"imageOval"})
    public static void loadImageOval(ImageView view, String url) {
        Glide.with(view.getContext()).load(url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }
}
