package myproject.pecintakucinglampung.admin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    FirebaseUser fbUser;

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
            updateData();
        }
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
}
