package forst.de.borkenbug;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "de.forst.borkenbug.MESSAGE";
    public static final String EXTRA_POSITION = "de.forst.borkenbug.POSITION";

    Thread updateThread = new Thread() {
        @Override
        public void run() {
            try {
                while (!updateThread.isInterrupted()) {
                    Thread.sleep(500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLastGPSLocationUpdate(model.getLastGPSFix().getValue());
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };

    private Data model = null;

    public void setMarker(View view){
        Intent intent = new Intent(this, AddMarker.class);
        Location position = model.getLastLocation().getValue();
        if(position != null){
            intent.putExtra(EXTRA_POSITION, position);
            startActivity(intent);
        }
        //startActivity(intent); //Debug
    }

    public void export(View view){
        Intent intent = new Intent(this, Export.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addMarkerButton = findViewById(R.id.addMarker);
        addMarkerButton.setEnabled(false);
        addMarkerButton.setText("Warte auf GPS ...");

        model = ViewModelProviders.of(this).get(Data.class);
        model.getLastLocation().observe( this, this::updateGPSLocation);
        model.getNumSatellites().observe(this, this::updateGPSStatus);

        initialiseGPS();
        updateThread.start();

    }

    private void initialiseGPS() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener(model);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 50, locationListener);
        locationManager.addGpsStatusListener(locationListener);

        Location location = locationManager.getLastKnownLocation("Borkenbug");
        if(location != null){
            locationListener.onLocationChanged(location);
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS ist nicht aktiviert")
                .setCancelable(false)
                .setPositiveButton("Aktivieren", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialiseGPS();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent intent = new Intent(this, GPSError.class);
                    intent.putExtra(EXTRA_MESSAGE, "Keine Berechtigung für GPS");
                    startActivity(intent);
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void updateLastGPSLocationUpdate(Date lastUpdate){
        if(lastUpdate == null)return;
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        long difference = Calendar.getInstance().getTime().getTime() - lastUpdate.getTime();
        String s = "Letzte Aktualisierung: " + format.format(difference);

        TextView editLocation = findViewById(R.id.gpsLastUpdate);
        editLocation.setText(s);
    }

    private void updateGPSLocation(Location location){
        TextView editLocation = findViewById(R.id.textView);
        editLocation.setText("");
        String longitude = "Longitude: " + location.getLongitude();
        String latitude = "Latitude: " + location.getLatitude();

        String s = longitude + "\n" + latitude;
        s += "\nGenauigkeit: " + location.getAccuracy() + " m";

        editLocation.setText(s);

        Button addMarkerButton = findViewById(R.id.addMarker);
        addMarkerButton.setEnabled(true);
        addMarkerButton.setText("Markierung erstellen");
    }

    private void updateGPSStatus(Integer numSatellites){
        TextView status = findViewById(R.id.gpsStatus);
        if(numSatellites < 4){
            String s = "Warte auf GPS Satelliten: " + numSatellites + "/4";
            status.setText(s);
        }else{
            status.setText("");
        }

    }

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener, android.location.GpsStatus.Listener {

        private Data data;

        public MyLocationListener(Data data){
            this.data = data;
        }

        @Override
        public void onLocationChanged(Location loc) {
            data.updateLocation(loc);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            TextView editLocation = findViewById(R.id.gpsStatus);
            //editLocation.setText(s);
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onGpsStatusChanged(int event) {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            int satellitesNotInFix = 0;
            int satellites = 0;
            for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
                if(sat.usedInFix()) {
                    satellites++;
                }
                satellitesNotInFix++;
            }
            data.updateSats(satellites);
        }
    }

}