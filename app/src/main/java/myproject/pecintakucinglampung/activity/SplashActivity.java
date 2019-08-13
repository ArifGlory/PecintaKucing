package myproject.pecintakucinglampung.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.MainActivity;
import myproject.pecintakucinglampung.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Intent i;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(SplashActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        if (fbUser!=null){
            Log.d("fbUser:","ada fbuser"+fbUser.getDisplayName());
            String token = FirebaseInstanceId.getInstance().getToken();
            SharedVariable.userID = fAuth.getCurrentUser().getUid();
            SharedVariable.nama = fAuth.getCurrentUser().getDisplayName();
            String email = fAuth.getCurrentUser().getEmail();

            if (email.equals("admin@gmail.com")){
                SharedVariable.foto = "no";
                SharedVariable.phone = "no";
                SharedVariable.nama = "Admin PKL";
                SharedVariable.email = fAuth.getCurrentUser().getEmail();
                SharedVariable.userID = fAuth.getUid();

                i = new Intent(SplashActivity.this, HomeAdmin.class);
                startActivity(i);
            }else{
                DocumentReference user = firestore.collection("users").document(SharedVariable.userID);
                user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            String foto = doc.get("foto").toString();
                            String nope = doc.get("nope").toString();

                            SharedVariable.foto = foto;
                            SharedVariable.phone = nope;
                            SharedVariable.email = fAuth.getCurrentUser().getEmail();
                            i = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(i);

                        }else {
                            Log.d("erorGetDatauser:",task.getException().toString());
                            i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erorGetDatauser:",e.toString());
                        i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                });
            }


        }else {
            Log.d("fbUser:","gak ada fbuser");
            i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}
