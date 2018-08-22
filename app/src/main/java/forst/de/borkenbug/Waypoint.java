package forst.de.borkenbug;

import android.location.Location;

import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.Serializable;

public class Waypoint {
    public final Location location;
    public final WaypointData data;

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

    public OverlayItem toOverlayItem(){
        return new OverlayItem("", "", new GeoPoint(location));
    }
}
