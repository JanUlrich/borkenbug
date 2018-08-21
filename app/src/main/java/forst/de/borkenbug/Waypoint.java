package forst.de.borkenbug;

import android.location.Location;

import com.google.gson.Gson;

import java.io.Serializable;

public class Waypoint {
    private final Location location;
    private final WaypointData data;

    public Waypoint(Location loc, WaypointData data){
        this.location = loc;
        this.data = data;
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    public Waypoint fromJSON(String json) {
        return new Gson().fromJson(json, this.getClass());
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
