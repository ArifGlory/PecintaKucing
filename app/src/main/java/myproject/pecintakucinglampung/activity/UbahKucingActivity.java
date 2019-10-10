package myproject.pecintakucinglampung.activity;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;
import myproject.pecintakucinglampung.admin.KelolaDokterActivity;
import myproject.pecintakucinglampung.admin.UbahDokterActivity;

public class UbahKucingActivity extends AppCompatActivity {

    Intent intent;
    Kucing kucing;
    EditText etNama,etUmur,etRas,etDokterLangganan,etJenisMakanan,etSusu,etShampo,etDeskripsiPerawatan;
    ImageView ivKucing;
    Button btnSimpan;
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
    private String ras,jenisKelamin,kondisi;
    Spinner spJenisKelamin,spKondisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_kucing);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(UbahKucingActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kucing");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        intent  = getIntent();
        kucing = (Kucing) intent.getSerializableExtra("kucing");

        Log.d("idKucing=",kucing.getIdKucing());
        btnSimpan = findViewById(R.id.btnSimpan);
        etNama = findViewById(R.id.etNama);
        etUmur = findViewById(R.id.etUmur);
        etRas = findViewById(R.id.etRas);
        etDokterLangganan = findViewById(R.id.etDokterLangganan);
        etJenisMakanan = findViewById(R.id.etJenisMakanan);
        etSusu = findViewById(R.id.etSusu);
        etShampo = findViewById(R.id.etShampo);
        etDeskripsiPerawatan = findViewById(R.id.etDeskripsiPerawatan);
        spJenisKelamin = findViewById(R.id.spJenisKelamin);
        spKondisi = findViewById(R.id.spKondisi);

        jenisKelamin    = "Jantan";
        kondisi         = "Sehat";

        ivKucing = findViewById(R.id.ivKucing);
        if (kucing.getRas().equals("no")){
            etRas.setText("");
        }else{
            etRas.setText(kucing.getRas());
        }
        etNama.setText(kucing.getNama());
        etUmur.setText(kucing.getUmur());
        etDeskripsiPerawatan.setText(kucing.getDeskripsiPerawatan());
        etDokterLangganan.setText(kucing.getNmDokterLangganan());
        etSusu.setText(kucing.getSusu());
        etShampo.setText(kucing.getShampo());
        etJenisMakanan.setText(kucing.getJenisMakanan());

        Glide.with(this)
                .load(kucing.getUrlGambar())
                .into(ivKucing);

        pDialogLoading = new SweetAlertDialog(UbahKucingActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        ivKucing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UbahKucingActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });
    }

    private void checkValidation() {

        // Get all edittext texts
        String getFullName = etNama.getText().toString();
        String getUmur = etUmur.getText().toString();
        String getRas = etRas.getText().toString();
        String getNamaDokterLangganan = etDokterLangganan.getText().toString();
        String getJenisMakanan = etJenisMakanan.getText().toString();
        String getSusu = etSusu.getText().toString();
        String getShampo = etShampo.getText().toString();
        String getDeskripsiPerawatan = etDeskripsiPerawatan.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0) {
            showError("Nama harus diisi");
        }
        else if (getUmur.equals("") || getUmur.length() == 0) {
            showError("Umur harus diisi");
        }
        else if (getRas.equals("") || getRas.length() == 0) {
            showError("Ras harus diisi");
        }
        else if (getNamaDokterLangganan.equals("") || getNamaDokterLangganan.length() == 0) {
            showError("Dokter Langganan harus diisi");
        }
        else if (getJenisMakanan.equals("") || getJenisMakanan.length() == 0) {
            showError("Jenis Makanan harus diisi");
        }
        else if (getSusu.equals("") || getSusu.length() == 0) {
            showError("Susu harus diisi");
        }
        else if (getShampo.equals("") || getShampo.length() == 0) {
            showError("Shampo harus diisi");
        }
        else if (getDeskripsiPerawatan.equals("") || getDeskripsiPerawatan.length() == 0) {
            showError("Deskripsi Perawatan harus diisi");
        }
        else{
            pDialogLoading.show();
           if (uri == null ){
               updateWithoutGambar();
           }else {
               updateWithGambar();
           }
        }
    }

    private void showError(String message){
        new SweetAlertDialog(UbahKucingActivity.this,SweetAlertDialog.ERROR_TYPE)
                .setContentText(message)
                .setTitleText("Oops..")
                .setConfirmText("OK")
                .show();
    }

    private void updateWithoutGambar(){

        ras =  etRas.getText().toString();
        if (ras.equals("") || ras.length() == 0){
            ras = "no";
        }
        ref.document(kucing.getIdKucing()).update("nama",etNama.getText().toString());
        ref.document(kucing.getIdKucing()).update("deskripsiPerawatan",etDeskripsiPerawatan.getText().toString());
        ref.document(kucing.getIdKucing()).update("jenisKelamin",jenisKelamin);
        ref.document(kucing.getIdKucing()).update("kondisiKesehatan",kondisi);
        ref.document(kucing.getIdKucing()).update("nmDokterLangganan",etDokterLangganan.getText().toString());
        ref.document(kucing.getIdKucing()).update("shampo",etShampo.getText().toString());
        ref.document(kucing.getIdKucing()).update("susu",etSusu.getText().toString());
        ref.document(kucing.getIdKucing()).update("ras",ras).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pDialogLoading.dismiss();
            }
        });
        ref.document(kucing.getIdKucing()).update("umur",etUmur.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(UbahKucingActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("Perubahan tersimpan")
                        .show();

                Intent intent = new Intent(getApplicationContext(), MycatActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateWithGambar(){
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
                Toast.makeText(UbahKucingActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(UbahKucingActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                ras =  etRas.getText().toString();
                if (ras.equals("") || ras.length() == 0){
                    ras = "no";
                }

                // save image to database
                final String urlGambar = downloadUrl.toString();
                ref.document(kucing.getIdKucing()).update("urlGambar",urlGambar);
                ref.document(kucing.getIdKucing()).update("nama",etNama.getText().toString());
                ref.document(kucing.getIdKucing()).update("ras",ras);
                ref.document(kucing.getIdKucing()).update("deskripsiPerawatan",etDeskripsiPerawatan.getText().toString());
                ref.document(kucing.getIdKucing()).update("jenisKelamin",jenisKelamin);
                ref.document(kucing.getIdKucing()).update("kondisiKesehatan",kondisi);
                ref.document(kucing.getIdKucing()).update("nmDokterLangganan",etDokterLangganan.getText().toString());
                ref.document(kucing.getIdKucing()).update("shampo",etShampo.getText().toString());
                ref.document(kucing.getIdKucing()).update("susu",etSusu.getText().toString());
                ref.document(kucing.getIdKucing()).update("umur",etUmur.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(UbahKucingActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Perubahan tersimpan")
                                .show();

                        Intent intent = new Intent(getApplicationContext(),MycatActivity.class);
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

                Toast.makeText(getApplicationContext()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }else

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivKucing.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivKucing.setImageURI(uri);
        }
    }
}
