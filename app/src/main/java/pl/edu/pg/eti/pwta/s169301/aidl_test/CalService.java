package pl.edu.pg.eti.pwta.s169301.aidl_test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.TimingLogger;

import java.text.DecimalFormat;

/**
 * Created by Khaos on 25.01.2018.
 */

public class CalService extends Service {

    private ICalServiceClient client;
    private int iterator =0;
    private int bestDistance = Integer.MAX_VALUE;


    @Override
    public IBinder onBind(Intent arg0){
        return binder;
    }
    private final ICalService.Stub binder = new ICalService.Stub() {
        @Override
        public void solve(City[] cities, ICalServiceClient serviceClient) throws RemoteException {


            client = serviceClient;
            String dlugosc = Integer.toString(cities.length);

            //Logi

            Log.i("TAG_ilość_miast", dlugosc);

            Log.i("graph", "Start generate");
            City[] shortestPath = generate(cities.length, cities);
            Log.i("graph", "Stop generate");
            Log.i("TAG_odległość", Float.toString(bestDistance));


            // zwracana wartość do clienta
            client.result(shortestPath);
        }
    };

    private float distanceBetweenTwoCities(City a, City b) {
        int axbx = a.getX() - b.getX();
        int ayby = a.getY() - b.getY();
        int pow1 = (int) Math.pow(axbx, 2);
        int pow2 = (int) Math.pow(ayby, 2);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float sqrtResult = (float) Math.sqrt(pow1 + pow2);
        float twoDigitsResult = Float.valueOf(decimalFormat.format(sqrtResult));

        return  twoDigitsResult;
    }

    private int countDistance(City[] graph) {
        int distance = 0;
        for (int i = 0; i < graph.length; i++) {
            distance += distanceBetweenTwoCities(
                    graph[i],
                    i + 1 == graph.length ? graph[0] : graph[i + 1]
            );
        }
        return distance;
    }

    private City[] generate(int n, City[] A) {
        if (n == 1) {
            iterator++;
            int distance = countDistance(A);
            Log.i("TAG_odległość", Float.toString(distance));
            if (distance < bestDistance) {
                bestDistance = distance;
            }
        } else {
            for(int i = 0; i < n - 1; i++) {
                generate(n - 1, A);
                if (n % 2 == 0) {
                    City a = A[i];
                    City b = A[n - 1];
                    A[i] = b;
                    A[n-1] = a;
                } else {
                    City a = A[0];
                    City b = A[n - 1];
                    A[0] = b;
                    A[n-1] = a;
                }
            }
            generate(n - 1, A);
        }

        return A;
    }
}
