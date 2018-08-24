package forst.de.borkenbug;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WaypointSync {
    public static void syncWaypoint(Waypoint wp, Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://borkenbug.balja.org/bin/?"+ wp.toOSMText();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Storage.setWaypointSynced(wp, context);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Borkenbug", "" + error);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}
