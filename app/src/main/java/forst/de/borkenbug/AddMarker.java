package forst.de.borkenbug;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AddMarker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        Intent intent = getIntent();
        Location location = (Location) intent.getParcelableExtra(MainActivity.EXTRA_POSITION);

        TextView textView = findViewById(R.id.textView);
        textView.setText(location.getAccuracy() + "");

    }
}
