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
import android.widget.CheckedTextView;
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
        data = getSharedPreferences(getString(R.string.app_name) + getString(R.string.export_activity_name), MODE_PRIVATE);
        try {
            sendEmail(null);
        } catch (IOException e) {
            Toast.makeText(this, "IO-Fehler", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Export", Toast.LENGTH_SHORT).show();
        finish();
        /*
        setContentView(R.layout.activity_export);
        ArrayList<String> fileNames = new ArrayList<>();
        File dir = getApplicationContext().getFilesDir();
        List<File> files = getListFiles(dir);
        for(File f : files){
            fileNames.add(f.getName());
        }

        //TODO: Hier eine Funktionierende Liste implementieren
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, fileNames);
        ListView listView = findViewById(R.id.dataList);
        listView.setAdapter(adapter);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        int size = listView.getCount();
        for(int i = 0; i<size; i++){
            listView.setItemChecked(i, true);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // change the checkbox state
            CheckedTextView checkedTextView = ((CheckedTextView)view);
            checkedTextView.setChecked(!checkedTextView.isChecked());
        });

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
        File dir = getApplicationContext().getFilesDir();
        for(File f : Storage.getListFiles(dir)){
            data += Storage.getFileData(f);
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
    /*
    private class ListAdapter extends ArrayAdapter<String> {

        public ListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public ListAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.i, null);
            }

            String p = getItem(position);

            if (p != null) {
                TextView tt1 = (TextView) v.findViewById(R.id.id);
                TextView tt2 = (TextView) v.findViewById(R.id.categoryId);
                TextView tt3 = (TextView) v.findViewById(R.id.description);

                if (tt1 != null) {
                    tt1.setText(p.getId());
                }

                if (tt2 != null) {
                    tt2.setText(p.getCategory().getId());
                }

                if (tt3 != null) {
                    tt3.setText(p.getDescription());
                }
            }

            return v;
        }
    }

*/
}