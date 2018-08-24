package forst.de.borkenbug;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddMarker extends AppCompatActivity {

    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        setupLayout();
        Intent intent = getIntent();
        location = (Location) intent.getParcelableExtra(getString(R.string.extra_position));
    }

    public void addMarker(View view){
        Context context = getApplicationContext();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String timestamp = format.format(Calendar.getInstance().getTime());

        Spinner spinnerTree = findViewById(R.id.spinnerTree);
        String tree = spinnerTree.getItemAtPosition(spinnerTree.getSelectedItemPosition()).toString();
        Spinner spinnerBug = findViewById(R.id.spinnerBug);
        String bug = spinnerBug.getItemAtPosition(spinnerBug.getSelectedItemPosition()).toString();
        EditText fmText = findViewById(R.id.festmeter);
        EditText flächeText = findViewById(R.id.fläche);
        int fm = 0;
        int fläche = 0;
        try{
        fm = Integer.parseInt(fmText.getText().toString());
        }catch (Exception e){
            //Dann bleibt Festmeter halt 0
        }
        try{
            fläche = Integer.parseInt(flächeText.getText().toString());
        }catch (Exception e){
            //Dann bleibt Fläche halt 0
        }

        final Waypoint waypoint = new Waypoint(location, Calendar.getInstance().getTime(), new WaypointData(tree, bug, fm, fläche));
        //Sync Waypoint:
        WaypointSync.syncWaypoint(waypoint, this);
        //Save Waypoint:
        try {
            //TODO: Hier in einem extra Ordner Waypoints speichern (am besten das in Storage implementieren)
            Storage.saveWaypoint(waypoint, getApplicationContext());

            CharSequence text = "Erfolgreich gespeichert\n" + timestamp;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLayout() {
        Resources res = getResources();
        String[] trees = res.getStringArray(R.array.trees);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTree);
        spinner.setOnItemSelectedListener(new OnTreeSelected());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        trees);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        //Spinner auf Tanne setzen: (erstes Element im String array)
        spinner.setSelection(spinnerArrayAdapter.getPosition(getResources().getStringArray(R.array.trees)[0]));
    }

    private class OnTreeSelected implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String item = parent.getItemAtPosition(pos).toString();
            Resources res = getResources();
            int treeNum = Arrays.asList(res.getStringArray(R.array.trees)).indexOf(item);
            int treeBugsResId = res.obtainTypedArray(R.array.tree_bugs).getResourceId(treeNum, -1);
            String[] sliderValues = res.getStringArray(treeBugsResId);
            Spinner spinner = (Spinner) findViewById(R.id.spinnerBug);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (getApplicationContext(), android.R.layout.simple_spinner_item,
                            sliderValues);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
