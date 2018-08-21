package forst.de.borkenbug;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    public static List<Waypoint> getWaypoints(Context context) throws IOException {
        List<Waypoint> ret = new ArrayList<>();
        //TODO: Hier muss ein eigenes Directory für die Wegpunkte her:
        for(File f : Storage.getListFiles(context.getFilesDir())){
            //ret.add(Waypoint.fromJSON(getFileData(f)));
        }
        return ret;
    }

    public static List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    public static String getFileData(String name){
        return "";
    }

    public static String getFileData(File file) throws IOException {
        StringBuilder text = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();

        return text.toString();
    }
}
