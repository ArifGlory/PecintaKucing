package myproject.pecintakucinglampung.admin;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import myproject.pecintakucinglampung.Kelas.ChatModel;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.adapter.AdapterChat;

public class ChatActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refUser;
    EditText etPesan;
    ImageButton btnKirim;
    CircleImageView ivUserProfilePhoto;
    LinearLayout lineUbah,lineKeluar;
    FirebaseUser fbUser;
    private FirebaseAuth fAuth;
    RecyclerView rvChat;
    AdapterChat adapter;
    private List<ChatModel> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Firebase.setAndroidContext(ChatActivity.this);
        FirebaseApp.initializeApp(ChatActivity.this);
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = firestore.collection("chat");

        etPesan = findViewById(R.id.etPesan);
        btnKirim = findViewById(R.id.btnKirim);
        rvChat = findViewById(R.id.rvChat);

        chatList = new ArrayList<>();
        adapter = new AdapterChat(ChatActivity.this,chatList);

        rvChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        rvChat.setHasFixedSize(true);
        rvChat.setItemAnimator(new DefaultItemAnimator());
        rvChat.setAdapter(adapter);


        pDialogLoading = new SweetAlertDialog(ChatActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Menampilkan data..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        getChat();

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pesan = etPesan.getText().toString();
                if (pesan != null || pesan.length() > 0){

                    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    ChatModel chatModel = new ChatModel(SharedVariable.userID,
                            "grup",
                            pesan,
                            timeStamp,
                            SharedVariable.nama
                    );
                    chatModel.setIdChat(timeStamp);
                    etPesan.setText("");
                    ref.document(timeStamp).set(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getChat();
                        }
                    });

                }else{
                    new SweetAlertDialog(ChatActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Anda belum menulis pesan")
                            .setTitleText("Oops..")
                            .setConfirmText("OK")
                            .show();
                }
            }
        });
    }

    private void getChat(){
        ref.orderBy("idChat", Query.Direction.DESCENDING).limit(100).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                chatList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        ChatModel chatt = doc.toObject(ChatModel.class);
                        String idChat = doc.get("idChat").toString();
                        chatt.setIdChat(idChat);
                        chatList.add(chatt);
                    }
                    Collections.reverse(chatList);
                    adapter.notifyDataSetChanged();
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ChatActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        });
    }
}
