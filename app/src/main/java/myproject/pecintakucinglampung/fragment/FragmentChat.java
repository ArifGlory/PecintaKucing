package myproject.pecintakucinglampung.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import myproject.pecintakucinglampung.MainActivity;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.UbahProfilActivity;
import myproject.pecintakucinglampung.adapter.AdapterChat;
import myproject.pecintakucinglampung.admin.AddDokterActivity;
import myproject.pecintakucinglampung.admin.KelolaDokterActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChat extends Fragment {


    public FragmentChat() {
        // Required empty public constructor
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = firestore.collection("chat");

        etPesan = view.findViewById(R.id.etPesan);
        btnKirim = view.findViewById(R.id.btnKirim);
        rvChat = view.findViewById(R.id.rvChat);

        chatList = new ArrayList<>();
        adapter = new AdapterChat(getActivity(),chatList);

        rvChat.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvChat.setHasFixedSize(true);
        rvChat.setItemAnimator(new DefaultItemAnimator());
        rvChat.setAdapter(adapter);


        pDialogLoading = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
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
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Anda belum menulis pesan")
                            .setTitleText("Oops..")
                            .setConfirmText("OK")
                            .show();
                }
            }
        });

        return view;
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
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        });
    }

    private void getRealtimeUpdate(){
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("ErorChat", "listen:error", e);
                    return;
                }

                chatList.clear();
                adapter.notifyDataSetChanged();
                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    Log.d("TAG", "New Msg: " + dc.getDocument().toObject(ChatModel.class));
                    ChatModel chatku = dc.getDocument().toObject(ChatModel.class);
                    chatList.add(chatku);
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
