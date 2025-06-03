package c.plus.plan.shorts.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ad.AdNative;
import c.plus.plan.shorts.databinding.ItemAdGridViewBinding;
import c.plus.plan.shorts.databinding.ItemAdViewBinding;
import c.plus.plan.shorts.databinding.ItemDramaGridBinding;

public class DramaGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int ITEM_VIEW_TYPE_NORMAL = 1;
    private static final int ITEM_VIEW_TYPE_AD = 2;
    private List<Object> mData;
    private int imageHeight;
    private int imageWidth;
    private int titleLines = 2;

    public void setData(List<Object> mData) {
        this.mData = mData;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE_AD){
            ItemAdGridViewBinding binding = ItemAdGridViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            AdViewHolder viewHolder= new AdViewHolder(binding);
            return viewHolder;
        } else {
            ItemDramaGridBinding binding = ItemDramaGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            ViewHolder viewHolder= new ViewHolder(binding);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vholder, int position) {
        Object o = mData.get(position);
        StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) vholder.itemView.getLayoutParams();

        if(o instanceof Drama){
            layoutParams.setFullSpan(false);
            ViewHolder holder = (ViewHolder) vholder;
            Drama item = (Drama) o;

            String bannerUrl = item.getBanner();
            if (bannerUrl != null && !bannerUrl.startsWith("https://")) {
                String host = Uri.parse(item.getCover()).getHost();
                LogUtils.d("host:" + host);
                bannerUrl = "https://" + host +"/"+ bannerUrl;
            }
            if (bannerUrl == null) {
                bannerUrl = item.getCover();
            }

            Glide.with(holder.binding.iv).load(bannerUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);
            holder.binding.tv.setText(item.getName());
            holder.binding.tv.setLines(titleLines);

            if(imageHeight > 0){
                LinearLayout.LayoutParams ls = (LinearLayout.LayoutParams) holder.binding.iv.getLayoutParams();
                ls.height = imageHeight;
                holder.binding.iv.setLayoutParams(ls);
            }
            holder.binding.getRoot().setOnClickListener(v -> {
                if(onItemClickListener != null){
                    onItemClickListener.click(position, item);
                }
            });
        } else if(o instanceof AdNative){
            layoutParams.setFullSpan(true);
            AdViewHolder holder = (AdViewHolder) vholder;
            AdNative item = (AdNative) o;
            holder.binding.getRoot().setVisibility(View.VISIBLE);
            holder.binding.ad.removeAllViews();
            holder.binding.close.setOnClickListener(v -> {
                holder.binding.getRoot().setVisibility(View.GONE);
                holder.binding.getRoot().removeAllViews();
            });
            item.showAd();

        }

        vholder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null) {
            Object o = mData.get(position);
            if (o instanceof Drama) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_AD;
            }

        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemDramaGridBinding binding;
        public ViewHolder(@NonNull ItemDramaGridBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {
        ItemAdGridViewBinding binding;

        public AdViewHolder(@NonNull ItemAdGridViewBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, Drama item);
    }
}
