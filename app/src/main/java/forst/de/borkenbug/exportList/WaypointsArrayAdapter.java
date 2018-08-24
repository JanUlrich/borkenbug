package forst.de.borkenbug.exportList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import forst.de.borkenbug.Waypoint;

public class WaypointsArrayAdapter extends ArrayAdapter<Waypoint>
{

    public WaypointsArrayAdapter(Context context, int resource, List<Waypoint> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
            convertView = new WaypointView(getContext());

        Waypoint pack = getItem(position);
        WaypointView packView = (WaypointView) convertView;
        packView.setWaypoint(pack);

        return convertView;
    }

}