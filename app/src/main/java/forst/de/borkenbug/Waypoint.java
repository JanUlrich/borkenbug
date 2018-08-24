package forst.de.borkenbug;

import android.location.Location;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class Waypoint {
    public final Location location;
    public final WaypointData data;
    public boolean exported = false;
    public boolean synced = false;

    public Waypoint(Location loc, WaypointData data){
        //super("","", new GeoPoint(loc));
        this.location = loc;
        this.data = data;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public static Waypoint fromJSON(String json) {
        //return new Waypoint(new Gson().fromJson(json, Location.class), new WaypointData());
        return new Gson().fromJson(json, Waypoint.class);
    }

    /**
     * See: https://wiki.openstreetmap.org/wiki/Openlayers_POI_layer_example
     * @return
     */
    public String toOSMText(){
        String ret = "" + location.getLatitude();
        ret += "\t" + location.getLongitude();
        ret += "\t" + data.toOSMText();
        return ret;
    }

    public String toGPXPoint(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String ret = "<wpt lat=\""+location.getLatitude()+"\" lon=\""+location.getLongitude()+"\">";
        ret += "<time>" + df.format(location.getTime()) + "</time>";
        ret += "<name>" + data.tree + ", " + data.bug + "</name>";
        ret += "<cmt>Fläche: " + data.size + ", Festmeter: " + data.fm + "</cmt>";
        ret += "</wpt>";
        return ret;
    }
}
