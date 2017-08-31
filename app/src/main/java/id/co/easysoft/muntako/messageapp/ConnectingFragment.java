package id.co.easysoft.muntako.messageapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by easysoft on 30/08/17.
 *
 */

public class ConnectingFragment extends Fragment implements Client.onConnectingSuccess {
    @BindView(R.id.addressEditText)
    EditText editTextAddress;
    @BindView(R.id.portEditText)
    EditText editTextPort;
    @BindView(R.id.connectButton)
    Button buttonConnect;
    ProgressDialog dialog;

    private Client myClient;
    String TAG = "fragment connect";

    JSONObject jsonData;
    MainActivity activity;

    public interface onClientCreated {
        public void getClient(Client c);
    }

    public onClientCreated onClientCreated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connecting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        dialog = new ProgressDialog(activity);
    }

    @OnClick(R.id.connectButton)
    public void connecting() {
        jsonData = new JSONObject();

        try {
            jsonData.put("request", Client.REQUEST_CONNECT_CLIENT);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "can't put request");
            return;
        }
        if (editTextAddress.getText().length() > 0 && editTextPort.getText().length() > 0) {
            myClient = new Client(editTextAddress.getText()
                    .toString(), Integer.parseInt(editTextPort
                    .getText().toString()), jsonData);

            myClient.setOnConnectingSuccess(this);
            activity.setMyClient(myClient);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Please fill the filed", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void connect(boolean success,String response) {
        dialog.dismiss();
        if (success) {
            Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
            activity.replaceFragment(new ChatRoomFragment());
//            startActivity(new Intent(activity,ChatRoomFragment.class));
        }
    }

    public ConnectingFragment.onClientCreated getOnClientCreated() {
        return onClientCreated;
    }

    public void setOnClientCreated(onClientCreated onClientCreated) {
        this.onClientCreated = onClientCreated;
    }
}
