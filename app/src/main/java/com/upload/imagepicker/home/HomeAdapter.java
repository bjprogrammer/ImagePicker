package com.upload.imagepicker.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.upload.imagepicker.BR;
import com.upload.imagepicker.R;
import com.upload.imagepicker.model.Upload;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Upload> data;

    // flag for footer ProgressBar (i.e. last item of list)
    static final int ITEM = 0;

    HomeAdapter() {
        data = new ArrayList<>();
    }

    public interface OnCustomCardListener {
        void onViewImageClick(String url);
    }

    private OnCustomCardListener listener;

    void setOnCustomCardClickListener(OnCustomCardListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (i == ITEM) {
            viewHolder = new ViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_feed, viewGroup, false));
        }

        return viewHolder;
    }

    void updateList(List<Upload> _list) {
        this.data = _list;
    }

    void clear() {
        if(data !=null) {
            data.clear();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        if (getItemViewType(i) == ITEM) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.bind(data.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        void bind(Object obj) {
            binding.setVariable(BR.upload, obj);
            binding.executePendingBindings();

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onViewImageClick(((Upload)obj).getImageUrl());
                }
            });
        }
    }
}
