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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import myproject.pecintakucinglampung.Kelas.Kesehatan;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;
import myproject.pecintakucinglampung.activity.ListKesehatanActivity;

public class UbahKesehatanActivity extends AppCompatActivity {

    Intent intent;
    Kesehatan kesehatan;
    Button btnAdd;
    EditText etJudul,etDeskripsi,etSolusi;
    FirebaseFirestore firestore;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    Uri uri,file;
    ImageView ivKesehatan;
    FirebaseUser fbUser;

    private int PLACE_PICKER_REQUEST = 1;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_kesehatan);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(UbahKesehatanActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kesehatan");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        kesehatan = (Kesehatan) intent.getSerializableExtra("kesehatan");

        btnAdd = findViewById(R.id.btnAdd);
        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etSolusi = findViewById(R.id.etSolusi);
        ivKesehatan = findViewById(R.id.ivKesehatan);

        pDialogLoading = new SweetAlertDialog(UbahKesehatanActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        etJudul.setText(kesehatan.getJudul());
        etDeskripsi.setText(kesehatan.getDeskripsi());
        etSolusi.setText(kesehatan.getSolusi());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        ivKesehatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UbahKesehatanActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
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
        String getJudul = etJudul.getText().toString();
        String getDeskripsi  = etDeskripsi.getText().toString();
        String getSolusi = etSolusi.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);

        // Check if all strings are null or not
        if (getJudul.equals("") || getJudul.length() == 0 ||
                getDeskripsi.equals("") || getDeskripsi.length() == 0 ||
                getSolusi.equals("") || getSolusi.length() == 0) {

            new SweetAlertDialog(UbahKesehatanActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();

        }
        else{
            pDialogLoading.show();
            if (uri == null){
                updateData();
            }else {
                updateWithGambar();
            }

        }
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
                Toast.makeText(UbahKesehatanActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(UbahKesehatanActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                final String urlGambar = downloadUrl.toString();
                ref.document(kesehatan.getIdKesehatan()).update("judul",etJudul.getText().toString());
                ref.document(kesehatan.getIdKesehatan()).update("urlGambar",urlGambar);
                ref.document(kesehatan.getIdKesehatan()).update("deskripsi",etDeskripsi.getText().toString());
                ref.document(kesehatan.getIdKesehatan()).update("solusi",etSolusi.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(UbahKesehatanActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Perubahan tersimpan")
                                .show();

                        Intent intent = new Intent(getApplicationContext(), ListKesehatanActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });



            }
        });
    }

    private void updateData(){

        ref.document(kesehatan.getIdKesehatan()).update("judul",etJudul.getText().toString());
        ref.document(kesehatan.getIdKesehatan()).update("deskripsi",etDeskripsi.getText().toString());
        ref.document(kesehatan.getIdKesehatan()).update("solusi",etSolusi.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(UbahKesehatanActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("Perubahan tersimpan")
                        .show();

                Intent intent = new Intent(getApplicationContext(), ListKesehatanActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            ivKesehatan.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            ivKesehatan.setImageURI(uri);
        }
    }
}
