package pe.edu.pucp.proyectog1bizcochitos.usuarioTI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;

public class NewDeviceActivity extends AppCompatActivity {

    FloatingActionButton btnSto, btnIm, btnCam;

    EditText idTipoEsp;
    EditText idMarca;
    EditText idCarac;
    EditText idIncluye;
    EditText idStock;
    ImageView selectedImage = null;
    Boolean isAllFabsVisible;

    private static final int IMAGE_UPLOAD = 1;
    private static final int IMAGE_PERMISSION = 3;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    DatabaseReference deviceIdRef;
    StorageTask uploadTask = null;
    public static final int CAMERA_PERMISSION = 5;
    public static final int CAMERA_UPLOAD = 7;

    Uri imgURL;
    Bitmap bitmap;
    Boolean isCamera;
    Device device;
    Spinner spinner;
    String tipo;
    String keyDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        device = new Device();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnSto = findViewById(R.id.add_storage);
        btnIm = findViewById(R.id.add_image);
        btnCam = findViewById(R.id.add_camera);
        selectedImage = findViewById(R.id.selectedImage);

        idTipoEsp = findViewById(R.id.idTipoEsp);
        idMarca = findViewById(R.id.idMarca);
        idCarac = findViewById(R.id.idCarac);
        idIncluye = findViewById(R.id.idIncluye);
        idStock = findViewById(R.id.idStock);


        String[] lista = {"Laptop", "Tableta", "Celular", "Monitor", "Otro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lista);
        spinner = findViewById(R.id.idSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = lista[position];

                if (item.equals("Otro")) {
                    tipo = idTipoEsp.getText().toString();
                    Log.d("infoApp",tipo);
                } else {
                    tipo = item;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NewDeviceActivity.this, "Seleccione un tipo de dispositivo", Toast.LENGTH_SHORT).show();
            }
        });

        setFloatingButton();

    }

    public void setFloatingButton() {

        btnIm.setVisibility(View.GONE);
        btnCam.setVisibility(View.GONE);


        isAllFabsVisible = false;

        btnSto.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            btnIm.show();
                            btnCam.show();
                            isAllFabsVisible = true;
                        } else {
                            btnIm.hide();
                            btnCam.hide();
                            isAllFabsVisible = false;
                        }
                    }
                });

        btnIm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage();
                        isCamera = false;
                    }
                });
        btnCam.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePicture();
                        isCamera = true;
                    }
                });


    }

    public void uploadImageToFirebase(View view) {

        if (TextUtils.isEmpty(idMarca.getText().toString())) {
            idMarca.setError("Ingrese un nombre de producto");
            return;
        } else if (TextUtils.isEmpty(idCarac.getText().toString())) {
            idCarac.setError("Ingrese una categoria");
            return;
        } else if (TextUtils.isEmpty(idIncluye.getText().toString())) {
            idIncluye.setError("Ingrese una descripcion");
            return;
        } else if (TextUtils.isEmpty(idStock.getText().toString())) {
            idStock.setError("Ingrese un precio");
            return;
        } else {
            try{
                Integer.parseInt(idStock.getText().toString());
            }catch (NumberFormatException e){
                e.getMessage();
            }
        }

        device.setMarca(idMarca.getText().toString());
        device.setCaracteristicas(idCarac.getText().toString());
        device.setIncluye(idIncluye.getText().toString());
        device.setStock(Integer.parseInt(idStock.getText().toString()));

        deviceIdRef = databaseReference.child("devices").push();
        keyDev = deviceIdRef.getKey();
        device.setDeviceId(keyDev);
        device.setTipo(tipo);

        deviceIdRef.setValue(device)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("infoApp", "info guardada exitosamente");
                        uploadImage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("infoApp", "Error al guardar");
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (uploadTask != null) {
            uploadTask.cancel();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (uploadTask != null && uploadTask.isInProgress()) {
            uploadTask.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uploadTask != null && uploadTask.isPaused()) {
            uploadTask.resume();
        }
    }


    public void uploadImage() {

        if (!isCamera) {

                if (imgURL != null) {
                    uploadTask = storageReference.child("Imagenes").child(keyDev + ".jpg")
                            .putFile(imgURL)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(NewDeviceActivity.this, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show();
                                    Log.d("infoApp", "subida exitosa");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("infoApp", "Error en la subida");
                                    e.printStackTrace();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    long bytesTransferred = snapshot.getBytesTransferred();
                                    long totalByteCount = snapshot.getTotalByteCount();
                                    double progreso = (int) Math.round((100.0 * bytesTransferred) / totalByteCount);
                                    Log.d("infoApp", "progreso: " + progreso + "%");
                                }
                            });
                }
            } else {

                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    uploadTask = storageReference.child("Imagenes").child(keyDev  + ".jpg")
                            .putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(NewDeviceActivity.this, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(NewDeviceActivity.this, DevicesTiActivity.class);
                                    startActivity(intent);
                                    Log.d("infoApp", "subida exitosa");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("infoApp", "Error en la subida");
                                    e.printStackTrace();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    long bytesTransferred = snapshot.getBytesTransferred();
                                    long totalByteCount = snapshot.getTotalByteCount();
                                    double progreso = (int) Math.round((100.0 * bytesTransferred) / totalByteCount);
                                    Log.d("infoApp", "progreso: " + progreso + "%");
                                }
                            });

                }
            }
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_UPLOAD && data != null && data.getData() != null) {
                Log.d("infoApp", "Foto seleccionada");
                imgURL = data.getData();
                Picasso.with(this).load(imgURL).into(selectedImage);
            } else if (requestCode == CAMERA_UPLOAD && data != null) {
                Log.d("infoApp", "Foto tomada");
                bitmap = (Bitmap) data.getExtras().get("data");
                selectedImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == IMAGE_PERMISSION) {
                selectImage();
            }
            if (requestCode == CAMERA_PERMISSION) {
                takePicture();
            }
        }
    }

    public void selectImage() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            Log.d("infoApp", "imagen abierta");
            startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), IMAGE_UPLOAD);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_PERMISSION);
        }
    }

    public void takePicture() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_UPLOAD);
                Log.d("infoApp", "camara abierta");
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }


}