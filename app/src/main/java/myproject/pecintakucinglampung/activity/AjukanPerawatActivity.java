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
import android.widget.TextView;
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
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.Kelas.Perawatan;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;

public class AjukanPerawatActivity extends AppCompatActivity {

    ImageView ivProfPict;
    EditText etHarga,etDeskripsi;
    TextView tvInfo;
    Button btnDaftar,btnChange;
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
    Intent intent;
    private String checkIfRegistered = "no";
    Perawatan perawatanSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajukan_perawat);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(AjukanPerawatActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("perawatan");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        btnDaftar = findViewById(R.id.btnDaftar);
        btnChange = findViewById(R.id.btnChange);
        etHarga = findViewById(R.id.etHarga);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        ivProfPict = findViewById(R.id.ivProfPict);
        tvInfo = findViewById(R.id.tvInfo);

        intent = getIntent();
        checkIfRegistered = intent.getStringExtra("check");
        perawatanSend = (Perawatan) intent.getSerializableExtra("perawatan");

        if (checkIfRegistered.equals("true")){
            etHarga.setEnabled(false);
            etDeskripsi.setEnabled(false);
            ivProfPict.setEnabled(false);

            etHarga.setText(perawatanSend.getHarga());
            etDeskripsi.setText(perawatanSend.getDeskripsi());
            Glide.with(this)
                    .load(perawatanSend.getFoto())
                    .into(ivProfPict);

            btnDaftar.setVisibility(View.GONE);
            btnChange.setVisibility(View.VISIBLE);
            tvInfo.setText("Anda sudah terdaftar menjadi perawat kucing");
        }

        pDialogLoading = new SweetAlertDialog(AjukanPerawatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        ivProfPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AjukanPerawatActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });


    }

    private void checkValidation() {

        // Get all edittext texts
        String getHarga = etHarga.getText().toString();
        String getDeskripsi = etDeskripsi.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);

        // Check if all strings are null or not
        if (getHarga.equals("") || getHarga.length() == 0
                || getDeskripsi.equals("") || getDeskripsi.length() == 0
                || uri == null) {

            new SweetAlertDialog(AjukanPerawatActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();

        }
        else{
            pDialogLoading.show();
            simpanData();
        }
    }

    private void simpanData(){
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
                Toast.makeText(AjukanPerawatActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(AjukanPerawatActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                final String urlGambar = downloadUrl.toString();
                Perawatan perawatan = new Perawatan(timeStamp,
                        SharedVariable.userID,
                        SharedVariable.nama,
                        SharedVariable.phone,
                        urlGambar,
                        etDeskripsi.getText().toString(),
                        etHarga.getText().toString(),
                        "0");

                ref.document(timeStamp).set(perawatan).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        if (task.isSuccessful()){
                            new SweetAlertDialog(AjukanPerawatActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Anda sekarang terdaftar sebagai perawat")
                                    .setTitleText("Berhasil")
                                    .setConfirmText("OK")
                                    .show();

                            Intent intent = new Intent(getApplicationContext(),PerawatanActivity.class);
                            startActivity(intent);
                        }else {
                            new SweetAlertDialog(AjukanPerawatActivity.this,SweetAlertDialog.ERROR_TYPE)
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
                        new SweetAlertDialog(AjukanPerawatActivity.this,SweetAlertDialog.ERROR_TYPE)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivProfPict.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivProfPict.setImageURI(uri);
        }
    }

    private void resetKomponen(){
        etHarga.setText("");
        etDeskripsi.setText("");
        ivProfPict.setImageResource(R.drawable.pawprint);
        uri = null;
    }

}
