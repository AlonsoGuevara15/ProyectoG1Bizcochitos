package pe.edu.pucp.proyectog1bizcochitos.usuarioTI;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.cliente.DevicesClienteActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditDeviceFragment extends Fragment {
    private static final String TAG = "debugeo";


    private static Device device;

    public EditDeviceFragment() {
        // Required empty public constructor
    }

    public static EditDeviceFragment newInstance(Device d) {
        EditDeviceFragment fragment = new EditDeviceFragment();
        device = d;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_device, container, false);


        TextView detailtipo = view.findViewById(R.id.edittipo2);
        EditText editTextMarcaedit = view.findViewById(R.id.editTextMarcaedit);
        EditText editTextIncluyeedit = view.findViewById(R.id.editTextIncluyeedit);
        EditText editTextStockedit = view.findViewById(R.id.editTextStockedit);
        EditText editTextCaractedit = view.findViewById(R.id.editTextCaractedit);
        Button btnfields = view.findViewById(R.id.btnUpdateFields);
        ImageView imagePreview = view.findViewById(R.id.imagePreviewEdit);

        detailtipo.setText(device.getTipo());
        editTextMarcaedit.setText(device.getMarca());
        editTextIncluyeedit.setText(device.getIncluye());
        editTextStockedit.setText(String.valueOf(device.getStock()));
        editTextCaractedit.setText(device.getCaracteristicas());
        String ruta = "Imagenes/" + device.getDeviceId() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(ruta);
        Log.d(TAG,ruta);
        Glide.with(getContext()).load(imageRef).into(imagePreview);

        btnfields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean valid = true;


                String marca = editTextMarcaedit.getText().toString();
                String incluye = editTextIncluyeedit.getText().toString();
                String caract = editTextCaractedit.getText().toString();


                if (marca.trim().equals("")) {
                    editTextMarcaedit.setError("Debe llenar este campo");
                    valid = false;
                }
                if (incluye.trim().equals("")) {
                    editTextIncluyeedit.setError("Debe llenar este campo");
                    valid = false;
                }
                if (caract.trim().equals("")) {
                    editTextCaractedit.setError("Debe llenar este campo");
                    valid = false;
                }


                try {
                    String stockstr = editTextStockedit.getText().toString();
                    int stock = Integer.parseInt(stockstr);
                    if (stock < 0) {
                        editTextStockedit.setError("Este campo debe ser mayor a 0");
                        valid = false;
                    }
                    if (valid) {
                        ((DevicesTiActivity) getActivity()).updateCampos(new Device(marca,caract, incluye,  device.getDeviceId(), stock));
                    }
                } catch (NumberFormatException e) {
                    editTextStockedit.setError("Debe ser número");
                }

            }
        });
        return view;
    }
}