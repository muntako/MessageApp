package id.co.easysoft.muntako.messageapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ConnectingFragment.onClientCreated{

    Client myClient;
    private String TAG = "mainactivity";

    public interface activeClient{
        public void onReceiveClient(Client c);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        replaceFragment(new ConnectingFragment());

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void replaceFragment(Fragment fragment){
        Class fragmentClass = fragment.getClass();
        Fragment f = null;
        try{
            f = (Fragment) fragmentClass.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout,f).commit();
    }

    @Override
    public void getClient(Client c) {
        myClient = c;
    }

    public Client getMyClient() {
        return myClient;
    }

    public void setMyClient(Client myClient) {
        this.myClient = myClient;
    }
}
