package c.plus.plan.skits.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import c.plus.plan.skits.DataApiInfo;
import c.plus.plan.skits.databinding.ItemDataApiBinding;

public class DataApiAdapter extends RecyclerView.Adapter<DataApiAdapter.ViewHolder> {
    private List<DataApiInfo> dataApiInfos;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setDataApiInfos(List<DataApiInfo> dataApiInfos) {
        this.dataApiInfos = dataApiInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDataApiBinding binding = ItemDataApiBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataApiInfo item = dataApiInfos.get(position);
        holder.binding.name.setText(item.getMethodDesc());
        String desc = "方法: " + item.getMethod();
        if(!TextUtils.isEmpty(item.getParamsDesc())){
            desc += "; 参数: " + item.getParamsDesc();
        }

        holder.binding.desc.setText(desc);


        holder.itemView.setOnClickListener(view -> {
            if(onItemClickListener != null){
                onItemClickListener.click(position, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataApiInfos == null ? 0 : dataApiInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemDataApiBinding binding;
        public ViewHolder(@NonNull ItemDataApiBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }

    public interface OnItemClickListener {
        void click(int position, DataApiInfo item);
    }
}
