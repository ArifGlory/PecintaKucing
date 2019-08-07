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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import myproject.pecintakucinglampung.Kelas.Perawatan;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.DetailPerawatActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterPerawatan extends RecyclerView.Adapter<AdapterPerawatan.MyViewHolder> {

    private Context mContext;
    private List<Perawatan> perawatanList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaPerawat,txtHarga;
        public ImageView ivPerawat;
        public LinearLayout linePerawat;

        public MyViewHolder(View view) {
            super(view);
            tvNamaPerawat = (TextView) view.findViewById(R.id.tvNamaPerawat);
            txtHarga = (TextView) view.findViewById(R.id.tvHarga);
            ivPerawat = view.findViewById(R.id.ivPerawat);
            linePerawat = view.findViewById(R.id.linePerawat);

        }
    }

    public AdapterPerawatan(Context mContext, List<Perawatan> perawatanList) {
        this.mContext = mContext;
        this.perawatanList = perawatanList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_perawatan, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (perawatanList.isEmpty()){

            Log.d("isiPerawatan: ",""+perawatanList.size());
        }else {

            Resources res = mContext.getResources();
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            final Perawatan perawatan  = perawatanList.get(position);
            int harga = Integer.parseInt(perawatan.getHarga());

            holder.tvNamaPerawat.setText(perawatan.getNama());
            holder.txtHarga.setText(formatRupiah.format((double) harga) + " / hari");
            Glide.with(mContext)
                    .load(perawatan.getFoto())
                    .into(holder.ivPerawat);

            holder.linePerawat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailPerawatActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("perawatan",perawatan);
                    mContext.startActivity(intent);
                }
            });


        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return perawatanList.size();
    }
}
