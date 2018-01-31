package pl.edu.pg.eti.pwta.s169301.aidl_client;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.edu.pg.eti.pwta.s169301.aidl_test.City;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalService;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalServiceClient;

public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    City[] cities;
    Date currentTime;
    /*City[] cities = {
            new City(1,12),
            new City (7, 19),
            new City (1,7),
            new City(4,3),
            new City(41,3),
            new City(4,32),
            new City(2,12),
            new City(53,32)
    };*/

    String KOLEJNOSC = "Kolejność odwiedzania miast:";
    String LISTA = "Lista wczytanych miast:";
    private static final String inputFile = "input.txt";
    private static final String outputFileName = "output.txt";


    TextView resultView, cityView;
    protected ICalService calService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        }



        findViewById(R.id.multiply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = Calendar.getInstance().getTime();
                cities = readCitiesFromFile(inputFile);
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
        public void result(final City[] shortestPath, final int f, final int iterration,
                           final double timeM) throws RemoteException {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    writeCities(shortestPath, resultView, KOLEJNOSC);

                    writeToFile("Data: " + currentTime);
                    writeToFile("");
                    writeToFile("Miasta pobrane:");
                    writeCitiesToFile(cities);
                    writeToFile("");
                    writeToFile("Trasa przejścia:");
                    writeCitiesToFile(shortestPath);
                    writeToFile("");
                    writeToFile("Najkrótsza odległość " + Integer.toString(f));
                    writeToFile("Czas obliczeń:  " + Double.toString(timeM)+" sek");
                    writeToFile("Ilość iteracji: " + Integer.toString(iterration));
                    writeToFile("");

                    resultView.append("\nNajkrótsza odległość: " + Integer.toString(f));
                    resultView.append("\nIlość iteracji: " + Integer.toString(iterration));
                    resultView.append("\nCzas trwania: " + Double.toString(timeM)+" sek");
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

    private City[] readCitiesFromFile(String fileName)
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, fileName);

        List<City> cities = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                cities.add(
                        new City(Integer.parseInt(parts[0]),
                                 Integer.parseInt(parts[1]))
                );
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cities.toArray(new City[cities.size()]);
    }

    private void writeToFile(String string) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        externalStorageDirectory = new File(externalStorageDirectory, outputFileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(externalStorageDirectory, true);
        } catch (FileNotFoundException ex) {
            //deal with the problem
        }
        PrintWriter pw = new PrintWriter(fos);
        pw.println(string);
        pw.close();
    }

    private void writeCitiesToFile(City[] cities) {
        for (City city : cities) {
            writeToFile( city.x + " " + city.y);
        }
    }
}


