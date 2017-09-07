package id.co.easysoft.muntako.messageapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import butterknife.OnClick;
import id.co.easysoft.muntako.messageapp.model.ResponseFromServer;

import static id.co.easysoft.muntako.messageapp.Constant.MESSAGE_DELIVERED;
import static id.co.easysoft.muntako.messageapp.Constant.SEND_MESSAGE_CLIENT;

public class Client {

    private String dstAddress;
    private int dstPort;
    private String response = "";
    TextView textResponse;
    private boolean connected = false;
    private Socket socket = null;
    private String jsonData;
    private String TAG = "client";
    private boolean success;
    ResponseFromServer fromServer;

    public interface onConnectionChange {
        void connect(boolean success, String response);
    }

    public interface onMessageSent{
        void doAction(boolean success, String message);
    }

    public interface onReceiveMessage{
        void showMessage(String sender,String message);
    }
    private onConnectionChange onConnectionChange;
    private onMessageSent onMessageSent;
    private onReceiveMessage onReceiveMessage;


    public Client(String addr, int port, String object) {
        dstAddress = addr;
        dstPort = port;
        jsonData = object;
        new connecting().execute();
    }

    Client(String object) {
        jsonData = object;
        new sendMessage().execute();
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
        new sendMessage().execute();
    }

    private class connecting extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... arg0) {

            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                socket.setKeepAlive(true);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                // transfer JSONObject as String to the server
                dataOutputStream.writeUTF(jsonData);
                Log.i(TAG, "waiting for response from host" + jsonData);

                // Thread will wait till server replies
                response = dataInputStream.readUTF();
                fromServer = new Gson().fromJson(response,ResponseFromServer.class);

                Log.i(TAG, "response " + response);
                success = fromServer.isSuccess();
                connected = success;

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } catch (Exception e) {
                e.printStackTrace();
                response = "" + e.toString();
            }
            return connected;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (success) {
                onConnectionChange.connect(true, fromServer.getMessage());
                new Thread(new alwaysListening()).start();
            }else {
                onConnectionChange.connect(false,response);
            }
        }
    }

    private class sendMessage extends AsyncTask<Void, Void, String> {
        boolean success;

        @Override
        protected String doInBackground(Void... params) {
            DataOutputStream dataOutputStream = null;
            response = "";

            try {
                if (socket != null&&socket.isConnected()&&!socket.isClosed()) {
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    // transfer JSONObject as String to the server
                    dataOutputStream.writeUTF(jsonData);
                    Log.i(TAG, "waiting for response from host" + jsonData);
                }

//                onMessageSent.doAction(success,fromServer.getMessage());

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (SocketException e){
                response = "SocketException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.getMessage()+e.getCause();
            }
            Log.i("Server Response",response);
            return null;
        }
    }

    private class alwaysListening implements Runnable{

        DataInputStream dataInputStream = null;
        @Override
        public void run() {

            while (socket != null&&socket.isConnected()&&!socket.isClosed()) {
                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    // Thread will wait till server replies
                    response = dataInputStream.readUTF();
                    System.out.print(response);
                    try {
                        fromServer = new Gson().fromJson(response, ResponseFromServer.class);
                        success = fromServer.isSuccess();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fromServer.getStatus().equalsIgnoreCase(MESSAGE_DELIVERED)){
                        onMessageSent.doAction(fromServer.isSuccess(), fromServer.getIdMessage());
                    }else if(fromServer.getStatus().equalsIgnoreCase(SEND_MESSAGE_CLIENT)){
                        onConnectionChange.connect(fromServer.isSuccess(),fromServer.getMessage());
                    }
                    else {
                        onReceiveMessage.showMessage(fromServer.getSender(),fromServer.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            onConnectionChange.connect(false,"connection closed");
        }
    }

    public boolean isConnected() {
        return connected;
    }

    @OnClick(R.id.disconnectButton)
    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                connected = false;
                onConnectionChange.connect(false,"Connection closed");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getResponse() {
        return response;
    }

    public Client.onMessageSent getOnMessageSent() {
        return onMessageSent;
    }

    public void setOnMessageSent(Client.onMessageSent onMessageSent) {
        this.onMessageSent = onMessageSent;
    }

    public onConnectionChange getOnConnectionChange() {
        return onConnectionChange;
    }

    public void setOnConnectionChange(onConnectionChange onConnectionChange) {
        this.onConnectionChange = onConnectionChange;
    }

    public Client.onReceiveMessage getOnReceiveMessage() {
        return onReceiveMessage;
    }

    public void setOnReceiveMessage(Client.onReceiveMessage onReceiveMessage) {
        this.onReceiveMessage = onReceiveMessage;
    }
}
