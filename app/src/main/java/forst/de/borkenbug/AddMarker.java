package forst.de.borkenbug;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddMarker extends AppCompatActivity {

    private Location location;
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

        Waypoint waypoint = new Waypoint(location, new WaypointData());
        String fileContents = waypoint.toJSON();

        Spinner spinner = findViewById(R.id.spinnerBug);
        //fileContents += spinner.getPrompt();

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
        HashMap<Integer, Integer> spinnerValues = new HashMap<>();
        spinnerValues.put(R.id.spinnerBug, R.array.bugArray);
        spinnerValues.put(R.id.spinnerTree, R.array.treeArray);

        for(int spinnerID : spinnerValues.keySet()){
            Spinner spinner = (Spinner) findViewById(spinnerID);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    spinnerValues.get(spinnerID), android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }
}
