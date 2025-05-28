package c.plus.plan.skits.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import c.plus.plan.jowosdk.entity.BlockAndDramas;
import c.plus.plan.jowosdk.entity.Drama;
import c.plus.plan.skits.DataApiInfo;
import c.plus.plan.skits.databinding.ItemBlockAndDramasBinding;

public class BlockAndDramasAdapter extends RecyclerView.Adapter<BlockAndDramasAdapter.ViewHolder> {
    private List<BlockAndDramas> blockAndDramasList;
    private OnItemClickListener onItemClickListener;

    public void setBlockAndDramasList(List<BlockAndDramas> blockAndDramasList) {
        this.blockAndDramasList = blockAndDramasList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlockAndDramasBinding binding = ItemBlockAndDramasBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder= new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BlockAndDramas item = blockAndDramasList.get(position);
        if(item.getBlock() != null){
            holder.binding.name.setText(item.getBlock().getName());
        }

        holder.adapter.setDramas(item.getDramas());
        holder.adapter.notifyDataSetChanged();
        holder.adapter.setOnItemClickListener(new DramaAdapter.OnItemClickListener() {
            @Override
            public void click(int position, Drama item) {
                if(onItemClickListener != null){
                    onItemClickListener.click(position, item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockAndDramasList == null ? 0 : blockAndDramasList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemBlockAndDramasBinding binding;
        private DramaAdapter adapter;
        public ViewHolder(@NonNull ItemBlockAndDramasBinding item) {
            super(item.getRoot());
            binding = item;


            adapter = new DramaAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false);
            binding.rv.setAdapter(adapter);
            binding.rv.setLayoutManager(layoutManager);
        }
    }

    public interface OnItemClickListener {
        void click(int position, Drama item);
    }
}
