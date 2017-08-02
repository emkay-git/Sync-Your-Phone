package android.cloud.microsoft.com.serviceapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mohit on 8/7/17.
 */

//Registers itself in observable pattern as server. and send notifications to activity.
public class NetworkChange extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        {
            CheckConnectivity cc = new CheckConnectivity(context);
            boolean result = cc.isNetworkAvailable();
            Intent i = new Intent(context, MyService.class);
            if(result)
            {

                context.startService(i);

                Log.i("wifiConnectionCheck","yes");
            }
            else
            {
                context.stopService(i);
                Log.i("wifiConnectionCheck", "no");
            }
//            Log.i("Wifi","Working");
            ObservableObject.getInstance().updateValue(result);
        }


    }
}
