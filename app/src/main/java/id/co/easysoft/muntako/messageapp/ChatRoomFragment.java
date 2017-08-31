package id.co.easysoft.muntako.messageapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.co.easysoft.muntako.messageapp.MainActivity.activeClient;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by ADMIN on 31-Aug-17.
 *
 */

public class ChatRoomFragment extends Fragment implements View.OnClickListener, Client.onMessageSent,
        Client.onConnectingSuccess{
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
    String ipAddress ="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_room,container,false);
        //Adding toolbar to activity
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);


        //Initializing recyclerview
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThreadAdapter(activity,messages,1);

        //Initializing message arraylist
        messages = new ArrayList<>();


        //Calling function to fetch the existing messages on the thread
        fetchMessages();

        //initializing button and edittext
        buttonSend = (Button) view.findViewById(R.id.buttonSend);
        editTextMessage = (EditText) view.findViewById(R.id.editTextMessage);

        //Adding listener to button
        buttonSend.setOnClickListener(this);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        myClient = activity.getMyClient();
        myClient.setOnMessageSent(this);
        setHasOptionsMenu(true);
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
    private void sendMessage()  {
        JSONObject jsonData;

        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(time);
        String sentAt = sdf.format(resultdate);
        int userId = 1;
        String name = "muntako";
        WifiManager wm = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


        final String message = editTextMessage.getText().toString().trim();
        if (!message.equalsIgnoreCase("")) {
            Message m = new Message(userId, message, sentAt, name);

            jsonData = new JSONObject();

            try {
                jsonData.put("request", Client.SEND_MESSAGE_CLIENT);
                jsonData.put("Message", message);
                jsonData.put("ipAddress", ipAddress);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "can't put request");
                return;
            }

            myClient.setJsonData(jsonData);
            myClient.setOnMessageSent(this);

            messages.add(m);
            adapter.notifyDataSetChanged();

            scrollToBottom();
            adapter.setMessages(messages);

            editTextMessage.setText("");
        }else {
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
    public static String getTimeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date());
    }

    //Sending message onclick
    @Override
    public void onClick(View v) {
        if (v == buttonSend)
            sendMessage();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    //Adding logout option here
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuLogout) {
            disconnecting();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void disconnecting() {
        if (myClient != null) {
            myClient.disconnect();
            activity.replaceFragment(new ConnectingFragment());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void connect(boolean success, String response) {
        if (!success){
            activity.replaceFragment(new ConnectingFragment());
        }
    }

    @Override
    public void getResponse(boolean success, final String message) {
        if (success){
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    processMessage("response", message, "0");
                    //TODO implement on message delivered to server, messaga single check
//                    adapter
                }
            });
        }
    }

}
