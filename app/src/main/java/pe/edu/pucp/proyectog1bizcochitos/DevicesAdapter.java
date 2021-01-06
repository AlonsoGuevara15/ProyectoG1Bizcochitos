package pe.edu.pucp.proyectog1bizcochitos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.cliente.DevicesClienteActivity;
import pe.edu.pucp.proyectog1bizcochitos.usuarioTI.DevicesTiActivity;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private static ArrayList<Device> lista;
    private static final String TAG = "debugeo";
    private Context contexto;
    private String rol;


    public DevicesAdapter(ArrayList<Device> l, Context contexto, String rol) {
        lista = l;
        this.contexto = contexto;
        this.rol = rol;
    }

    public DevicesAdapter() {
    }

    @NonNull
    @Override
    public DevicesAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        DeviceViewHolder mViewHolder = new DeviceViewHolder(itemView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.DeviceViewHolder holder, int position) {
        holder.tipodevice.setText(lista.get(position).getTipo());
        holder.marcadevice.setText(lista.get(position).getMarca());

        switch (rol) {
            case "Cliente":
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);

                holder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((DevicesClienteActivity)contexto).openDetailsFragment(lista.get(position).getDeviceId());
                    }
                });



                break;
            case "Admin":
                holder.btnDetails.setVisibility(View.GONE);
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((DevicesTiActivity)contexto).openEditFragment(lista.get(position).getDeviceId());
                    }
                });
                break;
        }


    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public TextView tipodevice;
        public TextView marcadevice;
        public ImageButton btnDelete;
        public ImageButton btnEdit;
        public ImageButton btnDetails;


        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tipodevice = itemView.findViewById(R.id.tipodevice);
            this.marcadevice = itemView.findViewById(R.id.marcadevice);
            this.btnDelete = itemView.findViewById(R.id.btnDelete);
            this.btnEdit = itemView.findViewById(R.id.btnEdit);
            this.btnDetails = itemView.findViewById(R.id.btnDetails);



        }
    }

}
