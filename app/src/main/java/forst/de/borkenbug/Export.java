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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

        //TODO: Hier eine Funktionierende Liste implementieren
        WaypointsArrayAdapter adapter = null;
        try {
            adapter = new WaypointsArrayAdapter(this,
                    android.R.layout.simple_list_item_multiple_choice, Storage.getWaypoints(this));

            ListView listView = findViewById(R.id.dataList);
            listView.setAdapter(adapter);

            /*
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            int size = listView.getCount();
            for(int i = 0; i<size; i++){
                listView.setItemChecked(i, true);
            }
            */

            listView.setOnItemClickListener((parent, view, position, id) -> {
                // change the checkbox state
                CheckedTextView checkedTextView = ((CheckedTextView)view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
            });

        } catch (IOException e) {
            finish();
        }
        /*
        try {
            sendEmail(null);
        } catch (IOException e) {
            Toast.makeText(this, "IO-Fehler", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Export", Toast.LENGTH_SHORT).show();
        finish();
        */
    }

    public void sendEmail(View view) throws IOException {
        /*
        ListView listView = findViewById(R.id.dataList);
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < listView.getCount(); i++) {
            if (checked.get(i)) {
                String filename = listView.getItemAtPosition(i).toString();
                Toast.makeText(this, filename, Toast.LENGTH_SHORT).show(); //DEBUG
            }
        }
        */
        String data = "";
        for(Waypoint wp : Storage.getWaypoints(getApplicationContext())){
            data += wp.toJSON();
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"stadelmeier.andreas@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Testdaten");
        i.putExtra(Intent.EXTRA_TEXT   , data);
        //i.putExtra(Intent.EXTRA_STREAM, path);
        try {
            startActivity(Intent.createChooser(i, "Sende mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Es ist kein Email-Client installiert", Toast.LENGTH_SHORT).show();
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
                convertView = new PackageView(getContext());

            Waypoint pack = getItem(position);
            PackageView packView = (PackageView) convertView;
            packView.setPackage(pack);

            return convertView;
        }

    }

    private class PackageView extends LinearLayout implements Checkable
    {
        private View v;
        private TextView tv0;
        private TextView tv1;
        private TextView tv2;
        private TextView tv3;

        private CheckBox testCheckBox;

        public PackageView(Context context)
        {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.favorites_package, this, true);
            tv0 = (TextView) v.findViewById(R.id.favPackageId);
            tv1 = (TextView) v.findViewById(R.id.favEventDate);
            tv2 = (TextView) v.findViewById(R.id.favEventAddres);
            tv3 = (TextView) v.findViewById(R.id.favEventState);

            // I don't have checkbox in my layout, but if I had:
            // testCheckBox = (CheckBox) v.findViewById(R.id.checkBoxId);
        }

        public void setPackage(Waypoint pack)
        {
            // my custom method where I set package id, date, and time
        ...
        }

        private Boolean checked = false;

        @Override
        public boolean isChecked()
        {
            return checked;
            // if I had checkbox in my layout I could
            // return testCheckBox.checked();
        }

        @Override
        public void setChecked(boolean checked)
        {
            this.checked = checked;

            // since I choose not to have check box in my layout, I change background color
            // according to checked state
            if(isChecked())
            ...
        else
            ...
            // if I had checkbox in my layout I could
            // testCheckBox.setChecked(checked);
        }

        @Override
        public void toggle()
        {
            checked = !checked;
            // if I had checkbox in my layout I could
            // return testCheckBox.toggle();
        }

    }
}