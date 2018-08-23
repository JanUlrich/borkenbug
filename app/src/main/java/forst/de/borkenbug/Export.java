package forst.de.borkenbug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
TODO: Der Export sollte automatisch ablaufen und alles auf einer Website speichern
Warten auf WLAN: https://stackoverflow.com/questions/8678362/wait-until-wifi-connected-on-android
 */
public class Export extends AppCompatActivity {

    SharedPreferences data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //data = getSharedPreferences(getString(R.string.app_name) + getString(R.string.export_activity_name), MODE_PRIVATE);
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

    public void sendEmail(View view) throws IOException {
        List<Waypoint> exports = new ArrayList<>();

        ListView listView = findViewById(R.id.dataList);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++) {
            if (checked.get(i)) {
                Waypoint wp = (Waypoint) listView.getAdapter().getItem(i);
                if(wp != null)exports.add(wp);
            }
        }
        if(exports.size()==0)return;

        String data = "";
        for(Waypoint wp : exports){
            data += wp.toJSON();
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"stadelmeier.andreas@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Testdaten");
        i.putExtra(Intent.EXTRA_TEXT   , data);
        //i.putExtra(Intent.EXTRA_STREAM, path);
        try {
            //startActivityForResult(Intent.createChooser(i, "Sende mail..."), MAIL_INTENT);
            startActivity(Intent.createChooser(i, "Sende mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "FEHLER: Es ist kein Email-Client installiert", Toast.LENGTH_LONG).show();
        }
    }
    private static final int MAIL_INTENT = 312315;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MAIL_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Daten exportiert", Toast.LENGTH_SHORT).show();
                finish();
            }else{

            }
        }
    }

    private class WaypointsArrayAdapter extends ArrayAdapter<Waypoint>
    {

        public WaypointsArrayAdapter(Context context, int resource, List<Waypoint> objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
                convertView = new WaypointView(getContext());

            Waypoint pack = getItem(position);
            WaypointView packView = (WaypointView) convertView;
            packView.setWaypoint(pack);

            return convertView;
        }

    }

    private class WaypointView extends LinearLayout implements Checkable
    {
        private View v;
        private TextView value;

        private CheckBox checkBox;

        public WaypointView(Context context)
        {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.export_list_item, this, true);
            value = v.findViewById(R.id.item_value);
            checkBox = v.findViewById(R.id.checkBoxId);
        }

        public void setWaypoint(Waypoint wp)
        {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
            String name = format.format(wp.location.getTime());
            value.setText(name);
        }

        @Override
        public boolean isChecked()
        {
            return checkBox.isChecked();
        }

        @Override
        public void setChecked(boolean checked)
        {
            checkBox.setChecked(checked);
        }

        @Override
        public void toggle()
        {
            checkBox.toggle();
        }

    }
}