package myproject.pecintakucinglampung.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Dokter;
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;

public class DetailcatActivity extends AppCompatActivity {

    TextView tvNamaKucing,tvUmur,tvRas,tvAdopsi,tvJual,tvAlamat,tvAlamat2
            ,tvJenisKelamin,tvKondisiKesehatan,tvNamaDokter,tvJenisMakanan
            ,tvSusu,tvShampo,tvDeskripsiPerawatan,tvHarga;
    ImageView ivKucing;
    LinearLayout lineAdopsi,lineJual;
    Intent intent;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    public static android.app.AlertDialog dialog;
    CollectionReference ref,refPemilik;
    FirebaseFirestore firestore;
    Kucing kucingku;
    CardView cardSetting;
    Button btnHubungi,btnLokasi;
    private String phonePemilik = "";
    private String alamatPemilik = "";
    private int PLACE_PICKER_REQUEST = 1;
    String alamat,latitude,longitude,alamatKirim;
    LatLng posisiTarget;
    Double lat,lon;

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
        tvAlamat = findViewById(R.id.tvAlamat);
        tvJenisKelamin = findViewById(R.id.tvJenisKelamin);
        tvKondisiKesehatan = findViewById(R.id.tvKondisiKesehatan);
        tvNamaDokter = findViewById(R.id.tvNamaDokter);
        tvJenisMakanan = findViewById(R.id.tvJenisMakanan);
        tvSusu = findViewById(R.id.tvSusu);
        tvShampo = findViewById(R.id.tvShampo);
        tvDeskripsiPerawatan = findViewById(R.id.tvDeskripsiPerawatan);
        tvHarga = findViewById(R.id.tvHarga);

        ivKucing = findViewById(R.id.ivKucing);
        lineAdopsi = findViewById(R.id.lineAdopsi);
        lineJual = findViewById(R.id.lineJual);
        cardSetting = findViewById(R.id.cardSetting);
        btnHubungi = findViewById(R.id.btnHubungi);
        btnLokasi = findViewById(R.id.btnLokasi);

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

                    LayoutInflater minlfater = LayoutInflater.from(DetailcatActivity.this);
                    View v2 = minlfater.inflate(R.layout.dialog_adopsi, null);
                    dialog = new AlertDialog.Builder(DetailcatActivity.this).create();
                    dialog.setView(v2);

                    final EditText etHarga =  v2.findViewById(R.id.etHarga);
                    tvAlamat2 = v2.findViewById(R.id.tvAlamat2);
                    final Button btnSimpanAdopsi = v2.findViewById(R.id.btnSimpanAdopsi);
                    final Button btnPilihAlamat = v2.findViewById(R.id.btnPilihAlamat);

                    btnPilihAlamat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                            try {
                                //menjalankan place picker
                                startActivityForResult(builder.build(DetailcatActivity.this), PLACE_PICKER_REQUEST);

                                // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btnSimpanAdopsi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (tvAlamat2.equals("Pilih Alamat") || tvAlamat2.equals("")){
                                Toast.makeText(getApplicationContext(),"Alamat belum dipilih",Toast.LENGTH_SHORT).show();
                            }else{
                                pDialogLoading.show();
                                ref.document(kucingku.getIdKucing()).update("alamat",alamat);
                                ref.document(kucingku.getIdKucing()).update("lat",latitude);
                                ref.document(kucingku.getIdKucing()).update("lon",longitude);
                                ref.document(kucingku.getIdKucing()).update("isAdopsi","true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pDialogLoading.dismiss();
                                        new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Kucing ini sekarang di iklankan untuk adopsi")
                                                .show();
                                        tvAdopsi.setText("Di iklankan untuk adopsi (Klik untuk membatalkan)");
                                        dialog.dismiss();
                                    }
                                });
                                kucingku.setIsAdopsi("true");

                            }

                        }
                    });

                    dialog.show();


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

                    LayoutInflater minlfater = LayoutInflater.from(DetailcatActivity.this);
                    View v2 = minlfater.inflate(R.layout.dialog_input_harga_kucing, null);
                    dialog = new AlertDialog.Builder(DetailcatActivity.this).create();
                    dialog.setView(v2);

                    final EditText etHarga =  v2.findViewById(R.id.etHarga);
                    tvAlamat2 = v2.findViewById(R.id.tvAlamat2);
                    final Button btnSimpanJual = v2.findViewById(R.id.btnSimpanJual);
                    final Button btnPilihAlamat = v2.findViewById(R.id.btnPilihAlamat);

                    btnPilihAlamat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                            try {
                                //menjalankan place picker
                                startActivityForResult(builder.build(DetailcatActivity.this), PLACE_PICKER_REQUEST);

                                // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    btnSimpanJual.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String harga = etHarga.getText().toString();
                            if (harga.length()  == 0 || harga.equals("")){
                                Toast.makeText(getApplicationContext(),"Harga harus diisi",Toast.LENGTH_SHORT).show();
                            }else if (tvAlamat2.equals("Pilih Alamat") || tvAlamat2.equals("")){
                                Toast.makeText(getApplicationContext(),"Alamat belum dipilih",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                ref.document(kucingku.getIdKucing()).update("harga",harga);
                                ref.document(kucingku.getIdKucing()).update("alamat",alamat);
                                ref.document(kucingku.getIdKucing()).update("lat",latitude);
                                ref.document(kucingku.getIdKucing()).update("lon",longitude);
                                pDialogLoading.show();
                                ref.document(kucingku.getIdKucing()).update("isDijual","true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pDialogLoading.dismiss();
                                        new SweetAlertDialog(DetailcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Kucing ini sekarang di iklankan untuk dijual")
                                                .show();
                                        tvJual.setText("Di iklankan untuk dijual (Klik untuk membatalkan)");
                                        dialog.dismiss();
                                    }
                                });
                                kucingku.setIsDijual("true");
                            }
                        }
                    });

                    dialog.show();


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
        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PetaRuteActivity.class);
                intent.putExtra("alamatKirim",alamatKirim);
                intent.putExtra("posisiTarget",posisiTarget);
                startActivity(intent);
            }
        });


    }

    private void getDataHarga(){
        ref.document(kucingku.getIdKucing()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.getString("harga") != null){
                        String harga = document.getString("harga");

                        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                        Double myHarga = Double.valueOf(harga);
                        tvHarga.setText("Harga "+formatRupiah.format((double) myHarga));
                        tvHarga.setVisibility(View.VISIBLE);

                        Log.d("harga:",harga);
                    }else{
                        Log.d("harga:","belum ada harga");
                    }

                    if (document.getString("alamat") != null){
                        String lat = document.getString("lat");
                        String lon = document.getString("lon");
                        alamatKirim = document.getString("alamat");

                        Double latitude = Double.valueOf(lat);
                        Double longitude = Double.valueOf(lon);
                        posisiTarget = new LatLng(latitude,longitude);
                    }
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
        tvJenisKelamin.setText(kucingku.getJenisKelamin());
        tvNamaDokter.setText(kucingku.getNmDokterLangganan());
        tvJenisMakanan.setText(kucingku.getJenisMakanan());
        tvSusu.setText(kucingku.getSusu());
        tvShampo.setText(kucingku.getShampo());
        tvDeskripsiPerawatan.setText(kucingku.getDeskripsiPerawatan());

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
        //jika untuk adopsi
        if (kucingku.getIsAdopsi().equals("true")){
            tvHarga.setVisibility(View.GONE);
        }


        //jika bukan pemilik
        if (!kucingku.getIdPemilik().equals(SharedVariable.userID)){
            cardSetting.setVisibility(View.GONE);
            btnHubungi.setVisibility(View.VISIBLE);
            btnLokasi.setVisibility(View.VISIBLE);
            getDataHarga();
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
                alamatPemilik = dc.get("alamat").toString();
                tvAlamat.setText(alamatPemilik);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, DetailcatActivity.this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);
                //tvPlaceAPI.setText(toastMsg);

                tvAlamat2.setText(place.getAddress());

                alamat = (String) place.getAddress();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                latitude = ""+lat;
                longitude = ""+lon;
                //Toast.makeText(getActivity()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
