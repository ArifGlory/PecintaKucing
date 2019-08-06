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

import com.bumptech.glide.Glide;

import java.util.List;


import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.R;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterKucing extends RecyclerView.Adapter<AdapterKucing.MyViewHolder> {

    private Context mContext;
    private List<Kucing> kucingList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaKucing,tvUmur,tvRas;
        public ImageView ivKucing;
        public LinearLayout lineKucing;

        public MyViewHolder(View view) {
            super(view);
            tvNamaKucing = (TextView) view.findViewById(R.id.tvNamaKucing);
            tvUmur = (TextView) view.findViewById(R.id.tvUmur);
            tvRas = (TextView) view.findViewById(R.id.tvRas);
            ivKucing = view.findViewById(R.id.ivKucing);
            lineKucing = view.findViewById(R.id.lineKucing);

        }
    }

    public AdapterKucing(Context mContext, List<Kucing> kucingList) {
        this.mContext = mContext;
        this.kucingList = kucingList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mycat, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (kucingList.isEmpty()){

            Log.d("isiKucing: ",""+kucingList.size());
        }else {

            Resources res = mContext.getResources();

            final Kucing kucingku  = kucingList.get(position);
            String ras = "";
            if (kucingku.getRas().equals("no")){
                ras = "Tidak diisi oleh pemilik";
            }else {
                ras = kucingku.getRas();
            }

            holder.tvNamaKucing.setText(kucingku.getNama());
            holder.tvUmur.setText("Umur : "+kucingku.getUmur());
            holder.tvRas.setText("Ras : "+ras);

            if(!kucingku.getUrlGambar().equals("no")){
                Glide.with(mContext)
                        .load(kucingku.getUrlGambar())
                        .into(holder.ivKucing);
            }





        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return kucingList.size();
    }
}
