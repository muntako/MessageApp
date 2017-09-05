package id.co.easysoft.muntako.messageapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import butterknife.OnClick;
import id.co.easysoft.muntako.messageapp.model.ResponseFromServer;

public class Client {

    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;
    private boolean connected = false;
    private Socket socket = null;
    private JSONObject jsonData;
    private String TAG = "client";
    private boolean success;
    ResponseFromServer fromServer;


    public static final String REQUEST_CONNECT_CLIENT = "request-connect-client";
    public static final String SEND_MESSAGE_CLIENT = "send-Message-client";

    public interface onConnectingSuccess {
        void connect(boolean success, String response);
    }

    public interface onMessageSent{
        void getResponse(boolean success, String message);
    }
    onConnectingSuccess onConnectingSuccess;
    onMessageSent onMessageSent;


    public Client(String addr, int port, JSONObject object) {
        dstAddress = addr;
        dstPort = port;
        jsonData = object;
        new connecting().execute();
    }

    Client(JSONObject object) {
        jsonData = object;
        new sendMessage().execute();
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
        new sendMessage().execute();
    }

    class connecting extends AsyncTask<Void, Void, Boolean> {


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
                dataOutputStream.writeUTF(jsonData.toString());
                Log.i(TAG, "waiting for response from host" + jsonData.toString());

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
                onConnectingSuccess.connect(success, fromServer.getMessage());
                new Thread(new alwaysListening()).start();
            }else {
                onConnectingSuccess.connect(success,response);
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
                    dataOutputStream.writeUTF(jsonData.toString());
                    Log.i(TAG, "waiting for response from host" + jsonData.toString());
                }

//                onMessageSent.getResponse(success,fromServer.getMessage());

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
                    onMessageSent.getResponse(fromServer.isSuccess(), fromServer.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
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
                onConnectingSuccess.connect(connected,"Connection closed");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getResponse() {
        return response;
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public Client.onMessageSent getOnMessageSent() {
        return onMessageSent;
    }

    public void setOnMessageSent(Client.onMessageSent onMessageSent) {
        this.onMessageSent = onMessageSent;
    }

    public Client.onConnectingSuccess getOnConnectingSuccess() {
        return onConnectingSuccess;
    }

    public void setOnConnectingSuccess(Client.onConnectingSuccess onConnectingSuccess) {
        this.onConnectingSuccess = onConnectingSuccess;
    }

}
