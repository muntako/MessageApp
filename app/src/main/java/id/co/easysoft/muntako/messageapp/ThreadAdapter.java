package id.co.easysoft.muntako.messageapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Belal on 5/29/2016.
 */
//Class extending RecyclerviewAdapter
public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    //user id
    private int userId;
    private Context context;

    //Tag for tracking self Message
    private int SELF = 786;

    //ArrayList of messages object containing all the messages in the thread
    private ArrayList<Message> messages;

    //Constructor
    public ThreadAdapter(Context context, ArrayList<Message> messages, int userId){
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
        if (viewType == SELF) {
            //Inflating the layout self
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_thread, parent, false);
        }
        else {
            //else inflating the layout others
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_room_other, parent, false);
        }
        //returing the view
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Adding messages to the views
        Message message = messages.get(position);
        holder.textViewMessage.setText(message.getMessage());
        holder.textViewTime.setText(message.getSentAt());
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    //Initializing views
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTime;
        ImageView status;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
            status = (ImageView) itemView.findViewById(R.id.status);
        }
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

    public boolean setDelivered(String message){
        if (true){

        }
        return true;
    }
}