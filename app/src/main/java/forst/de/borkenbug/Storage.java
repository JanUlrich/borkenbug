package forst.de.borkenbug;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.security.AccessController.getContext;

public class Storage {

    public static Uri generateExportFile(String data, Context context) throws IOException {
        File path = new File(context.getFilesDir(), "export");
        if(!path.exists())path.mkdirs();
        File newFile = new File(path, "export.gpx");
        if(newFile.exists()){
            newFile.delete();
            newFile = new File(path, "export.gpx");
        }
        Uri contentUri = FileProvider.getUriForFile(context, context.getString(R.string.fileprovider), newFile);
        //File file = File.createTempFile("export.gpx", null, context.getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(newFile);
        outputStream.write(data.getBytes());
        outputStream.close();
        return contentUri;
    }

    public static List<Waypoint> getWaypoints(Context context) throws IOException {
        Gson gson = new Gson();
        List<Waypoint> ret = new ArrayList<>();
        for(File f : Storage.getListFiles(getWaypointDir(context))){
            //Waypoint wp = Waypoint.fromJSON(getFileData(f));
            Waypoint wp  = gson.fromJson(getFileData(f), Waypoint.class);
            if(wp != null)ret.add(wp);
        }
        Collections.sort(ret,new Comparator<Waypoint>() {
            @Override
            public int compare(Waypoint waypoint, Waypoint t1) {
                return Long.compare(t1.getTime().getTime(), waypoint.getTime().getTime());
            }
        });
        return ret;
    }

    private static File getWaypointDir(Context context){
        File folder = new File(context.getFilesDir() +
                File.separator + context.getString(R.string.waypoint_directory));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static void setWaypointSynced(Waypoint wp, Context context){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String filename = format.format(wp.getTime());
        //Hier könnten die Waypoints gelöscht werden oder in einen anderen Ordner verschoben.
        //Ist momentan nicht nötig, da nicht auf den Network State Change gewartet wird sondern direkt gesendet
        //new File(getWaypointDir(context).getAbsolutePath() + File.separator + filename).delete();
    }

    public static void saveWaypoint(Waypoint wp, Context context) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String filename = format.format(wp.getTime());
        Gson gson = new Gson();
        FileOutputStream outputStream = new FileOutputStream(
                getWaypointDir(context).getAbsolutePath() + File.separator + filename);
        outputStream.write(gson.toJson(wp).getBytes());
        outputStream.close();
    }

    private static List<File> getListFiles(File parentDir) {
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

    private static String getFileData(File file) throws IOException {
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
