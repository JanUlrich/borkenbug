package forst.de.borkenbug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import forst.de.borkenbug.exportList.WaypointView;
import forst.de.borkenbug.exportList.WaypointsArrayAdapter;

/*
TODO: Der Export sollte automatisch ablaufen und alles auf einer Website speichern
Warten auf WLAN: https://stackoverflow.com/questions/8678362/wait-until-wifi-connected-on-android
 */
public class Export extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        WaypointsArrayAdapter adapter = null;
        try {
            adapter = new WaypointsArrayAdapter(this,
                    -1, Storage.getWaypoints(this));

            ListView listView = findViewById(R.id.dataList);
            listView.setAdapter(adapter);
            //Alle nicht exportierten sind standardmäßig angeklickt:
            for (int i = 0; i < listView.getCount(); i++) {
                Waypoint wp = (Waypoint) listView.getAdapter().getItem(i);
                listView.setItemChecked(i, !wp.exported);
            }
        } catch (IOException e) {
            finish();
        }
    }

    private Uri generateGPXFile(List<Waypoint> fromWPs) throws IOException {
        String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
                "<gpx version=\"1.1\" creator=\"Borkenbug-App\">";
        for(Waypoint wp : fromWPs){
            ret += "\n" + wp.toGPXPoint();
        }
        ret += "</gpx>";
        return Storage.generateExportFile(ret, this.getApplicationContext());
    }

    List<Waypoint> exports;
    public void sendEmail(View view) throws IOException {
        exports = new ArrayList<>();
        ListView listView = findViewById(R.id.dataList);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++) {
            if (checked.get(i)) {
                Waypoint wp = (Waypoint) listView.getAdapter().getItem(i);
                if(wp != null)exports.add(wp);
            }
        }
        if(exports.size()==0)return;

        Uri exportUri = generateGPXFile(exports);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        //i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"stadelmeier.andreas@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "GPX Export");
        i.putExtra(Intent.EXTRA_TEXT   , "");
        i.putExtra(Intent.EXTRA_STREAM, exportUri);
        try {
            //startActivityForResult(Intent.createChooser(i, "Sende mail..."), getResources().getInteger(R.integer.mail_intent_flag));
            startActivityForResult(i, getResources().getInteger(R.integer.mail_intent_flag));
            //startActivity(Intent.createChooser(i, "Sende mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "FEHLER: Es ist kein Email-Client installiert", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources().getInteger(R.integer.mail_intent_flag)) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Daten exportiert", Toast.LENGTH_SHORT).show();
                for(Waypoint wp : exports){
                    Storage.setWaypointExported(wp, this);
                }
                finish();
            }else{
                Toast.makeText(this, "Export abgebrochen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}