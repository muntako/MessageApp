package id.co.easysoft.muntako.messageapp.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.easysoft.muntako.messageapp.Client;
import id.co.easysoft.muntako.messageapp.MainActivity;
import id.co.easysoft.muntako.messageapp.R;
import id.co.easysoft.muntako.messageapp.model.RequestToServer;

import static android.content.Context.WIFI_SERVICE;
import static id.co.easysoft.muntako.messageapp.Constant.REQUEST_CONNECT_CLIENT;

/**
 * Created by easysoft on 30/08/17.
 *
 */

public class ConnectingFragment extends Fragment implements Client.onConnectionChange {
    @BindView(R.id.addressEditText)
    EditText editTextAddress;
    @BindView(R.id.portEditText)
    EditText editTextPort;
    @BindView(R.id.nicknameEditText)
    EditText editTextNickname;
    @BindView(R.id.connectButton)
    Button buttonConnect;
    ProgressDialog dialog;

    private Client myClient;
    String TAG = "fragment connect";

    JSONObject jsonData;
    MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connecting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (MainActivity) activity;
        dialog = new ProgressDialog(activity);
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (MainActivity) getActivity();
        }
        dialog = new ProgressDialog(activity);
    }


    @OnClick(R.id.connectButton)
    public void connecting() {
        jsonData = new JSONObject();
        WifiManager wm = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        RequestToServer toServer = new RequestToServer(REQUEST_CONNECT_CLIENT, ipAddress);
        String request = new Gson().toJson(toServer);
        if (editTextAddress.getText().length() > 0 && editTextPort.getText().length() > 0 && editTextNickname.getText().length() > 0) {
            myClient = new Client(editTextAddress.getText()
                    .toString(), Integer.parseInt(editTextPort
                    .getText().toString()), request);

            myClient.setOnConnectionChange(this);
            activity.setMyClient(myClient);
            dialog.show();
            activity.setNickname(editTextNickname.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please fill the filed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void connect(final boolean success, final String response) {
        if (dialog != null)
            dialog.dismiss();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                    activity.replaceFragment(new ChatRoomFragment());
                } else {
                    Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
