package br.com.thiengo.chatpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by viniciusthiengo on 04/04/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private Context context;
    private LinkedList<SpannableStringBuilder> messages;

    MessagesAdapter(Context context, LinkedList<SpannableStringBuilder> messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from( context )
                .inflate(R.layout.item_message, parent, false);

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvMessage.setText( messages.get( position ) );
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        private ViewHolder(View view) {
            super(view);
            tvMessage = (TextView) view.findViewById(R.id.tv_message);
        }
    }
}
