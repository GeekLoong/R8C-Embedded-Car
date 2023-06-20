package net.kuisec.r8c.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.kuisec.r8c.Bean.DialogItemBean;
import net.kuisec.r8c.R;

import java.util.List;

/**
 * 对话框适配器类
 * @author Jinsn
 */
public class DialogItemAdapter extends RecyclerView.Adapter<DialogViewHolder> {
    List<DialogItemBean> list;

    public DialogItemAdapter(List<DialogItemBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog, parent, false);
        return new DialogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder dialogViewHolder, int position) {
        DialogItemBean bean = list.get(position);
        dialogViewHolder.imageView.setImageResource(bean.getIcon());
        dialogViewHolder.textView.setText(bean.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class DialogViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView textView;

    public DialogViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.item_dialog_imageview);
        textView = itemView.findViewById(R.id.item_dialog_textview);
    }
}
