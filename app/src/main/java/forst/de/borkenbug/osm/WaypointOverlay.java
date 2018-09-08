package forst.de.borkenbug.osm;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import forst.de.borkenbug.Waypoint;

public class WaypointOverlay extends OverlayItem {
    public WaypointOverlay(Waypoint wp){
        super(wp.data.tree, wp.data.bug, new GeoPoint(wp.getLatitude(), wp.getLongitude()));
    }
}
