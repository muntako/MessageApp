package id.co.easysoft.muntako.messageapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import butterknife.OnClick;

public class Client {

    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;
    boolean connected = false;
    Socket socket = null;
    private JSONObject jsonData;
    private String TAG = "client";
    boolean success;
    public static int CONNECT = 0;
    public static int SEND_MESSAGE = 1;


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


    Client(String addr, int port, JSONObject object) {
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
                response = "response from server :" + dataInputStream.readUTF();

                Log.i(TAG, "response " + response);
                success = response.contains("Connection Accepted");
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
            onConnectingSuccess.connect(success,response);
        }
    }

    private class sendMessage extends AsyncTask<Void, Void, String> {
        boolean success;

        @Override
        protected String doInBackground(Void... params) {
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;
            response = "";

            try {
                if (socket != null&&socket.isConnected()&&!socket.isClosed()) {

                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());

                    // transfer JSONObject as String to the server
                    dataOutputStream.writeUTF(jsonData.toString());
                    Log.i(TAG, "waiting for response from host" + jsonData.toString());

                    // Thread will wait till server replies
                    response = "response from server :" + dataInputStream.readUTF();
                    success = response.contains("Accepted");
                }
                onMessageSent.getResponse(success,response);

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

    public boolean isConnected() {
        return connected;
    }

    @OnClick(R.id.disconnectButton)
    void disconnect() {
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
