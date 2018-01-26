package pl.edu.pg.eti.pwta.s169301.aidl_client;

import android.app.Activity;
import android.app.Service;
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

public class MainActivity extends Activity {

    EditText editName, editVal1, editVal2;
    TextView resultView;
    protected ICalService calService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        editName = findViewById(R.id.name);
        editVal1 = findViewById(R.id.num1);
        editVal2 = findViewById(R.id.num2);
        resultView = findViewById(R.id.result);
        if (calService == null) {
            Intent it = new Intent("ms");
            bindService(it, connection, Context.BIND_AUTO_CREATE);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    public void multiply(View v) {
        switch (v.getId()){
            case R.id.multiply_btn: {
                int num1 = Integer.parseInt(editVal1.getText().toString());
                int num2 = Integer.parseInt(editVal2.getText().toString());
                try {
                    int result = calService.getResult(num1, num2);
                    String msg = calService.getMessage(editName.getText().toString());
                    resultView.setText(msg + result);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
                break;
            }
        }
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
}
