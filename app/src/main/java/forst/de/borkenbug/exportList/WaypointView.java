package forst.de.borkenbug.exportList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import forst.de.borkenbug.R;
import forst.de.borkenbug.Waypoint;

public class WaypointView extends LinearLayout implements Checkable
{
    private View v;
    private TextView value;

    private CheckBox checkBox;

    public WaypointView(Context context)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.export_list_item, this, true);
        value = v.findViewById(R.id.item_value);
        checkBox = v.findViewById(R.id.checkBoxId);
    }

    public void setWaypoint(Waypoint wp)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY-hh:mm:ss");
        String name = format.format(wp.getTime());
        value.setText(name);
    }

    @Override
    public boolean isChecked()
    {
        return checkBox.isChecked();
    }

    @Override
    public void setChecked(boolean checked)
    {
        checkBox.setChecked(checked);
    }

    @Override
    public void toggle()
    {
        checkBox.toggle();
    }

}