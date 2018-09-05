package forst.de.borkenbug;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        return getWaypoints(context, getWaypointDir(context));
    }
    public static List<Waypoint> getUnsyncedWaypoints(Context context) throws IOException {
        return getWaypoints(context, getWaypointUnsyncedDir(context));
    }
    public static List<Waypoint> getWaypoints(Context context, File dir) throws IOException {
        List<Waypoint> ret = new ArrayList<>();
        for(File f : Storage.getListFiles(dir)){
            //Waypoint wp = Waypoint.fromJSON(getFileData(f));
            Waypoint wp  = getWaypoint(f);
            ret.add(wp);
        }
        Collections.sort(ret,new Comparator<Waypoint>() {
            @Override
            public int compare(Waypoint waypoint, Waypoint t1) {
                return Long.compare(t1.getTime().getTime(), waypoint.getTime().getTime());
            }
        });
        return ret;
    }

    private static Waypoint getWaypoint(File f) throws IOException {
        Gson gson = new Gson();
        Waypoint ret = gson.fromJson(getFileData(f), Waypoint.class);
        if(ret == null)throw new IOException();
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

    private static File getWaypointUnsyncedDir(Context context){
        File folder = new File(context.getFilesDir() +
                File.separator + context.getString(R.string.waypoint_unsynced_directory));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static void setWaypointSynced(Waypoint wp, Context context){
        String filename = getFileName(wp);
        new File(getWaypointUnsyncedDir(context).getAbsolutePath() + File.separator + filename).delete();
        new File(getWaypointDir(context).getAbsolutePath() + File.separator + filename).delete();
        wp.synced = true;
        try {
            saveWaypoint(wp, context);
        } catch (IOException e) {
            //TODO: Man könnte den alten Waypoint nicht direkt löschen sondern erst in einen Backup-Ordner verschieben. Tritt dann hier die Exception auf, wären die Daten des WPs nicht verloren
        }
    }

    public static void setWaypointExported(Waypoint wp, Context context){
        String filename = getFileName(wp);
        new File(getWaypointDir(context).getAbsolutePath() + File.separator + filename).delete();
        wp.exported = true;
        try {
            saveWaypoint(wp, context);
        } catch (IOException e) {
            //TODO: Man könnte den alten Waypoint nicht direkt löschen sondern erst in einen Backup-Ordner verschieben. Tritt dann hier die Exception auf, wären die Daten des WPs nicht verloren
        }
    }

    public static void saveWaypoint(Waypoint wp, Context context) throws IOException {
        String filename = getFileName(wp);
        Gson gson = new Gson();
        FileOutputStream outputStream = new FileOutputStream(
                getWaypointDir(context).getAbsolutePath() + File.separator + filename);
        outputStream.write(gson.toJson(wp).getBytes());
        outputStream.close();
        if(!wp.synced){
            outputStream = new FileOutputStream(
                    getWaypointUnsyncedDir(context).getAbsolutePath() + File.separator + filename);
            outputStream.write(gson.toJson(wp).getBytes());
            outputStream.close();
        }
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

    private static String getFileName(Waypoint wp){
        return "WPHash-" + wp.hashCode();
    }
}
