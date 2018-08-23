package forst.de.borkenbug;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddMarker extends AppCompatActivity {

    private Location location;
    private static final HashMap<String, List<String>> sliderValues = new HashMap<>();
    static{
        List<String> fichte = new ArrayList<>();
        fichte.add("Buckdrucker");
        fichte.add("Kupferstecher");
        fichte.add("Trockenheit");
        fichte.add("Sturm");
        sliderValues.put("Fichte", fichte);
        List<String> tanne = new ArrayList<>();
        tanne.add("Trockenheit");
        tanne.add("Mistel");
        tanne.add("Borkenkäfer");
        sliderValues.put("Tanne", tanne);
        List<String> buche = new ArrayList<>();
        buche.add("Trockenheit");
        sliderValues.put("Buche", buche);
        List<String> eiche = new ArrayList<>();
        eiche.add("Eichenprozessionsspinner");
        sliderValues.put("Eiche", eiche);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        setupLayout();
        Intent intent = getIntent();
        location = (Location) intent.getParcelableExtra(MainActivity.EXTRA_POSITION);

        //TextView textView = findViewById(R.id.);
        //textView.setText(location.getAccuracy() + "");
    }

    public void addMarker(View view){
        Context context = getApplicationContext();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String filename = format.format(Calendar.getInstance().getTime());

        Spinner spinnerTree = findViewById(R.id.spinnerTree);
        String tree = spinnerTree.getItemAtPosition(spinnerTree.getSelectedItemPosition()).toString();
        Spinner spinnerBug = findViewById(R.id.spinnerTree);
        String bug = spinnerBug.getItemAtPosition(spinnerBug.getSelectedItemPosition()).toString();
        EditText fmText = findViewById(R.id.festmeter);
        int fm = 0;
        int fläche = 0;
        try{
            fm = Integer.parseInt(fmText.getText().toString());
            EditText flächeText = findViewById(R.id.fläche);
            fläche = Integer.parseInt(flächeText.getText().toString());
        }catch (Exception e){
            //Dann bleibt Fläche und Festmeter halt 0
        }

        final Waypoint waypoint = new Waypoint(location, new WaypointData(tree, bug, fm, fläche));
        //Sync Waypoint:
        WaypointSync.syncWaypoint(waypoint, this);
        //Save Waypoint:
        try {
            //TODO: Hier in einem extra Ordner Waypoints speichern (am besten das in Storage implementieren)
            Storage.saveWaypoint(waypoint, getApplicationContext());

            CharSequence text = "Erfolgreich gespeichert\n" + filename;
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLayout() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerTree);
        spinner.setOnItemSelectedListener(new OnTreeSelected());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        sliderValues.keySet().toArray(new String[0]));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private class OnTreeSelected implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            Spinner spinner = (Spinner) findViewById(R.id.spinnerBug);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                    (getApplicationContext(), android.R.layout.simple_spinner_item,
                            sliderValues.get(parent.getItemAtPosition(pos).toString()));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                    .simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
