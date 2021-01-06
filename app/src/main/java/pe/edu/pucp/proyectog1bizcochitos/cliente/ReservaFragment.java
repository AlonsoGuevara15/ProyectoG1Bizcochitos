package pe.edu.pucp.proyectog1bizcochitos.cliente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservaFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static Device device;

    public ReservaFragment() {
        // Required empty public constructor
    }


    public static ReservaFragment newInstance(Device d) {
        ReservaFragment fragment = new ReservaFragment();
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
        View view= inflater.inflate(R.layout.fragment_reserva, container, false);


        TextView detailtipo = view.findViewById(R.id.detailtipo2);
        TextView detailMarca = view.findViewById(R.id.detailMarca2);
        EditText editTextDireccion = view.findViewById(R.id.editTextDireccion);
        EditText editTextMotivo = view.findViewById(R.id.editTextMotivo);
        CheckBox detailStock = view.findViewById(R.id.checkBoxCorreo);
        Button btnReserva = view.findViewById(R.id.btnConfirmaReserva);

        detailtipo.setText(device.getTipo());
        detailMarca.setText("Marca: " + device.getMarca());
        btnReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DevicesClienteActivity) getActivity()).saveSolicitud(new Solicitud(editTextMotivo.getText().toString(), editTextDireccion.getText().toString(), device.getDeviceId(), detailStock.isChecked()));

            }
        });


        return view;
    }
}