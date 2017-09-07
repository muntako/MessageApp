//package id.co.easysoft.muntako.messageapp;
//
//import android.annotation.TargetApi;
//import android.app.Fragment;
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import id.co.easysoft.muntako.messageapp.Fragment.ConnectingFragment;
//
//import static id.co.easysoft.muntako.messageapp.Constant.SEND_MESSAGE_CLIENT;
//
///**
// * Created by easysoft on 30/08/17.
// *
// */
//
//public class MessagingFragment extends Fragment implements Client.onMessageSent,Client.onConnectionChange{
//
//    String TAG = "fragment Message";
//
//    @BindView(R.id.responseTextView)
//    TextView response;
//    @BindView(R.id.clearButton)
//    Button buttonClear;
//    @BindView(R.id.send)
//    Button send;
//    @BindView(R.id.message)
//    EditText message;
//    @BindView(R.id.disconnectButton)
//    Button disconnect;
//
//    Client myClient;
//    JSONObject jsonData;
//    MainActivity activity;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_messaging,container,false);
//        ButterKnife.bind(this,view);
//        return view;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        activity = (MainActivity) context;
//        myClient = activity.getMyClient();
//        myClient.setOnMessageSent(this);
//    }
//
//    @OnClick(R.id.send)
//    public void sendMessage() {
//        jsonData = new JSONObject();
//
//        try {
//            jsonData.put("request", SEND_MESSAGE_CLIENT);
//            jsonData.put("Message",message.getText().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "can't put request");
//            return;
//        }
//        if (message.getText().length() > 0) {
//            myClient.setJsonData(jsonData);
//            myClient.setOnMessageSent(this);
//        } else {
//            Toast.makeText(getActivity(), "Please fill the filed", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    @OnClick(R.id.disconnectButton)
//    public void disconnecting() {
//        if (myClient!=null) {
//            myClient.disconnect();
//            activity.replaceFragment(new ConnectingFragment());
//        }
//    }
//
//    @OnClick(R.id.clearButton)
//    public void clearing() {
//        response.setText("");
//    }
//
//
//    @Override
//    public void connect(boolean success,String response) {
//        if (!success){
//            Toast.makeText(activity,"Socket disconnected", Toast.LENGTH_SHORT).show();
//            disconnecting();
//        }
//    }
//
//
//    @Override
//    public void doAction(boolean success, final String message) {
//            threadUI(message);
//    }
//    public void threadUI(final String message){
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                response.setText(message);
//            }
//        });
//    }
//}
