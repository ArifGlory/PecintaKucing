package myproject.pecintakucinglampung.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import myproject.pecintakucinglampung.Kelas.Perawatan;
import myproject.pecintakucinglampung.R;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterPerawatan extends RecyclerView.Adapter<AdapterPerawatan.MyViewHolder> {

    private Context mContext;
    private List<Perawatan> PerawatanList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaPerawat,txtHarga;
        public ImageView ivPerawat;
        public LinearLayout linePerawat;

        public MyViewHolder(View view) {
            super(view);
            tvNamaPerawat = (TextView) view.findViewById(R.id.tvNamaPerawat);
            ivPerawat = view.findViewById(R.id.ivPerawat);
            linePerawat = view.findViewById(R.id.linePerawat);

        }
    }

    public AdapterPerawatan(Context mContext, List<Perawatan> PerawatanList) {
        this.mContext = mContext;
        this.PerawatanList = PerawatanList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_perawatan, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (PerawatanList.isEmpty()){

            Log.d("isiPerawatan: ",""+PerawatanList.size());
        }else {

            Resources res = mContext.getResources();

            final Perawatan Perawatan  = PerawatanList.get(position);





        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return PerawatanList.size();
    }
}
