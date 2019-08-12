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
import myproject.pecintakucinglampung.Kelas.Kesehatan;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.Kelas.Slider;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.DetailKesehatanActivity;
import myproject.pecintakucinglampung.activity.DetailcatActivity;
import myproject.pecintakucinglampung.activity.ListKesehatanActivity;
import myproject.pecintakucinglampung.admin.KelolaSliderActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterKesehatan extends RecyclerView.Adapter<AdapterKesehatan.MyViewHolder> {

    private Context mContext;
    private List<Kesehatan> kesehatanList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaKesehatan;
        public LinearLayout lineKesehatan;

        public MyViewHolder(View view) {
            super(view);
            tvNamaKesehatan = (TextView) view.findViewById(R.id.tvNamaKesehatan);
            lineKesehatan = view.findViewById(R.id.lineKesehatan);

        }
    }

    public AdapterKesehatan(Context mContext, List<Kesehatan> kesehatanList) {
        this.mContext = mContext;
        this.kesehatanList = kesehatanList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kesehatan");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kesehatan, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (kesehatanList.isEmpty()){

            Log.d("isiSlider: ",""+kesehatanList.size());
        }else {


            final Kesehatan kesehatan = kesehatanList.get(position);

            holder.tvNamaKesehatan.setText(kesehatan.getJudul());


           holder.lineKesehatan.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   if (SharedVariable.email.equals("admin@gmail.com")){
                       new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                               .setTitleText("Hapus Data Kesehatan")
                               .setContentText("Anda yakin menghapus data ini ?")
                               .setConfirmText("Ya")
                               .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                   @Override
                                   public void onClick(SweetAlertDialog sDialog) {
                                       sDialog.dismissWithAnimation();
                                       pDialogLoading.show();
                                       ref.document(kesehatan.getIdKesehatan()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               pDialogLoading.dismiss();
                                               if (mContext instanceof ListKesehatanActivity){
                                                   ((ListKesehatanActivity)mContext).getDataKesehatan();
                                               }
                                           }
                                       });
                                   }
                               })
                               .setCancelButton("Tidak", new SweetAlertDialog.OnSweetClickListener() {
                                   @Override
                                   public void onClick(SweetAlertDialog sDialog) {
                                       sDialog.dismissWithAnimation();

                                   }
                               })
                               .show();
                   }

                   return true;
               }
           });

           holder.lineKesehatan.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(mContext, DetailKesehatanActivity.class);
                   intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                   intent.putExtra("kesehatan",kesehatan);
                   mContext.startActivity(intent);
               }
           });


        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return kesehatanList.size();
    }
}
