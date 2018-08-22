package forst.de.borkenbug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {

        } else {
            // Do nothing
        }
    }
}