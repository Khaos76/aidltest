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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pl.edu.pg.eti.pwta.s169301.aidl_test.City;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalService;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalServiceClient;

public class MainActivity extends Activity {

    EditText editName, editVal1, editVal2;
    TextView resultView;
    protected ICalService calService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.multiply_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    calService.solve(new City[] {new City(1, 2)}, resultListener);


                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        editName = findViewById(R.id.name);
        editVal1 = findViewById(R.id.num1);
        editVal2 = findViewById(R.id.num2);
        resultView = findViewById(R.id.result);

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
        public void result(final int r) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultView.setText(Integer.toString(r));
                }
            });

        }
    };

}
