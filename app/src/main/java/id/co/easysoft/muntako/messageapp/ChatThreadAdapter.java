package id.co.easysoft.muntako.messageapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.co.easysoft.muntako.messageapp.model.Message;

/**
 * Created by Belal on 5/29/2016.
 *
 */
//Class extending RecyclerviewAdapter
public class ChatThreadAdapter extends RecyclerView.Adapter<ChatThreadAdapter.ViewHolder> {

    //user id
    private int userId;
    private Context context;

    //Tag for tracking self Message
    private int SELF = 786;

    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messages;
    private ViewGroup parent;

    //Constructor
    public ChatThreadAdapter(Context context, ArrayList<Message> messages, int userId) {
        this.userId = userId;
        this.messages = messages;
        this.context = context;
    }

    //IN this method we are tracking the self Message
    @Override
    public int getItemViewType(int position) {
        //getting Message object of current position
        Message message = messages.get(position);

        //If its owner  id is  equals to the logged in user id
        if (message.getUsersId() == userId) {
            //Returning self
            return SELF;
        }
        //else returning position
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creating view
        View itemView = null;
        //if view type is self
        this.parent = parent;
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);
        } else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_room_other, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Adding messages to the views
        Message message = messages.get(position);
        holder.textViewMessage.setText(message.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(message.getSentAt());

        holder.textViewTime.setText(sdf.format(resultdate));

        if (holder.getItemViewType() == SELF) {
           holder.status.setEnabled(message.isDelivered());
            if (holder.status.isEnabled()&&message.isHasBeenRead()){
                holder.status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_read));
            }

        }else {
            holder.textViewSender.setText(message.getName());
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTime,textViewSender;
        ImageView status;

        ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            status = (ImageView) itemView.findViewById(R.id.status);
            textViewSender = (TextView)itemView.findViewById(R.id.TextViewSender);
        }

        onMessageSent onMessageSent;

        public ChatThreadAdapter.onMessageSent getOnMessageSent() {
            return onMessageSent;
        }

        public void setOnMessageSent(ChatThreadAdapter.onMessageSent onMessageSent) {
            this.onMessageSent = onMessageSent;
        }
    }

    interface onMessageSent {
        void changeStatus(boolean b);
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }


    public boolean setDelivered(String id) {
        for (int i = 0; i < messages.size(); i++) {
            if ((messages.get(i).getSentAt()+"").equalsIgnoreCase(id)) {
                messages.get(i).setDelivered(true);
                notifyDataSetChanged();
            }
        }
        return false;
    }

    public boolean setHasBeenRead(String id){
        for (int i = 0; i < messages.size(); i++) {
            if ((messages.get(i).getSentAt()+"").equalsIgnoreCase(id)) {
                messages.get(i).setHasBeenRead(true);
                notifyDataSetChanged();
            }
        }
        return false;
    }
}