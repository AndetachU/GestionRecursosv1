package co.edu.uniminuto.gestionrecursos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 25;
    // 1. Declaración de los objetos de la interface que se usaran en la parte lógica
    private Button btnCheckPermissions;
    private Button btnRequestPermissions;
    private Button btnRequestPermissionContact;
    private Button btnRequestPermissionCall;
    private Button btnRequestPermissionRS;
    private Button btnRequestPermissionES;
    private TextView tvCamera;
    private TextView tvBiometric;
    private TextView tvExternalWS;
    private TextView tvReadexternalS;
    private TextView tvInternet;
    private TextView tvResponse;
    private TextView tvLlamadas;
    private TextView tvContactos;
    //1.1 Objetos para recursos.
    private TextView versionAndroid;
    private int versionSDK;
    private ProgressBar pbLevelBat;
    private TextView tvLevelBat;
    private TextView tvConection;
    IntentFilter batFilter;
    CameraManager cameraManager;
    String cameraID;
    private Button btnOn;
    private Button btnOff;
    ConnectivityManager conexion;
    private EditText etArchivo;
    private Button btnSaveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //3. Llamado del método de enlace de objetos
        initObject();
        //4. Enlace de botones a los métodos
        btnCheckPermissions.setOnClickListener(this::voidCheckPermissions);
        btnRequestPermissions.setOnClickListener(this::voidRequestPermissions);
        btnRequestPermissionCall.setOnClickListener(this::voidRequestPermissionsCall);
        btnRequestPermissionContact.setOnClickListener(this::voidRequestPermissionsContact);
        btnRequestPermissionRS.setOnClickListener(this::voidRequestPermissionsRS);
        btnRequestPermissionES.setOnClickListener(this::voidRequestPermissionsES);
        btnOn.setOnClickListener(this::voidOnToggleFlashLight);
        btnOff.setOnClickListener(this::voidOffToggleFlashLight);
        batFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,batFilter);
        btnSaveFile.setOnClickListener(this::SaveFile);
        networkAvailable();
    }



    //2. Enlace de objetos
    private void initObject() {
        btnCheckPermissions = findViewById(R.id.btnCheckPermission);
        btnRequestPermissions = findViewById(R.id.btnRequestPermission);
        btnRequestPermissions.setActivated(false);
        btnRequestPermissionContact = findViewById(R.id.btnRequestPermissionContact);
        btnRequestPermissionCall = findViewById(R.id.btnRequestPermissionCall);
        btnRequestPermissionES = findViewById(R.id.btnRequestPermissionES);
        btnRequestPermissionRS = findViewById(R.id.btnRequestPermissionRS);
        tvCamera = findViewById(R.id.tvCamera);
        tvBiometric = findViewById(R.id.tvDactilar);
        tvExternalWS = findViewById(R.id.tvEws);
        tvReadexternalS = findViewById(R.id.tvRS);
        tvInternet = findViewById(R.id.tvInternet);
        tvResponse = findViewById(R.id.tvResponse);

        versionAndroid = findViewById((R.id.tvVersionAndroid));
        pbLevelBat = findViewById(R.id.pbLevelBaterry);
        tvLevelBat = findViewById(R.id.tvLevelBaterry);
        tvConection = findViewById(R.id.tvConexion);
        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);
        etArchivo = findViewById(R.id.etArchivo);
        btnSaveFile = findViewById(R.id.btnSaveFile);


    }

    //8.Implementacion del Onresume para la versión de Android.
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Version SO:" + versionSO + " / SDK: " + versionSDK);
    }

    //5. Verificación de Permisos
    private void voidCheckPermissions(View view) {
        //Si hay permiso --> 0 si no --> -1
        int statusCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        tvCamera.setText("Status Camera: " + statusCamera);
        int statusBiometric = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC);
        tvBiometric.setText("Status Biometrics: " + statusBiometric);
        int statusExternalWS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        tvExternalWS.setText("Status WES: " + statusExternalWS);
        int statusReadExternalS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        tvReadexternalS.setText("Status RS: " + statusReadExternalS);
        int statusInternet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        tvInternet.setText("Status Internet: " + statusInternet);
        int statusLlamada = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        tvLlamadas.setText("Status Internet: " + statusLlamada);
        int statusContactos = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        tvContactos.setText("Status Contactos: " + statusContactos);
        btnRequestPermissions.setEnabled(true);

    }

    //6. Solicitud de Permiso de camara
    private void voidRequestPermissions(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }
    }

    //9. Solicitud de Permiso de contacto
    private void voidRequestPermissionsContact(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
        }
    }

    //10. Solicitud de Permiso de Llamadas
    private void voidRequestPermissionsCall(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
        }
    }
    //11. solicitud de Permiso de ES
    private void voidRequestPermissionsES(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }
    //12. Solicitud de Permiso de RS
    private void voidRequestPermissionsRS(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    //7. Gestion de respuestas del usuario respecto a la solicitud del permiso.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tvResponse.setText(" " + grantResults[0]);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Box Permission")
                        .setMessage("You denied the permission")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
            } else {
                Toast.makeText(this, "This permission is required for the application.", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(this, "This permission is required for the application.", Toast.LENGTH_SHORT).show();

        }

    }

    //13. Metodo para encender y apagar linterna
    private void voidOnToggleFlashLight(View view) {

        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraID = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraID, true);
        } catch (CameraAccessException e) {
            Toast.makeText(this,"No se puede encender la linterna",Toast.LENGTH_SHORT).show();
            Log.i("FLASH",e.getMessage());

        }
    }

    private void voidOffToggleFlashLight(View view) {
        try {
            cameraManager.setTorchMode(cameraID, false);
        } catch (CameraAccessException e) {
            Toast.makeText(this,"No se puede encender la linterna",Toast.LENGTH_SHORT).show();
            Log.i("FLASH",e.getMessage());

        }
    }
// 14.Bateria
    BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        pbLevelBat.setProgress(levelBattery);
        tvLevelBat.setText("Level Battery: " + levelBattery + " %");
    }

};
    //15. guardar archivo
    private void SaveFile(View view) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, String.valueOf(etArchivo));
        try{
            FileOutputStream fos = new FileOutputStream(file + ".txt");
            fos.write(("La versión de Android es: " + versionAndroid + tvLevelBat).getBytes());
            fos.close();
        }catch (IOException e){
            Log.i("FILE",e.getMessage());
        }

    }
//16. mostrar conexión a internet

    private void networkAvailable() {
        conexion = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = conexion.getActiveNetwork();
        NetworkCapabilities actNw = conexion.getNetworkCapabilities(nw);
        tvConection.setText("Conection is: " + actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
    }

}

