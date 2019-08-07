package myproject.pecintakucinglampung.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;

public class DetailcatActivity extends AppCompatActivity {

    TextView tvNamaKucing,tvUmur,tvRas,tvAdopsi,tvJual;
    ImageView ivKucing;
    LinearLayout lineAdopsi,lineJual;
    Intent intent;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refPemilik;
    FirebaseFirestore firestore;
    Kucing kucingku;
    CardView cardSetting;
    Button btnHubungi;
    private String phonePemilik = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailcat);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kucing");
        refPemilik = firestore.collection("users");

        intent = getIntent();
        kucingku = (Kucing) intent.getSerializableExtra("kucing");

        tvNamaKucing = findViewById(R.id.tvNamaKucing);
        tvUmur = findViewById(R.id.tvUmur);
        tvRas = findViewById(R.id.tvRas);
        tvAdopsi = findViewById(R.id.tvAdopsi);
        tvJual = findViewById(R.id.tvJual);
        ivKucing = findViewById(R.id.ivKucing);
        lineAdopsi = findViewById(R.id.lineAdopsi);
        lineJual = findViewById(R.id.lineJual);
        cardSetting = findViewById(R.id.cardSetting);
        btnHubungi = findViewById(R.id.btnHubungi);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        setView();
        getPemilik(kucingku.getIdPemilik());

        lineAdopsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kucingku.getIsAdopsi().equals("no")){
                    pDialogLoading.show();
                    ref.document(kucingku.getIdKucing()).update("isAdopsi","true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialogLoading.dismiss();
                            new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Kucing ini sekarang di iklankan untuk adopsi")
                                    .show();
                            tvAdopsi.setText("Di iklankan untuk adopsi (Klik untuk membatalkan)");
                        }
                    });
                    kucingku.setIsAdopsi("true");
                }else {
                    pDialogLoading.show();
                    ref.document(kucingku.getIdKucing()).update("isAdopsi","no").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialogLoading.dismiss();
                            new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Iklan adopsi dibatalkan")
                                    .show();
                            tvAdopsi.setText("Ajukan untuk adopsi");
                        }
                    });
                    kucingku.setIsAdopsi("no");
                }
            }
        });

        lineJual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kucingku.getIsDijual().equals("no")){
                    pDialogLoading.show();
                    ref.document(kucingku.getIdKucing()).update("isDijual","true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialogLoading.dismiss();
                            new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Kucing ini sekarang di iklankan untuk dijual")
                                    .show();
                            tvJual.setText("Di iklankan untuk dijual (Klik untuk membatalkan)");
                        }
                    });
                    kucingku.setIsDijual("true");
                }else{
                    pDialogLoading.show();
                    ref.document(kucingku.getIdKucing()).update("isDijual","no").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialogLoading.dismiss();
                            new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Iklan penjualan dibatalkan")
                                    .show();
                            tvJual.setText("Jual Kucing");
                        }
                    });
                    kucingku.setIsDijual("no");
                }
            }
        });
        btnHubungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phonePemilik.length() == 0){
                    new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Nomor pemilik bermasalah,coba lagi nanti")
                            .show();
                }else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phonePemilik, null));
                    startActivity(intent);
                }

            }
        });

    }

    private void setView(){

        if (kucingku.getIsDijual().equals("true")){
            tvJual.setText("Di iklankan untuk dijual (Klik untuk membatalkan)");
        }
        if (kucingku.getIsAdopsi().equals("true")){
            tvAdopsi.setText("Di iklankan untuk adopsi (Klik untuk membatalkan)");
        }

        tvNamaKucing.setText(kucingku.getNama());
        tvUmur.setText(kucingku.getUmur());
        String ras = kucingku.getRas();
        if (kucingku.getRas().equals("no")){
            ras = "Tidak diisi oleh pemilik";
        }else {
            ras = kucingku.getRas();
        }
        tvRas.setText(ras);

        if (!kucingku.getUrlGambar().equals("no")){
            Glide.with(this)
                    .load(kucingku.getUrlGambar())
                    .into(ivKucing);
        }

        //jika bukan pemilik
        if (!kucingku.getIdPemilik().equals(SharedVariable.userID)){
            cardSetting.setVisibility(View.GONE);
            btnHubungi.setVisibility(View.VISIBLE);
        }
    }

    private void getPemilik(String idPemilik){
        pDialogLoading.show();
        refPemilik.document(idPemilik).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                pDialogLoading.dismiss();
                DocumentSnapshot dc = task.getResult();
                phonePemilik = dc.get("nope").toString();
            }
        });
    }
}
