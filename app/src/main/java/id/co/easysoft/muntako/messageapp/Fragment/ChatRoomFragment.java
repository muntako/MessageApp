package id.co.easysoft.muntako.messageapp.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.co.easysoft.muntako.messageapp.Client;
import id.co.easysoft.muntako.messageapp.Client.onConnectionChange;
import id.co.easysoft.muntako.messageapp.MainActivity;
import id.co.easysoft.muntako.messageapp.model.Message;
import id.co.easysoft.muntako.messageapp.R;
import id.co.easysoft.muntako.messageapp.ThreadAdapter;
import id.co.easysoft.muntako.messageapp.model.ResponseFromServer;

import static android.content.Context.WIFI_SERVICE;
import static id.co.easysoft.muntako.messageapp.Constant.MESSAGE_HAS_BEEN_READ;
import static id.co.easysoft.muntako.messageapp.Constant.SEND_MESSAGE_CLIENT;

/**
 * Created by ADMIN on 31-Aug-17.
 *
 */

public class ChatRoomFragment extends Fragment implements View.OnClickListener, Client.onMessageSent,
        Client.onReceiveMessage,Client.onConnectionChange,Client.onMessageRead{
    //Recyclerview objects
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ThreadAdapter adapter;

    //ArrayList of messages to store the thread messages
    private ArrayList<Message> messages = new ArrayList<>();

    //Button to send new message on the thread
    private Button buttonSend;

    //EditText to send new message on the thread
    private EditText editTextMessage;
    Client myClient;
    private String TAG = "Chat Activity";
    MainActivity activity;
    String ipAddressDestination = "", nickname = "";
    private String ipAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_room, container, false);
        //Adding toolbar to activity
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        //Initializing recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThreadAdapter(activity, messages, 1);

        //Initializing message arraylist
        messages = new ArrayList<>();

        //Calling function to fetch the existing messages on the thread
        fetchMessages();

        //initializing button and edittext
        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        editTextMessage = (EditText) view.findViewById(R.id.editTextMessage);

        //Adding listener to button
        buttonSend.setOnClickListener(this);
        displayDestinationForm();
        return view;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        myClient = activity.getMyClient();
        myClient.setOnMessageSent(this);
        myClient.setOnReceiveMessage(this);
        myClient.setOnMessageRead(this);
        setHasOptionsMenu(true);
        nickname = activity.getNickname();
    }

    void displayDestinationForm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.fragment_contact, null);
        final TextView ipAddressTest = (TextView) view.findViewById(R.id.addressEditText);
        builder.setView(view);
        builder.setTitle("Chat Setting");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ipAddressDestination = ipAddressTest.getText().toString();
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(activity);
        this.activity = (MainActivity) a;
        myClient = activity.getMyClient();
        myClient.setOnMessageSent(this);
        myClient.setOnReceiveMessage(this);
        myClient.setOnMessageRead(this);
        setHasOptionsMenu(true);
        nickname = activity.getNickname();
    }

    //This method will fetch all the messages of the thread
    private void fetchMessages() {
        adapter = new ThreadAdapter(activity, messages, 1);
        recyclerView.setAdapter(adapter);
        scrollToBottom();
    }

    //Processing message to add on the thread
    private void processMessage(String name, String message, String id) {
        Message m = new Message(Integer.parseInt(id), message, getTimeStamp(), name);
        messages.add(m);
        //TODO set scroll to bottom
        scrollToBottom();
    }

    //This method will send the new message to the thread
    private void sendMessage() {
        String jsonData;

        long sentAt = System.currentTimeMillis();
        int userId = 1;
        String name = activity.getNickname();
        WifiManager wm = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


        final String message = editTextMessage.getText().toString().trim();
        if (!message.equalsIgnoreCase("")) {
            Message m = new Message(userId, message, sentAt, nickname);

            RequestToServer toServer = new RequestToServer(SEND_MESSAGE_CLIENT,ipAddress,message,ipAddressDestination,nickname,sentAt+"");
            jsonData = new Gson().toJson(toServer);
            myClient.setJsonData(jsonData);
            myClient.setOnMessageSent(this);
            System.out.println("Request "+toServer);
            messages.add(m);
            adapter.notifyDataSetChanged();

            scrollToBottom();
            adapter.setMessages(messages);

            editTextMessage.setText("");
        } else {
            return;
        }
    }

    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    //This method will return current timestamp
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    //Sending message onclick
    @Override
    public void onClick(View v) {
        if (v == buttonSend)
            sendMessage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    //Adding logout option here
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            disconnecting();
        } else if (id == R.id.action_list_contact) {
            displayDestinationForm();
        }
        return super.onOptionsItemSelected(item);
    }

    public void disconnecting() {
        if (myClient != null) {
            myClient.disconnect();
        }
        activity.replaceFragment(new ConnectingFragment());
    }

    @Override
    public void doAction(boolean success, final String message) {
        if (success) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.setDelivered(message);
                }
            });
        }
    }

    @Override
    public void showMessage(final ResponseFromServer fromServer) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                processMessage(fromServer.getSender(), fromServer.getMessage(), "0");
                RequestToServer toServer = new RequestToServer(MESSAGE_HAS_BEEN_READ,ipAddress,"message has been read",
                        fromServer.getIpAddressSender(),nickname,fromServer.getIdMessage());
                myClient.setJsonData(new Gson().toJson(toServer));
            }
        });

    }

    @Override
    public void connect(boolean success, String response) {
        if (!success){
            disconnecting();
        }
    }

    @Override
    public void hasBeenRead(boolean read, final String id) {
        if (read){
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.setHasBeenRead(id);
                }
            });
        }
    }
}
