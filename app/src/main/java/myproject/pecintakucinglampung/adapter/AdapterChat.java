package myproject.pecintakucinglampung.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.ChatModel;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyViewHolder> {

    private Context mContext;
    private List<ChatModel> chatList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNamaSender,txtMessage;
        public ImageView ivChatModel;
        public LinearLayout contentWithBackground,content;

        public MyViewHolder(View view) {
            super(view);
            txtNamaSender = (TextView) view.findViewById(R.id.txtNamaSender);
            txtMessage = (TextView) view.findViewById(R.id.txtMessage);
            contentWithBackground = view.findViewById(R.id.contentWithBackground);
            content = view.findViewById(R.id.content);

        }
    }

    public AdapterChat(Context mContext, List<ChatModel> chatList) {
        this.mContext = mContext;
        this.chatList = chatList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("chat");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat_message, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (chatList.isEmpty()){

            Log.d("isiChatModel: ",""+chatList.size());
        }else {
            Resources res = mContext.getResources();
            final ChatModel chatku  = chatList.get(position);

            holder.txtMessage.setText(chatku.getPesan());

            if (chatku.getFromId().equals(SharedVariable.userID)){
                holder.contentWithBackground.setBackground(res.getDrawable(R.drawable.out_message_bg));

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBackground.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.contentWithBackground.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);
                layoutParams = (LinearLayout.LayoutParams) holder.txtNamaSender.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;

                holder.txtNamaSender.setLayoutParams(layoutParams);
                holder.txtNamaSender.setText("Saya");

            }else{
                holder.contentWithBackground.setBackground(res.getDrawable(R.drawable.in_message_bg));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBackground.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.contentWithBackground.setLayoutParams(layoutParams);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.content.setLayoutParams(lp);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);

                layoutParams = (LinearLayout.LayoutParams) holder.txtNamaSender.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtNamaSender.setLayoutParams(layoutParams);
                holder.txtNamaSender.setText(chatku.getNama());

            }




        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return chatList.size();
    }
}
