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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;

public class AddcatActivity extends AppCompatActivity {

    EditText etNama,etUmur,etRas,etDokterLangganan,etJenisMakanan,etSusu,etShampo,etDeskripsiPerawatan;
    ImageView ivKucing;
    Button btnSimpan,btnEdit;
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
        setContentView(R.layout.activity_addcat);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(AddcatActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kucing");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        btnSimpan = findViewById(R.id.btnSimpan);
        btnEdit = findViewById(R.id.btnEdit);
        etNama = findViewById(R.id.etNama);
        etUmur = findViewById(R.id.etUmur);
        etRas = findViewById(R.id.etRas);
        ivKucing = findViewById(R.id.ivKucing);
        etDokterLangganan = findViewById(R.id.etDokterLangganan);
        etJenisMakanan = findViewById(R.id.etJenisMakanan);
        etSusu = findViewById(R.id.etSusu);
        etShampo = findViewById(R.id.etShampo);
        etDeskripsiPerawatan = findViewById(R.id.etDeskripsiPerawatan);
        spJenisKelamin = findViewById(R.id.spJenisKelamin);
        spKondisi = findViewById(R.id.spKondisi);

        jenisKelamin    = "Jantan";
        kondisi         = "Sehat";

        pDialogLoading = new SweetAlertDialog(AddcatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    ActivityCompat.requestPermissions(AddcatActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });
        spJenisKelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jenisKelamin = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spKondisi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kondisi = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        else if (uri == null){
            showError("Gambar belum dipilih");
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
            simpanData();
        }
    }

    private void showError(String message){
        new SweetAlertDialog(AddcatActivity.this,SweetAlertDialog.ERROR_TYPE)
                .setContentText(message)
                .setTitleText("Oops..")
                .setConfirmText("OK")
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivKucing.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivKucing.setImageURI(uri);
        }
    }

    private void simpanData(){
        ras =  etRas.getText().toString();
        if (ras.equals("") || ras.length() == 0){
            ras = "no";
        }

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
                Toast.makeText(AddcatActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(AddcatActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                final String urlGambar = downloadUrl.toString();
                Kucing kucing = new Kucing(
                        etNama.getText().toString(),
                        SharedVariable.userID,
                        etUmur.getText().toString(),
                        ras,
                        urlGambar,
                        jenisKelamin,
                        etDokterLangganan.getText().toString(),
                        kondisi,
                        etJenisMakanan.getText().toString(),
                        etSusu.getText().toString(),
                        etShampo.getText().toString(),
                        etDeskripsiPerawatan.getText().toString()
                );
                kucing.setIdKucing(timeStamp);
                kucing.setIsAdopsi("no");
                kucing.setIsDijual("no");

                ref.document(timeStamp).set(kucing).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        if (task.isSuccessful()){
                            new SweetAlertDialog(AddcatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Data kucing ditambahkan")
                                    .setTitleText("Berhasil")
                                    .setConfirmText("OK")
                                    .show();

                            resetKomponen();
                        }else {
                            new SweetAlertDialog(AddcatActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Terjadi kesalahan")
                                    .setTitleText("Oops..")
                                    .setConfirmText("OK")
                                    .show();
                            Log.d("erorUpload:","erorGambar "+task.getException().toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(AddcatActivity.this,SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Terjadi kesalahan")
                                .setTitleText("Oops..")
                                .setConfirmText("OK")
                                .show();
                        Log.d("erorUpload:","erorGambar "+e.toString());
                    }
                });

            }
        });

    }

    private void resetKomponen(){
        etUmur.setText("");
        etRas.setText("");
        etNama.setText("");
        etDeskripsiPerawatan.setText("");
        etDokterLangganan.setText("");
        etSusu.setText("");
        etShampo.setText("");
        etJenisMakanan.setText("");
        ivKucing.setImageResource(R.drawable.pawprint);
        uri = null;
    }
}
