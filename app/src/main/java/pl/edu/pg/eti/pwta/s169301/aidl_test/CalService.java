package pl.edu.pg.eti.pwta.s169301.aidl_test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by Khaos on 25.01.2018.
 */

public class CalService extends Service {

    private ICalServiceClient client;

    @Override
    public IBinder onBind(Intent arg0){
        return binder;
    }
    private final ICalService.Stub binder = new ICalService.Stub() {
        @Override
        public void solve(City[] cities, ICalServiceClient serviceClient) throws RemoteException {


            client = serviceClient;
            String dlugosc = Integer.toString(cities.length);
            Log.i("TAG_odległość", Float.toString(distanceBetweenTwoPoints(cities[0], cities[1])));
            // zwracana wartość do clienta
            client.result(Float.toString(distanceBetweenTwoPoints(cities[0], cities[1])));
        }
    };

    private float distanceBetweenTwoPoints(City a, City b) {
        int axbx = a.getX() - b.getX();
        int ayby = a.getY() - b.getY();
        int pow1 = (int) Math.pow(axbx, 2);
        int pow2 = (int) Math.pow(ayby, 2);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float sqrtResult = (float) Math.sqrt(pow1 + pow2);
        float twoDigitsResult = Float.valueOf(decimalFormat.format(sqrtResult));

        return  twoDigitsResult;
    }
}
