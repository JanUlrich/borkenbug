package forst.de.borkenbug;

import android.location.Location;

import com.google.gson.Gson;


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
}
