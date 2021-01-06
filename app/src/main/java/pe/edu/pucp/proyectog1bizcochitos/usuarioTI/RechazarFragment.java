package pe.edu.pucp.proyectog1bizcochitos.usuarioTI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.clases.Usuario;
import pe.edu.pucp.proyectog1bizcochitos.cliente.DevicesClienteActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RechazarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechazarFragment extends Fragment {

    private static Device device;
    private static Solicitud soli;

    public RechazarFragment() {
        // Required empty public constructor
    }


    public static RechazarFragment newInstance(Solicitud s, Device d) {
        RechazarFragment fragment = new RechazarFragment();
        device = d;
        soli = s;
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
        View view = inflater.inflate(R.layout.fragment_rechazar, container, false);

        TextView nombrerechazado = view.findViewById(R.id.nombrerechazado);
        TextView remitenterechazado = view.findViewById(R.id.remitenterechazado);
        EditText editTextJustif = view.findViewById(R.id.editTextJustif);
        Button btnRechazo = view.findViewById(R.id.btnRechazo);

        if (device!=null) {
            nombrerechazado.setText(device.getTipo() + " - " + device.getMarca());
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(soli.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Usuario user = dataSnapshot.getValue(Usuario.class);
                remitenterechazado.setText("De: " + user.getNombre());
            }
        });

        btnRechazo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;

                String justif = editTextJustif.getText().toString();
                if (justif.trim().equals("")) {
                    editTextJustif.setError("Debe llenar este campo");
                    valid = false;
                }

                if (valid) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("estado", "Rechazado");
                    updates.put("justifrechazo", justif);
                    databaseReference.child("requests").child(soli.getSolicId()).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentPedidosTI);
                            if (cFragmentOld != null) {
                                fm.popBackStack();
                                fm.beginTransaction().remove(cFragmentOld).commit();
                            }
                            View view = getActivity().findViewById(R.id.fragmentPedidosTI);
                            view.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Solicitud rechazada exitosamente", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


        return view;
    }
}