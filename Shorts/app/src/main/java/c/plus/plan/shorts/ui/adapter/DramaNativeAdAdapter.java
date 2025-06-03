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
//            item.showAd(holder.binding.getRoot(), new TPNativeAdRender() {
//
//                // 获取广告布局文件，用自定义渲染的方式可以自己写布局的id
//                @Override
//                public ViewGroup createAdLayoutView() {
//                    LayoutInflater inflater = (LayoutInflater) Utils.getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    return (ViewGroup) inflater.inflate(R.layout.tp_native_ad_list_item, null);
//                }
//
//                @Override
//                public ViewGroup renderAdView(TPNativeAdView tpNativeAdView) {
//                    ViewGroup viewGroup = createAdLayoutView();
//
//                    ImageView imageView = viewGroup.findViewById(R.id.tp_mopub_native_main_image);
//                    if(imageView != null) {
//                        if(tpNativeAdView.getMediaView() != null) {
//                            // 如果三方广告平台有mediaview，需要用三方提供的mediaview来替换原来布局中的imageview
//                            ViewGroup.LayoutParams params = imageView.getLayoutParams();
//                            ViewParent viewParent = imageView.getParent();
//                            if(viewParent != null) {
//                                ((ViewGroup)viewParent).removeView(imageView);
//                                ((ViewGroup)viewParent).addView(tpNativeAdView.getMediaView(), params);
//                                getClickViews().add(tpNativeAdView.getMediaView());
//                            }
//                        } else if(tpNativeAdView.getMainImage() != null) {
//                            // 部分三方平台返回的是drawable，可以直接设置
//                            imageView.setImageDrawable(tpNativeAdView.getMainImage());
//                        } else if(tpNativeAdView.getMainImageUrl() != null) {
//                            // 其他三方平台返回的是图片的url，需要先下载图片再填充到view中
//                            TPImageLoader.getInstance().loadImage(imageView, tpNativeAdView.getMainImageUrl());
//                        }
//                    }
//
//                    ImageView iconView = viewGroup.findViewById(R.id.tp_native_icon_image);
//                    if(iconView != null) {
//                        iconView.setVisibility(View.VISIBLE);
//                        if(tpNativeAdView.getIconImage() != null) {
//                            iconView.setImageDrawable(tpNativeAdView.getIconImage());
//                        } else if(tpNativeAdView.getIconImageUrl() != null){
//                            TPImageLoader.getInstance().loadImage(iconView, tpNativeAdView.getIconImageUrl());
//                        } else if (tpNativeAdView.getIconView() != null) {
//                            ViewGroup.LayoutParams params = iconView.getLayoutParams();
//                            ViewParent viewParent = iconView.getParent();
//                            if (viewParent != null) {
//                                int index = ((ViewGroup)viewParent).indexOfChild(iconView);
//                                ((ViewGroup) viewParent).removeView(iconView);
//                                tpNativeAdView.getIconView().setId(R.id.tp_native_icon_image);
//                                ((ViewGroup) viewParent).addView(tpNativeAdView.getIconView(),index, params);
//
//                            }
//                        } else {
//                            iconView.setVisibility(View.GONE);
//                        }
//                    }
//
//                    TextView titleView = viewGroup.findViewById(R.id.tp_native_title);
//                    if(titleView != null && tpNativeAdView.getTitle() != null) {
//                        titleView.setText(tpNativeAdView.getTitle());
//                    }
//
//                    TextView subTitleView = viewGroup.findViewById(R.id.tp_native_text);
//                    if(subTitleView != null && tpNativeAdView.getSubTitle() != null) {
//                        subTitleView.setText(tpNativeAdView.getSubTitle());
//                    }
//
//                    Button callToActionView = viewGroup.findViewById(R.id.tp_native_cta_btn);
//                    if(callToActionView != null && tpNativeAdView.getCallToAction() != null) {
//                        callToActionView.setText(tpNativeAdView.getCallToAction());
//                    }
//
////                    ImageView close = viewGroup.findViewById(R.id.close);
////                    close.setOnClickListener(v -> {
////                        holder.binding.getRoot().removeAllViews();
////                    });
//
//                    // facebook会需要一个adchoice的容器来填充adchoice
//                    FrameLayout adChoiceView = viewGroup.findViewById(R.id.tp_ad_choices_container);
//
//                    // 把主要的元素设置给三方广告平台，第二个参数是是否可以点击
//                    setImageView(imageView, true);
//                    setIconView(iconView, true);
//                    setTitleView(titleView, true);
//                    setSubTitleView(subTitleView, true);
//                    setCallToActionView(callToActionView, true);
//                    setAdChoicesContainer(adChoiceView, false);
//
//                    return viewGroup;
//                }
//            }, "");

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
