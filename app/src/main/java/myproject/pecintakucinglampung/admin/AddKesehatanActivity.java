package myproject.pecintakucinglampung.admin;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Kesehatan;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.Utils;

public class AddKesehatanActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_add_kesehatan);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(AddKesehatanActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kesehatan");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        btnAdd = findViewById(R.id.btnAdd);
        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etSolusi = findViewById(R.id.etSolusi);

        pDialogLoading = new SweetAlertDialog(AddKesehatanActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

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

            new SweetAlertDialog(AddKesehatanActivity.this,SweetAlertDialog.ERROR_TYPE)
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
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        Kesehatan kesehatan = new Kesehatan(timeStamp,
                etJudul.getText().toString(),
                etDeskripsi.getText().toString(),
                etSolusi.getText().toString());

        ref.document(timeStamp).set(kesehatan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(AddKesehatanActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Data disimpan")
                            .setTitleText("Berhasil")
                            .setConfirmText("OK")
                            .show();

                    resetKomponen();
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(AddKesehatanActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Terjadi kesalahan")
                            .setTitleText("Oops..")
                            .setConfirmText("OK")
                            .show();
                    Log.d("erorUpload:","erorGambar "+task.getException().toString());
                }
            }
        });
    }

    private void resetKomponen(){
        etSolusi.setText("");
        etDeskripsi.setText("");
        etJudul.setText("");
    }
}
