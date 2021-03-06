package pe.edu.pucp.proyectog1bizcochitos.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceClientFragment extends Fragment {
    private static final String TAG = "debugeo";



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static Device device;


    public DeviceClientFragment() {
        // Required empty public constructor
    }

    public static DeviceClientFragment newInstance(Device d) {
        DeviceClientFragment fragment = new DeviceClientFragment();
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
        View view = inflater.inflate(R.layout.fragment_device_client, container, false);
        TextView detailtipo = view.findViewById(R.id.nombrerechazado);
        TextView detailMarca = view.findViewById(R.id.detailMarca);
        TextView detailIncludes = view.findViewById(R.id.detailIncludes);
        TextView detailcaract = view.findViewById(R.id.detailcaract);
        TextView detailStock = view.findViewById(R.id.detailStock);
        Button btnReserva = view.findViewById(R.id.btnRechazo);
        ImageView imagePreview = view.findViewById(R.id.imagePreview);


        detailtipo.setText(device.getTipo());
        detailMarca.setText("Marca: " + device.getMarca());
        detailIncludes.setText("Incluye: " + device.getIncluye());
        detailcaract.setText("Caracteristicas: " + device.getCaracteristicas());
        detailStock.setText(device.getStock() + "");

        String ruta = "Imagenes/" + device.getDeviceId() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(ruta);
        Log.d(TAG,ruta);
        Glide.with(getContext()).load(imageRef).into(imagePreview);

        btnReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DevicesClienteActivity) getActivity()).openReservaFragment(device.getDeviceId());
            }
        });


        return view;


    }
}