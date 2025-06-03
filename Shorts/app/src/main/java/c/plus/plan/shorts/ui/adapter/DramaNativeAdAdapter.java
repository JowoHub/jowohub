package c.plus.plan.shorts.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.shorts.R;
import c.plus.plan.shorts.ad.AdNative;
import c.plus.plan.shorts.databinding.ItemAdViewBinding;
import c.plus.plan.shorts.databinding.ItemDramaNativeAdBinding;

public class DramaNativeAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int ITEM_VIEW_TYPE_NORMAL = 1;
    private static final int ITEM_VIEW_TYPE_AD = 2;
    private List<Object> mData;
    private int imageHeight;
    private int titleLines = 2;


    public void setData(List<Object> mData) {
        this.mData = mData;
    }

    public void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    private DramaNativeAdAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(DramaNativeAdAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_AD) {
            ItemAdViewBinding binding = ItemAdViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            AdViewHolder viewHolder = new AdViewHolder(binding);
            return viewHolder;
        } else {
            ItemDramaNativeAdBinding binding = ItemDramaNativeAdBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            DramaNativeAdAdapter.ViewHolder viewHolder = new DramaNativeAdAdapter.ViewHolder(binding);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vholder, int position) {
        Object o = mData.get(position);
        if (o instanceof Drama) {
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
            Glide.with(holder.binding.iv).load(bannerUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.iv);
            holder.binding.tv.setText(item.getName());
            holder.binding.tv.setLines(titleLines);

            if (imageHeight > 0) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.binding.iv.getLayoutParams();
                layoutParams.height = imageHeight;
                holder.binding.iv.setLayoutParams(layoutParams);
            }
            holder.binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.click(position, item);
                }
            });
        } else if(o instanceof AdNative){
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemDramaNativeAdBinding binding;

        public ViewHolder(@NonNull ItemDramaNativeAdBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {
        ItemAdViewBinding binding;

        public AdViewHolder(@NonNull ItemAdViewBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, Drama item);
    }
}
