package myproject.pecintakucinglampung.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Shader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.AddcatActivity;
import myproject.pecintakucinglampung.activity.DetailcatActivity;
import myproject.pecintakucinglampung.activity.MycatActivity;
import myproject.pecintakucinglampung.activity.UbahKucingActivity;
import myproject.pecintakucinglampung.admin.UbahDokterActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterKucing extends RecyclerView.Adapter<AdapterKucing.MyViewHolder> {

    private Context mContext;
    private List<Kucing> kucingList;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    FirebaseFirestore firestore;
    CollectionReference ref;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaKucing,tvUmur,tvRas;
        public ImageView ivKucing;
        public LinearLayout lineKucing;
        public Button btnSetting;
        public CardView cardKucing;

        public MyViewHolder(View view) {
            super(view);
            tvNamaKucing = (TextView) view.findViewById(R.id.tvNamaKucing);
            tvUmur = (TextView) view.findViewById(R.id.tvUmur);
            tvRas = (TextView) view.findViewById(R.id.tvRas);
            ivKucing = view.findViewById(R.id.ivKucing);
            lineKucing = view.findViewById(R.id.lineKucing);
            btnSetting = view.findViewById(R.id.btnSetting);
            cardKucing = view.findViewById(R.id.cardKucing);

        }
    }

    public AdapterKucing(Context mContext, List<Kucing> kucingList) {
        this.mContext = mContext;
        this.kucingList = kucingList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kucing");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

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

            if ((kucingku.getIdPemilik().equals(SharedVariable.userID )&& (mContext instanceof MycatActivity))){
                holder.btnSetting.setVisibility(View.VISIBLE);
            }

            if (kucingku.getIdPemilik().equals(SharedVariable.userID)){
                holder.cardKucing.setBackgroundColor(res.getColor(R.color.grey_10));
            }

            if(!kucingku.getUrlGambar().equals("no")){
                Glide.with(mContext)
                        .load(kucingku.getUrlGambar())
                        .into(holder.ivKucing);
            }

            holder.btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailcatActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("kucing",kucingku);
                    mContext.startActivity(intent);
                }
            });

            holder.lineKucing.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (kucingku.getIdPemilik().equals(SharedVariable.userID)){

                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Kelola Kucing")
                                .setContentText("Anda dapat melakukan perubahan data kucing ini")
                                .setConfirmText("Ubah Data")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent = new Intent(mContext, UbahKucingActivity.class);
                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("kucing",kucingku);
                                        mContext.startActivity(intent);
                                    }
                                })
                                .setCancelButton("Hapus", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        pDialogLoading.show();
                                        ref.document(kucingku.getIdKucing()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pDialogLoading.dismiss();
                                                if (mContext instanceof MycatActivity){
                                                    ((MycatActivity)mContext).getKucingByPemilik(SharedVariable.userID);
                                                }
                                            }
                                        });

                                    }
                                })
                                .show();
                    }
                    return true;
                }
            });

            holder.lineKucing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!kucingku.getIdPemilik().equals(SharedVariable.userID)){
                        Intent intent = new Intent(mContext, DetailcatActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("kucing",kucingku);
                        mContext.startActivity(intent);
                    }
                }
            });





        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return kucingList.size();
    }
}
