package pl.edu.pg.eti.pwta.s169301.aidl_client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import pl.edu.pg.eti.pwta.s169301.aidl_test.City;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalService;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalServiceClient;

public class MainActivity extends Activity {

    City[] cities = {
            new City(1,12),
            new City (7, 19),
            new City (1,7),
            new City(4,3),
            new City(41,3),
            new City(4,32),
            new City(2,12),
            new City(53,32)
    };

    String KOLEJNOSC = "Kolejność odwiedzania miast:";
    String LISTA = "Lista początkowa miast:";


    TextView resultView, cityView;
    protected ICalService calService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.multiply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.setText("");
                cityView.setText("");
                writeCities(cities, cityView, LISTA);

                try {
                    calService.solve(cities, resultListener);


                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        resultView = findViewById(R.id.result);
        cityView = findViewById(R.id.cities);

        if (calService == null) {
            Intent it = new Intent();
            ComponentName componentName = new ComponentName("pl.edu.pg.eti.pwta.s169301.aidl_test",
                    "pl.edu.pg.eti.pwta.s169301.aidl_test.CalService");
            it.setComponent(componentName);
            bindService(it, connection, Context.BIND_AUTO_CREATE);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            calService = ICalService.Stub.asInterface(service);

            Toast.makeText(getApplicationContext(),"Service Connected", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            calService = null;
            Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private final ICalServiceClient.Stub  resultListener = new ICalServiceClient.Stub() {
        @Override
        //public void result(final int r) throws RemoteException {
        //public void result(final String s) throws RemoteException {
        public void result(final City[] shortestPath) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    writeCities(shortestPath, resultView, KOLEJNOSC);

                }
            });

        }
    };

    private void writeCities(City[] cities, TextView view, String s){
        StringBuilder builder = new StringBuilder();
        builder.append(s + " \n");
        builder.append("nr   x   y\n");
        for (int i = 0; i<cities.length; i++){
            builder.append(i+1+". " + cities[i].x + " " + cities[i].y +"\n");
        }
        view.setText(builder.toString());
    }

}
