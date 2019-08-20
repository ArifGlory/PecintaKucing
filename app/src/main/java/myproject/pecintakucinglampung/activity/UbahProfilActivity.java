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
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;

public class UbahProfilActivity extends AppCompatActivity {

    EditText etPhone,etAlamat;
    CircleImageView ivUserProfilePhoto;
    ImageButton btnSimpan;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private int PLACE_PICKER_REQUEST = 1;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;

    FirebaseFirestore firestore;
    CollectionReference ref;
    FirebaseUser fbUser;
    Uri uri,file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);
        Firebase.setAndroidContext(UbahProfilActivity.this);
        FirebaseApp.initializeApp(UbahProfilActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("users");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        btnSimpan = findViewById(R.id.btnSimpan);
        etPhone = findViewById(R.id.etPhone);
        etAlamat = findViewById(R.id.etAlamat);
        ivUserProfilePhoto = findViewById(R.id.ivProfPict);

        pDialogLoading = new SweetAlertDialog(UbahProfilActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading");
        pDialogLoading.setCancelable(false);

        if (!SharedVariable.foto.equals("no")){
            Glide.with(getApplicationContext())
                    .load(SharedVariable.foto)
                    .into(ivUserProfilePhoto);
        }

        etPhone.setText(SharedVariable.phone);
        etAlamat.setText(SharedVariable.alamat);

        ivUserProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UbahProfilActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
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

    private void checkValidation() {
        String getPhone = etPhone.getText().toString();
        String getAlamat = etAlamat.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);

        if (getPhone.equals("") || getPhone.length() == 0
        || getAlamat.equals("") || getAlamat.length() == 0) {

            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Semua Field Harus diisi")
                    .show();
        }
        else {
            pDialogLoading.show();
            updateData();
        }
    }

    private void updateData(){
        if (uri == null){
            ref.document(SharedVariable.userID).update("alamat",etAlamat.getText().toString());
            ref.document(SharedVariable.userID).update("nope",etPhone.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pDialogLoading.dismiss();
                    if (task.isSuccessful()){
                        new SweetAlertDialog(UbahProfilActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Berhasil")
                                .setContentText("Data telah diubah")
                                .show();
                        SharedVariable.phone = etPhone.getText().toString();
                        SharedVariable.alamat = etAlamat.getText().toString();
                    }else {
                        new SweetAlertDialog(UbahProfilActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Terjadi kesalahan, coba lagi nanti")
                                .show();
                    }
                }
            });
        }else{
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
                    Toast.makeText(UbahProfilActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    pDialogLoading.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(UbahProfilActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                    // save image to database
                    final String urlGambar = downloadUrl.toString();
                    ref.document(SharedVariable.userID).update("nope",etPhone.getText().toString());
                    ref.document(SharedVariable.userID).update("alamat",etAlamat.getText().toString());

                    ref.document(SharedVariable.userID).update("foto",urlGambar).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialogLoading.dismiss();
                            if (task.isSuccessful()){
                                new SweetAlertDialog(UbahProfilActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Berhasil")
                                        .setContentText("Data telah diubah")
                                        .show();
                                SharedVariable.phone = etPhone.getText().toString();
                                SharedVariable.foto = urlGambar;
                                SharedVariable.alamat = etAlamat.getText().toString();
                            }else {
                                new SweetAlertDialog(UbahProfilActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Terjadi kesalahan, coba lagi nanti")
                                        .show();
                            }
                        }
                    });



                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();


            ivUserProfilePhoto.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivUserProfilePhoto.setImageURI(uri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
