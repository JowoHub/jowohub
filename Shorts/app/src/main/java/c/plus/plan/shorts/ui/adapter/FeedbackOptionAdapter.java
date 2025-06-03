package c.plus.plan.shorts.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.Utils;

import java.util.List;

import c.plus.plan.shorts.R;
import c.plus.plan.shorts.databinding.ItemFeedbackOptionBinding;
import c.plus.plan.shorts.ui.entity.Menu;

public class FeedbackOptionAdapter extends RecyclerView.Adapter<FeedbackOptionAdapter.ViewHolder> {
    private List<String> options;
    private int current = -1;

    public void setOptions(List<String> options) {
        this.options = options;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public FeedbackOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeedbackOptionBinding binding = ItemFeedbackOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        FeedbackOptionAdapter.ViewHolder viewHolder= new FeedbackOptionAdapter.ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackOptionAdapter.ViewHolder holder, int position) {
        String item = options.get(position);
        holder.binding.tv.setText(item);

        if(current == position){
            holder.binding.tv.setBackgroundResource(R.drawable.bg_feedback_item_s);
            holder.binding.tv.setTextColor(Utils.getApp().getResources().getColor(R.color.white));
        } else {
            holder.binding.tv.setBackgroundResource(R.drawable.bg_feedback_item);
            holder.binding.tv.setTextColor(Utils.getApp().getResources().getColor(R.color.gray_500));
        }

        holder.binding.getRoot().setOnClickListener(v -> {
            int old = current;
            current = position;
            notifyItemChanged(old);
            notifyItemChanged(current);
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });
    }

    public String getCurrentOption(){
        if(current < 0){
            return null;
        }

        return options.get(current);
    }

    @Override
    public int getItemCount() {
        return options == null ? 0 : options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemFeedbackOptionBinding binding;
        public ViewHolder(@NonNull ItemFeedbackOptionBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, String item);
    }
}
