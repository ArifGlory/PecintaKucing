package myproject.pecintakucinglampung.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.Kelas.Slider;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.DetailPerawatActivity;
import myproject.pecintakucinglampung.activity.MycatActivity;
import myproject.pecintakucinglampung.admin.KelolaSliderActivity;
import myproject.pecintakucinglampung.admin.UbahDokterActivity;
import myproject.pecintakucinglampung.admin.UbahSliderActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterSlider extends RecyclerView.Adapter<AdapterSlider.MyViewHolder> {

    private Context mContext;
    private List<Slider> sliderList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvLink;
        public ImageView ivSlide;
        public LinearLayout lineSlide;

        public MyViewHolder(View view) {
            super(view);
            tvLink = (TextView) view.findViewById(R.id.tvLink);
            ivSlide =  view.findViewById(R.id.ivSlide);
            lineSlide = view.findViewById(R.id.lineSlide);

        }
    }

    public AdapterSlider(Context mContext, List<Slider> sliderList) {
        this.mContext = mContext;
        this.sliderList = sliderList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("slider");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (sliderList.isEmpty()){

            Log.d("isiSlider: ",""+sliderList.size());
        }else {


            final Slider slider  = sliderList.get(position);

            holder.tvLink.setText("Link = "+slider.getLink());
            Glide.with(mContext)
                    .load(slider.getUrlGambar())
                    .into(holder.ivSlide);

           holder.lineSlide.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                           .setTitleText("Kelola Slider")
                           .setContentText("Pilih aksi yang anda inginkan")
                           .setConfirmText("Hapus")
                           .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                               @Override
                               public void onClick(SweetAlertDialog sDialog) {
                                   sDialog.dismissWithAnimation();
                                   pDialogLoading.show();
                                   ref.document(slider.getIdSlider()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           pDialogLoading.dismiss();
                                           if (mContext instanceof KelolaSliderActivity){
                                               ((KelolaSliderActivity)mContext).getDataSlider();
                                           }
                                       }
                                   });
                               }
                           })
                           .setCancelButton("Ubah", new SweetAlertDialog.OnSweetClickListener() {
                               @Override
                               public void onClick(SweetAlertDialog sDialog) {
                                   sDialog.dismissWithAnimation();
                                   Intent intent = new Intent(mContext, UbahSliderActivity.class);
                                   intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                   intent.putExtra("slider",slider);
                                   mContext.startActivity(intent);

                               }
                           })
                           .show();
                   return true;
               }
           });


        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return sliderList.size();
    }
}
