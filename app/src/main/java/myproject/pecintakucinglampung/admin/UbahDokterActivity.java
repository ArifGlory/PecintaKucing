package myproject.pecintakucinglampung.admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Dokter;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;

public class UbahDokterActivity extends AppCompatActivity {

    ImageView ivDokter;
    EditText etNama,etBidang,etPhone,etJadwal;
    Button btnAlamat,btnSimpan;
    TextView tvAlamat;

    FirebaseFirestore firestore;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    Uri uri,file;
    FirebaseUser fbUser;

    private int PLACE_PICKER_REQUEST = 1;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    private String alamat,latitude,longitude,time;
    private Double lat,lon;

    Intent intent;
    Dokter dokter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_dokter);

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(UbahDokterActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("dokter");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        dokter = (Dokter) intent.getSerializableExtra("dokter");

        ivDokter = findViewById(R.id.ivDokter);
        etNama = findViewById(R.id.etNama);
        etBidang = findViewById(R.id.etBidang);
        etPhone = findViewById(R.id.etPhone);
        tvAlamat = findViewById(R.id.tvAlamat);
        etJadwal = findViewById(R.id.etJadwal);
        btnAlamat = findViewById(R.id.btnAlamat);
        btnSimpan = findViewById(R.id.btnSimpan);

        pDialogLoading = new SweetAlertDialog(UbahDokterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        setView();

        ivDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UbahDokterActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });

        btnAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(UbahDokterActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });


    }

    private void setView(){
        Glide.with(this)
                .load(dokter.getUrlGambar())
                .into(ivDokter);
        etNama.setText(dokter.getNama());
        etBidang.setText(dokter.getBidang());
        etPhone.setText(dokter.getPhone());
        etJadwal.setText(dokter.getJadwal());
        tvAlamat.setVisibility(View.VISIBLE);
        tvAlamat.setText(dokter.getAlamat());

        latitude = dokter.getLat();
        longitude = dokter.getLon();
    }

    private void checkValidation() {

        // Get all edittext texts
        String getNama = etNama.getText().toString();
        String getBidang  = etBidang.getText().toString();
        String getPhone = etPhone.getText().toString();
        String getJadwal = etJadwal.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);

        // Check if all strings are null or not
        if (getNama.equals("") || getNama.length() == 0 ||
                getBidang.equals("") || getBidang.length() == 0 ||
                getPhone.equals("") || getPhone.length() == 0 ||
                getJadwal.equals("") || getJadwal.length() == 0) {

            new SweetAlertDialog(UbahDokterActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();

        }
        else{
            pDialogLoading.show();

            if (uri == null){
                updateWithoutImage();
            }else{
                updateWithImage();
            }
        }
    }

    private void updateWithoutImage(){
        pDialogLoading.show();
        ref.document(dokter.getIdDokter()).update("nama",etNama.getText().toString());
        ref.document(dokter.getIdDokter()).update("phone",etPhone.getText().toString());
        ref.document(dokter.getIdDokter()).update("alamat",tvAlamat.getText().toString());
        ref.document(dokter.getIdDokter()).update("jadwal",etJadwal.getText().toString());
        ref.document(dokter.getIdDokter()).update("bidang",etBidang.getText().toString());
        ref.document(dokter.getIdDokter()).update("lat",latitude);
        ref.document(dokter.getIdDokter()).update("lon",longitude).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(UbahDokterActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("Perubahan tersimpan")
                        .show();

                Intent intent = new Intent(getApplicationContext(),KelolaDokterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updateWithImage(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UbahDokterActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(UbahDokterActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                final String urlGambar = downloadUrl.toString();
                ref.document(dokter.getIdDokter()).update("urlGambar",urlGambar);
                ref.document(dokter.getIdDokter()).update("nama",etNama.getText().toString());
                ref.document(dokter.getIdDokter()).update("phone",etPhone.getText().toString());
                ref.document(dokter.getIdDokter()).update("alamat",tvAlamat.getText().toString());
                ref.document(dokter.getIdDokter()).update("jadwal",etJadwal.getText().toString());
                ref.document(dokter.getIdDokter()).update("bidang",etBidang.getText().toString());
                ref.document(dokter.getIdDokter()).update("lat",latitude);
                ref.document(dokter.getIdDokter()).update("lon",longitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(UbahDokterActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Perubahan simpan")
                                .show();

                        Intent intent = new Intent(getApplicationContext(),KelolaDokterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);
                //tvPlaceAPI.setText(toastMsg);

                tvAlamat.setText(place.getAddress());
                tvAlamat.setVisibility(View.VISIBLE);

                alamat = (String) place.getAddress();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                latitude = ""+lat;
                longitude = ""+lon;
                Toast.makeText(getApplicationContext()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }else

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivDokter.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivDokter.setImageURI(uri);
        }
    }
}
