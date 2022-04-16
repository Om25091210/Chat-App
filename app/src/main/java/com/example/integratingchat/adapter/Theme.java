package com.example.integratingchat.adapter;

import com.example.integratingchat.Coustomize;
import com.example.integratingchat.R;
import com.example.integratingchat.data.StaticConfig;
import com.example.integratingchat.ui.ChatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Theme extends RecyclerView.Adapter<Theme.NoteViewHolder> {

    ArrayList<String> gradients;
    Context context;
    String nameFriend="";
    String roomId="";
    String idFriend="";

    public Theme(ArrayList<String> grad_list,String nameFriend,String roomId,String idFriend, Coustomize coustomize) {
        this.gradients=grad_list;
        this.context=coustomize;
        this.nameFriend=nameFriend;
        this.roomId=roomId;
        this.idFriend=idFriend;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout_chat_theme,parent, false);
        return new NoteViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        if(gradients.get(position).equals("Inlfuenza")){
            holder.gradient.setBackgroundResource(R.drawable.grad_one);
            holder.gradTitle.setText("Inlfuenza");
        }
        if(gradients.get(position).equals("Nelson")){
            holder.gradient.setBackgroundResource(R.drawable.grad_two);
            holder.gradTitle.setText("Nelson");
        }
        if(gradients.get(position).equals("Lemon Twist")){
            holder.gradient.setBackgroundResource(R.drawable.grad_three);
            holder.gradTitle.setText("Lemon Twist");
        }
        if(gradients.get(position).equals("Hazel")){
            holder.gradient.setBackgroundResource(R.drawable.grad_four);
            holder.gradTitle.setText("Hazel");
        }
        if(gradients.get(position).equals("Blurry Beach")){
            holder.gradient.setBackgroundResource(R.drawable.grad_five);
            holder.gradTitle.setText("Blurry Beach");
        }
        if(gradients.get(position).equals("Misty Meadow")){
            holder.gradient.setBackgroundResource(R.drawable.grad_six);
            holder.gradTitle.setText("Misty Meadow");
        }
        if(gradients.get(position).equals("Dance To Forget")){
            holder.gradient.setBackgroundResource(R.drawable.grad_seven);
            holder.gradTitle.setText("Dance To Forget");
        }
        if(gradients.get(position).equals("Star Fall")){
            holder.gradient.setBackgroundResource(R.drawable.grad_eight);
            holder.gradTitle.setText("Star Fall");
        }
        if(gradients.get(position).equals("Man Of Steel")){
            holder.gradient.setBackgroundResource(R.drawable.grad_nine);
            holder.gradTitle.setText("Man Of Steel");
        }
        if(gradients.get(position).equals("Purple Bliss")){
            holder.gradient.setBackgroundResource(R.drawable.grad_ten);
            holder.gradTitle.setText("Purple Bliss");
        }
        if(gradients.get(position).equals("Ali")){
            holder.gradient.setBackgroundResource(R.drawable.grad_eleven);
            holder.gradTitle.setText("Ali");
        }
        if(gradients.get(position).equals("Deep Sea Space")){
            holder.gradient.setBackgroundResource(R.drawable.grad_tweleve);
            holder.gradTitle.setText("Deep Sea Space");
        }
        if(gradients.get(position).equals("Royal")){
            holder.gradient.setBackgroundResource(R.drawable.grad_thirteen);
            holder.gradTitle.setText("Royal");
        }
        if(gradients.get(position).equals("Mauve")){
            holder.gradient.setBackgroundResource(R.drawable.grad_fourteen);
            holder.gradTitle.setText("Mauve");
        }
        if(gradients.get(position).equals("Jupiter")){
            holder.gradient.setBackgroundResource(R.drawable.grad_fifteen);
            holder.gradTitle.setText("Jupiter");
        }
        if(gradients.get(position).equals("AstroGrad")){
            holder.gradient.setBackgroundResource(R.drawable.grad_sixteen);
            holder.gradTitle.setText("AstroGrad");
        }
        if(gradients.get(position).equals("Nehozure")){
            holder.gradient.setBackgroundResource(R.drawable.grad_seventeen);
            holder.gradTitle.setText("Nehozure");
        }
        if(gradients.get(position).equals("cosmic Fusion")){
            holder.gradient.setBackgroundResource(R.drawable.grad_eighteen);
            holder.gradTitle.setText("cosmic Fusion");
        }
        if(gradients.get(position).equals("The Blue Lagoon")){
            holder.gradient.setBackgroundResource(R.drawable.grad_nineteen);
            holder.gradTitle.setText("The Blue Lagoon");
        }
        if(gradients.get(position).equals("Feel Tonight")){
            holder.gradient.setBackgroundResource(R.drawable.grad_twenty);
            holder.gradTitle.setText("Feel Tonight");
        }
        if(gradients.get(position).equals("CinnaMint")){
            holder.gradient.setBackgroundResource(R.drawable.grad_twenty_one);
            holder.gradTitle.setText("CinnaMint");
        }
        if(gradients.get(position).equals("Visions of Grandeur")){
            holder.gradient.setBackgroundResource(R.drawable.grad_twenty_two);
            holder.gradTitle.setText("Visions of Grandeur");
        }
        if(gradients.get(position).equals("Crystal Clear")){
            holder.gradient.setBackgroundResource(R.drawable.grad_twenty_three);
            holder.gradTitle.setText("Crystal Clear");
        }
        if(gradients.get(position).equals("Relay")){
            holder.gradient.setBackgroundResource(R.drawable.grad_twenty_four);
            holder.gradTitle.setText("Relay");
        }
        if(gradients.get(position).equals("Terminal")){
            holder.gradient.setBackgroundResource(R.drawable.grad_twenty_five);
            holder.gradTitle.setText("Terminal");
        }
        if(gradients.get(position).equals("")){
            holder.gradient.setBackgroundResource(R.drawable.rounded_corner1);
            holder.gradTitle.setText("Default");
        }



        holder.cardgrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+gradients.get(position), Toast.LENGTH_SHORT).show();
                context.getSharedPreferences("gradientStorage",Context.MODE_PRIVATE).edit()
                        .putString("gradientName_",gradients.get(position)).apply();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, nameFriend);
                        ArrayList<CharSequence> id = new ArrayList<CharSequence>();
                        id.add(idFriend);
                        intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, id);
                        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, roomId);
                        context.startActivity(intent);

                        Coustomize.coustomize.finish();

                    }
                },500);

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return gradients.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView gradTitle;
        CircleImageView gradient;
        CardView cardgrad;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            gradient=itemView.findViewById(R.id.rounded_grad);
            gradTitle=itemView.findViewById(R.id.textView3);
            cardgrad=itemView.findViewById(R.id.cardgrad);

        }
    }

}

