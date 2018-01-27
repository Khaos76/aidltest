package pl.edu.pg.eti.pwta.s169301.aidl_test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

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
            client.result(cities.length);
        }
    };

}
