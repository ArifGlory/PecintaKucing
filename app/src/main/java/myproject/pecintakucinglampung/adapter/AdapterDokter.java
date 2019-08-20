package myproject.pecintakucinglampung.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Dokter;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.admin.KelolaDokterActivity;
import myproject.pecintakucinglampung.admin.UbahDokterActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterDokter extends RecyclerView.Adapter<AdapterDokter.MyViewHolder> {

    private Context mContext;
    private List<Dokter> dokterList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaDokter,tvPhone,tvAlamat;
        public ImageView ivDokter;
        public LinearLayout lineDokter;

        public MyViewHolder(View view) {
            super(view);
            tvNamaDokter = (TextView) view.findViewById(R.id.tvNamaDokter);
            tvPhone = (TextView) view.findViewById(R.id.tvPhone);
            tvAlamat = (TextView) view.findViewById(R.id.tvAlamat);
            ivDokter =  view.findViewById(R.id.ivDokter);
            lineDokter = view.findViewById(R.id.lineDokter);

        }
    }

    public AdapterDokter(Context mContext, List<Dokter> dokterList) {
        this.mContext = mContext;
        this.dokterList = dokterList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("dokter");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dokter, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (dokterList.isEmpty()){

            Log.d("isiDokter: ",""+dokterList.size());
        }else {


            final Dokter dokter  = dokterList.get(position);

            holder.tvNamaDokter.setText(dokter.getNama());
            holder.tvPhone.setText(dokter.getPhone());
            holder.tvAlamat.setText(dokter.getAlamat());
            Glide.with(mContext)
                    .load(dokter.getUrlGambar())
                    .into(holder.ivDokter);

           holder.lineDokter.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   if (SharedVariable.email.equals("admin@gmail.com")){
                       new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                               .setTitleText("Kelola Dokter")
                               .setContentText("Pilih aksi yang anda inginkan")
                               .setConfirmText("Hapus")
                               .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                   @Override
                                   public void onClick(SweetAlertDialog sDialog) {
                                       sDialog.dismissWithAnimation();
                                       pDialogLoading.show();
                                       ref.document(dokter.getIdDokter()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               pDialogLoading.dismiss();
                                               if (mContext instanceof KelolaDokterActivity){
                                                   ((KelolaDokterActivity)mContext).getDataDokter();
                                               }
                                           }
                                       });
                                   }
                               })
                               .setCancelButton("Ubah", new SweetAlertDialog.OnSweetClickListener() {
                                   @Override
                                   public void onClick(SweetAlertDialog sDialog) {
                                       sDialog.dismissWithAnimation();
                                       Intent intent = new Intent(mContext, UbahDokterActivity.class);
                                       intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                       intent.putExtra("dokter",dokter);
                                       mContext.startActivity(intent);

                                   }
                               })
                               .show();
                   }
                   return true;
               }
           });

           holder.lineDokter.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(mContext, UbahDokterActivity.class);
                   intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                   intent.putExtra("dokter",dokter);
                   mContext.startActivity(intent);
               }
           });


        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return dokterList.size();
    }
}
