package forst.de.borkenbug;

import android.location.Location;

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
