package forst.de.borkenbug;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Data extends ViewModel {
    private MutableLiveData<Integer> satellites = new MutableLiveData<>();
    private MutableLiveData<Location> lastLocation = new MutableLiveData<>();

    public void updateSats(int s){
        satellites.setValue(s);
    }

    public void updateLocation(Location loc){
        lastLocation.setValue(loc);
    }

    public LiveData<Location> getLastLocation(){
        return lastLocation;
    }

    public LiveData<Integer> getNumSatellites(){
        return satellites;
    }
}
