package com.example.integratingchat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.integratingchat.Coustomize;
import com.example.integratingchat.MessageSwipeController;
import com.example.integratingchat.Specific;
import com.example.integratingchat.SwipeControllerActions;
import com.example.integratingchat.ViewImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.integratingchat.R;
import com.example.integratingchat.data.SharedPreferenceHelper;
import com.example.integratingchat.data.StaticConfig;
import com.example.integratingchat.model.Consersation;
import com.example.integratingchat.model.Message;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;



public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Consersation consersation;
    private ImageButton btnSend;
    private EditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;
    ImageView takePicture;
    ImageView sendpic;
    boolean connected = false;
    Bitmap bitmap;
    private String profilepath;
    String filelink="";
    ImageView scrolldown;
    String avatar="";
    String global_reply="",global_reply_position="";
    String path="";
    View rootView;
    LinearLayout replylayout;
    boolean isKeyboardShowing = false;
    String nameFriend="";
    String token="";
    String saved_token="";
    String get_the_number_of_unseen_messages="";
    String username="";
    String dp="";
    boolean get_the_token_once=true;
    boolean scroll_for_new_messages=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootView = findViewById(R.id.root_view);
        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID);

        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        nameFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);

        get_the_number_of_unseen_messages=intentData.getStringExtra("Get_the_number_of_messages");

        dp=intentData.getStringExtra("dp of user");


        Window window = ChatActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(ChatActivity.this, R.color.bg));
        window.setNavigationBarColor(ContextCompat.getColor(ChatActivity.this, R.color.black));

        if(!isNetworkAvailable()){
            try {

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setCancelable(false);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(!isNetworkAvailable()){
                            alertDialog.dismiss();
                        }
                        else{
                            alertDialog.dismiss();
                        }

                    }
                });

                alertDialog.show();
            } catch (Exception e) {
                Log.e("network","failed");
            }
        }

        consersation = new Consersation();
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        replylayout=findViewById(R.id.replylayout);
        replylayout.setVisibility(View.GONE);



        scrolldown=findViewById(R.id.scrolldown);
        scrolldown.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference().child("user").child(idFriend.get(0).toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(get_the_token_once) {
                    token = snapshot.child("DeviceToken").getValue(String.class);

                    getSharedPreferences("SavingToken", MODE_PRIVATE).edit()
                            .putString("Its_Token", token).apply();
                    get_the_token_once=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        username=getSharedPreferences("Saving_Name_of_user", MODE_PRIVATE)
                .getString("Its_user", "");

// rootview is the root view of the layout of this activity/fragment
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rootView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        Log.d("Height", "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
          });

        sendpic=findViewById(R.id.imageView4);
        sendpic.setVisibility(View.GONE);

        takePicture=findViewById(R.id.takePicture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("Dhiti")
                        .setAspectRatio(1,1) //You can skip this for free form aspect ratio)
                        .start(ChatActivity.this);
            }
        });

        String base64AvataUser = SharedPreferenceHelper.getInstance(this).getUserInfo().avata;
        if (!base64AvataUser.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            avatar=base64AvataUser;
            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataUser = null;
        }

        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);

        if (idFriend != null && nameFriend != null) {
            getSupportActionBar().setTitle(nameFriend);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            recyclerChat.setItemViewCacheSize(50);
            saved_token= getSharedPreferences("SavingToken", MODE_PRIVATE)
                    .getString("Its_Token", "");
            adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, bitmapAvataUser,null,recyclerChat,saved_token,nameFriend,username,idFriend.get(0).toString(),get_the_number_of_unseen_messages);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        for(int i=1;i<=2;i++) {
                            linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            recyclerChat.setAdapter(adapter);

            scrolldown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i=0;i<=1;i++) {
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
                        scrolldown.setVisibility(View.GONE);
                    }
                }
            });

            MessageSwipeController messageSwipeController=new MessageSwipeController(this, new SwipeControllerActions() {
                @Override
                public void showReplyUI(int position) {
                    adapter.fun_trigger(position);
                }
            });

            ItemTouchHelper itemTouchHelper= new ItemTouchHelper(messageSwipeController);
            itemTouchHelper.attachToRecyclerView(recyclerChat);

            recyclerChat.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1)) {
                        scrolldown.setVisibility(View.GONE);
                    }
                    else {
                        scrolldown.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {

                    } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                    } else {
                        // Do something
                    }
                }
            });
        }
    }
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            return connected;
        }
        else {
            connected = false;
            return connected;
        }
    }

    void trigger_fun(Context context, String replying_text, String nameFriend,String position){

        ((Activity)context).getSharedPreferences("reply",MODE_PRIVATE).edit()
                .putString("replying_text",replying_text).apply();

        ((Activity)context).getSharedPreferences("position",MODE_PRIVATE).edit()
                .putString("position_of_text",position).apply();

        LinearLayout replylayout=((Activity)context).findViewById(R.id.replylayout);
        replylayout.setVisibility(View.VISIBLE);

        TextView replytext=((Activity)context).findViewById(R.id.replytext);
        TextView userreply=((Activity)context).findViewById(R.id.userreply);

        EditText editText=((Activity)context).findViewById(R.id.editWriteMessage);
        editText.requestFocus();

        ImageView cancel=((Activity)context).findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replylayout.setVisibility(View.GONE);

                ((Activity)context).getSharedPreferences("reply",MODE_PRIVATE).edit()
                        .putString("replying_text","").apply();

                ((Activity)context).getSharedPreferences("position",MODE_PRIVATE).edit()
                        .putString("position_of_text","").apply();
            }
        });
        replytext.setText(replying_text);
        userreply.setText(nameFriend);

    }
    void scroll_up(Context context, String position,String from_where){
        if(from_where.equals("new_messages") && scroll_for_new_messages){

            if(position!=null) {
                RecyclerView recyclerChat = ((Activity) context).findViewById(R.id.recyclerChat);
                recyclerChat.setItemViewCacheSize(50);
                recyclerChat.smoothScrollToPosition(recyclerChat.getAdapter().getItemCount() - Integer.parseInt(position));
                scroll_for_new_messages = false;
            }
        }
        else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerChat = ((Activity) context).findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            linearLayoutManager.scrollToPosition(Integer.parseInt(position));
            linearLayoutManager.findViewByPosition(Integer.parseInt(position));
        }

    }
    void onKeyboardVisibilityChanged(boolean opened) {
        scrolldown.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                // Set uri as Image in the ImageView:
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , Uri.parse(String.valueOf(resultUri)));
                    sendpic.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendpic.setImageBitmap(bitmap);
                sendpic.invalidate();
                BitmapDrawable drawable6 = (BitmapDrawable) sendpic.getDrawable();
                Bitmap bitmap6 = drawable6.getBitmap();
                getImageUri(getApplicationContext(), bitmap6);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        final String randomKey= UUID.randomUUID().toString();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ""+randomKey, null);
        return Uri.parse(path);
    }
    private void uploadPicture(Uri parse) {

        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();
        final String randomKey= UUID.randomUUID().toString();
        profilepath="picture/"+randomKey+".png";
        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child(profilepath);

        riversRef.putFile(parse)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        filelink = task.getResult().toString();
                                        //TODO: data structure must be added.
                                        Message newMessage = new Message();
                                        newMessage.text=filelink;
                                        newMessage.idSender = StaticConfig.UID;
                                        newMessage.idReceiver = roomId;
                                        newMessage.seen="false";
                                        newMessage.timestamp = System.currentTimeMillis();
                                        newMessage.pushkey=FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().getKey();
                                        FirebaseDatabase.getInstance().getReference().child("message/" + roomId).child(newMessage.pushkey).setValue(newMessage);

                                        long delayInMillis = 1000;
                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                pd.dismiss();
                                            }
                                        }, delayInMillis);
                                    }
                                });
                        Snackbar.make(findViewById(android.R.id.content),"Image Uploaded.",Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                double progressPercent=(100.00 * tasksnapshot.getBytesTransferred() / tasksnapshot.getTotalByteCount());
                pd.setMessage("Progress: "+(int) progressPercent+"%");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent coustomize=new Intent(ChatActivity.this, Coustomize.class);
            coustomize.putExtra("sendingidoffriend",nameFriend);
            coustomize.putExtra("avatarOFfriend2509",avatar);
            coustomize.putExtra("roomIdoffriend",roomId);
            coustomize.putExtra("friendIDoffriend",idFriend.get(0).toString());
            startActivity(coustomize);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend.get(0));
        setResult(RESULT_OK, result);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) {
            global_reply=getSharedPreferences("reply",MODE_PRIVATE)
                    .getString("replying_text","");

            global_reply_position=getSharedPreferences("position",MODE_PRIVATE)
                    .getString("position_of_text","");


            getSharedPreferences("reply",MODE_PRIVATE).edit()
                    .putString("replying_text","").apply();

            getSharedPreferences("position",MODE_PRIVATE).edit()
                    .putString("position_of_text","").apply();

            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                LinearLayout replylayout=findViewById(R.id.replylayout);
                replylayout.setVisibility(View.GONE);
                editWriteMessage.setText("");
                Message newMessage = new Message();
                newMessage.text = content;
                newMessage.idSender = StaticConfig.UID;
                newMessage.idReceiver = roomId;
                newMessage.reply=global_reply;
                newMessage.position=global_reply_position;
                newMessage.name=username;
                newMessage.seen="false";
                newMessage.timestamp = System.currentTimeMillis();
                newMessage.pushkey=FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().getKey();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).child(newMessage.pushkey).setValue(newMessage);
                adapter.notifyDataSetChanged();
                scrolldown.setVisibility(View.GONE);

            }
            if(!path.isEmpty()){
                uploadPicture(Uri.parse(path));
            }
        }
    }
}


class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Consersation consersation;
    private HashMap<String, Bitmap> bitmapAvata;
    private HashMap<String, DatabaseReference> bitmapAvataDB;
    private Bitmap bitmapAvataUser;
    String timestamp_previous;
    Bitmap imageSend;
    String timestamp_previous_user="";
    private static int SPLASH_TEXT_TIME_OUT=2000;
    DatabaseReference message;
    String name_of_friend;
    String token_of_friend;
    String text;
    int once=0;
    DatabaseReference notification;
    Dialog dialogAnimateds;
    Button delete;
    String username="";
    RecyclerView recyclerView=null;
    boolean connected = false;
    boolean check=false;
    String uuid_of_friend="";
    String get_the_number_of_unseen_messages="";
    Boolean show_messages_first_time=true;
    ArrayList<Long> key_unseen_last=new ArrayList<Long>();
    ArrayList<String> key_last=new ArrayList<String>();
    boolean scroll_new_messages_first=true;


    public ListMessageAdapter(Context context, Consersation consersation, HashMap<String, Bitmap> bitmapAvata, Bitmap bitmapAvataUser, Bitmap bitmap, RecyclerView recyclerChat, String token, String nameFriend,String username,String UUID_OF_FRIEND,String get_the_number_of_unseen_messages) {
        this.context = context;
        this.consersation = consersation;
        this.bitmapAvata = bitmapAvata;
        this.bitmapAvataUser = bitmapAvataUser;
        this.imageSend = bitmap;
        this.recyclerView=recyclerChat;
        this.name_of_friend=nameFriend;
        this.token_of_friend=token;
        this.username=username;
        this.get_the_number_of_unseen_messages=get_the_number_of_unseen_messages;
        this.uuid_of_friend=UUID_OF_FRIEND;
        bitmapAvataDB = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        notification=FirebaseDatabase.getInstance().getReference().child("user").child(uuid_of_friend).child("Notification");

        if(!isNetworkAvailable() && !check){
            Toast.makeText(context, "No Network Detected!", Toast.LENGTH_SHORT).show();
            check=true;
        }

        if (holder instanceof ItemMessageFriendHolder) {

            if(scroll_new_messages_first){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChatActivity chatActivity = new ChatActivity();
                        chatActivity.scroll_up(context, get_the_number_of_unseen_messages, "new_messages");
                        scroll_new_messages_first=false;
                    }
                },1500);
            }

            ((ItemMessageFriendHolder) holder).newmessages.setVisibility(View.GONE);
            //((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);

            Glide.with(context).load(consersation.getListMessageData().get(position).text).into(((ItemMessageFriendHolder) holder).imageView);
            if(consersation.getListMessageData().get(position).text.contains("https://firebasestorage.googleapis.com")){
                ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
            }else{
                ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.VISIBLE);
            }

            //implements image click to view.
            ((ItemMessageFriendHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendPic_for_view=new Intent(context, ViewImage.class);
                    sendPic_for_view.putExtra("sending_pic_for_view",consersation.getListMessageData().get(position).text);
                    context.startActivity(sendPic_for_view);
                    //TODO: animation between activity
                }
            });


            message=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
            message.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot key:snapshot.getChildren()) {
                        Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                        String reply=snapshot.child(key.getKey()).child("reply").getValue(String.class);
                        String text=snapshot.child(key.getKey()).child("text").getValue(String.class);
                        String position1=snapshot.child(key.getKey()).child("position").getValue(String.class);
                        String seen_or_not=snapshot.child(key.getKey()).child("seen").getValue(String.class);
                        if (!consersation.getListMessageData().get(position).idSender.equals(StaticConfig.UID)) {
                            if (time_st==(consersation.getListMessageData().get(position).timestamp)) {
                                assert seen_or_not != null;
                                if(seen_or_not.equals("false")){

                                        if(!key_unseen_last.contains(time_st)) {
                                              key_unseen_last.add(time_st);
                                        }

                                }
                                Message newMessage = new Message();
                                newMessage.text = text;
                                newMessage.idSender = consersation.getListMessageData().get(position).idSender;
                                newMessage.idReceiver = consersation.getListMessageData().get(position).idReceiver;
                                newMessage.timestamp = consersation.getListMessageData().get(position).timestamp;
                                newMessage.seen = "true";
                                newMessage.reply=reply;
                                newMessage.name=name_of_friend;
                                newMessage.position=position1;
                                newMessage.pushkey = key.getKey();
                                FirebaseDatabase.getInstance().getReference().child("message/" + newMessage.idReceiver).child(key.getKey()).setValue(newMessage);

                            }
                        }
                    }
                    try {
                        if (Integer.parseInt(get_the_number_of_unseen_messages) == key_unseen_last.size()) {
                            if(show_messages_first_time) {
                                Log.e("mesgsdf", key_unseen_last.get(key_unseen_last.size() - 1) + "");
                                if (key_unseen_last.get(key_unseen_last.size() - 1) == (consersation.getListMessageData().get(position).timestamp)) {
                                    ((ItemMessageFriendHolder) holder).newmessages.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ((ItemMessageFriendHolder) holder).reply.setVisibility(View.GONE);

            message=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
            message.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot key:snapshot.getChildren()) {
                        Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                        String reply=snapshot.child(key.getKey()).child("reply").getValue(String.class);

                        if (time_st==(consersation.getListMessageData().get(position).timestamp)) {
                            if(!reply.equals("")) {
                                ((ItemMessageFriendHolder) holder).reply.setVisibility(View.VISIBLE);
                                ((ItemMessageFriendHolder) holder).reply.setText(reply);
                            }
                            else{
                                ((ItemMessageFriendHolder) holder).reply.setVisibility(View.GONE);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            /*//implementing click listeners on texts
            ((ItemMessageFriendHolder) holder).txtContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
                    message.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot key:snapshot.getChildren()) {
                                Long time_st = snapshot.child(Objects.requireNonNull(key.getKey())).child("timestamp").getValue(Long.class);
                                String reply=snapshot.child(key.getKey()).child("reply").getValue(String.class);
                                String position1=snapshot.child(key.getKey()).child("position").getValue(String.class);
                                if (time_st==(consersation.getListMessageData().get(position).timestamp)) {
                                    if(!reply.equals("")) {

                                        ChatActivity chatActivity=new ChatActivity();
                                        chatActivity.scroll_up(context,position1,"");

                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });*/

            try {
                timestamp_previous = String.valueOf(consersation.getListMessageData().get(position - 1).timestamp);
            } catch (IndexOutOfBoundsException e) {
                timestamp_previous=String.valueOf("1611033741411");
            }
            Timestamp stamp_previous = new Timestamp(Long.parseLong(timestamp_previous));
            Date date_previous = new Date(stamp_previous.getTime());
            String str_previous=date_previous+"";
            String sub_11_previous=str_previous.substring(0,10);
            String sub_111_previous=str_previous.substring(30,str_previous.length());
            String extracted_date_previous=sub_11_previous+" "+sub_111_previous;


            String timestamp=String.valueOf(consersation.getListMessageData().get(position).timestamp);
            Timestamp stamp = new Timestamp(Long.parseLong(timestamp));
            Date date = new Date(stamp.getTime());
            String str=date+"";
            String sub_1=str.substring(11,16);
            ((ItemMessageFriendHolder) holder).time.setText(sub_1.trim());//==================================
            String sub_11=str.substring(0,10);
            String sub_111=str.substring(30,str.length());
            String extracted_date=sub_11+" "+sub_111;

            ((ItemMessageFriendHolder) holder).date_of_chat.setVisibility(View.GONE);
            Date today = new Date();
            String todays_date=today+"";
            String sub_2=todays_date.substring(0,10);
            String sub_22=todays_date.substring(30,todays_date.length());
            String extracted_current_date=sub_2+" "+sub_22;

            if(!extracted_date_previous.equals(extracted_date)){
                if(extracted_current_date.equals(extracted_date)){
                    ((ItemMessageFriendHolder) holder).date_of_chat.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).date_of_chat.setText("Today");
                }
                else{
                    ((ItemMessageFriendHolder) holder).date_of_chat.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).date_of_chat.setText(extracted_date);
                }
            }

            Bitmap currentAvata = bitmapAvata.get(consersation.getListMessageData().get(position).idSender);
            if (currentAvata != null) {
                ((ItemMessageFriendHolder) holder).avata.setImageBitmap(currentAvata);
            } else {
                final String id = consersation.getListMessageData().get(position).idSender;
                if(bitmapAvataDB.get(id) == null){
                    bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                    bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String avataStr = (String) dataSnapshot.getValue();
                                if(!avataStr.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                    byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                    ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                }else{
                                    ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avata));
                                }
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot key:snapshot.getChildren()){
                        Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                        if (time_st==(consersation.getListMessageData().get(position).timestamp)){
                            String text=snapshot.child(key.getKey()).child("text").getValue(String.class);
                            ((ItemMessageFriendHolder) holder).txtContent.setText(text);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(!isNetworkAvailable()){
                ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            }

        } else if (holder instanceof ItemMessageUserHolder) {

            String chat_color=context.getSharedPreferences("gradientStorage",Context.MODE_PRIVATE)
                    .getString("gradientName_","");

            //TODO:different colour theme different chat text colour and time colour.
            if(chat_color.equals("Inlfuenza")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_one);
            }
            if(chat_color.equals("Nelson")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_two);
            }
            if(chat_color.equals("Lemon Twist")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_three);
            }
            if(chat_color.equals("Hazel")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_four);
            }
            if(chat_color.equals("Blurry Beach")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_five);
            }
            if(chat_color.equals("Misty Meadow")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_six);
            }
            if(chat_color.equals("Dance To Forget")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_seven);
            }
            if(chat_color.equals("Star Fall")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_eight);
            }
            if(chat_color.equals("Man Of Steel")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_nine);
            }
            if(chat_color.equals("Purple Bliss")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_ten);
            }
            if(chat_color.equals("Ali")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_eleven);
            }
            if(chat_color.equals("Deep Sea Space")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_tweleve);
            }
            if(chat_color.equals("Royal")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_thirteen);
            }
            if(chat_color.equals("Mauve")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_fourteen);
            }
            if(chat_color.equals("Jupiter")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_fifteen);
            }
            if(chat_color.equals("AstroGrad")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_sixteen);
            }
            if(chat_color.equals("Nehozure")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_seventeen);
            }
            if(chat_color.equals("cosmic Fusion")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_eighteen);
            }
            if(chat_color.equals("The Blue Lagoon")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_nineteen);
            }
            if(chat_color.equals("Feel Tonight")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_twenty);
            }
            if(chat_color.equals("CinnaMint")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_twenty_one);
            }
            if(chat_color.equals("Visions of Grandeur")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_twenty_two);
            }
            if(chat_color.equals("Crystal Clear")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_twenty_three);
            }
            if(chat_color.equals("Relay")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_twenty_four);
            }
            if(chat_color.equals("Terminal")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.grad_twenty_five);
            }
            if(chat_color.equals("")){

                ((ItemMessageUserHolder) holder).linearLayout2.setBackgroundResource(R.drawable.rounded_corner1);
            }


            ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);

            Glide.with(context).load(consersation.getListMessageData().get(position).text).into(((ItemMessageUserHolder) holder).imageView);
            if(consersation.getListMessageData().get(position).text.contains("https://firebasestorage.googleapis.com")){
                ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
            }else{
                ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.VISIBLE);
            }
       /*     if (bitmapAvataUser != null) {
                ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);
            }*/


            try {
                timestamp_previous_user=String.valueOf(consersation.getListMessageData().get(position-1).timestamp);
            } catch (IndexOutOfBoundsException e) {
                timestamp_previous_user=String.valueOf("1611033741411");
            }
            Timestamp stamp_previous = new Timestamp(Long.parseLong(timestamp_previous_user));
            Date date_previous = new Date(stamp_previous.getTime());
            String str_previous=date_previous+"";
            String sub_11_previous=str_previous.substring(0,10);
            String sub_111_previous=str_previous.substring(30,str_previous.length());
            String extracted_date_previous=sub_11_previous+" "+sub_111_previous;


            String timestamp=String.valueOf(consersation.getListMessageData().get(position).timestamp);
            Timestamp stamp = new Timestamp(Long.parseLong(timestamp));
            Date date = new Date(stamp.getTime());
            String str=date+"";
            String sub_1=str.substring(11,16);
            ((ItemMessageUserHolder) holder).time.setText(sub_1.trim());//==================================
            String sub_11=str.substring(0,10);
            String sub_111=str.substring(30,str.length());
            String extracted_date=sub_11+" "+sub_111;

            ((ItemMessageUserHolder) holder).date_of_chat.setVisibility(View.GONE);
            Date today = new Date();
            String todays_date=today+"";
            String sub_2=todays_date.substring(0,10);
            String sub_22=todays_date.substring(30,todays_date.length());
            String extracted_current_date=sub_2+" "+sub_22;

            if(!extracted_date_previous.equals(extracted_date)){
                if(extracted_current_date.equals(extracted_date)){
                    ((ItemMessageUserHolder) holder).date_of_chat.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).date_of_chat.setText("Today");
                }
                else{
                    ((ItemMessageUserHolder) holder).date_of_chat.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).date_of_chat.setText(extracted_date);
                }
            }


                ((ItemMessageUserHolder) holder).txtContent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!((ItemMessageUserHolder) holder).txtContent.getText().toString().equals("This message was deleted")) {
                            dialogAnimateds = new Dialog(context, R.style.dialogstyletick);
                            dialogAnimateds.setContentView(R.layout.dialof_delete);
                            dialogAnimateds.show();
                            delete = dialogAnimateds.findViewById(R.id.button2);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot key : snapshot.getChildren()) {
                                                Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                                                if (time_st == (consersation.getListMessageData().get(position).timestamp)) {
                                                    String id_receiver = snapshot.child(key.getKey()).child("idReceiver").getValue(String.class);
                                                    String id_sender = snapshot.child(key.getKey()).child("idSender").getValue(String.class);
                                                    String pushkey = snapshot.child(key.getKey()).child("pushkey").getValue(String.class);
                                                    String seen = snapshot.child(key.getKey()).child("seen").getValue(String.class);

                                                    Message setMessage = new Message();
                                                    setMessage.idReceiver = id_receiver;
                                                    setMessage.idSender = id_sender;
                                                    setMessage.position = "";
                                                    setMessage.pushkey = pushkey;
                                                    setMessage.reply = "";
                                                    setMessage.seen = seen;
                                                    setMessage.timestamp = time_st;
                                                    setMessage.name=username;
                                                    setMessage.text = "This message was deleted";

                                                    reference.child(key.getKey()).setValue(setMessage);
                                                    consersation.getListMessageData().remove(position);
                                                    consersation.getListMessageData().add(position, setMessage);
                                                    notifyDataSetChanged();
                                                    dialogAnimateds.dismiss();

                                                    break;

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        }
                        return false;
                    }
                });

            message=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
            message.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot key:snapshot.getChildren()) {
                        Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                        String text=snapshot.child(key.getKey()).child("text").getValue(String.class);
                        if (consersation.getListMessageData().get(position).idSender.equals(StaticConfig.UID)) {
                            if (time_st==(consersation.getListMessageData().get(position).timestamp)) {
                                String seen=snapshot.child(key.getKey()).child("seen").getValue(String.class);
                                if(seen.equals("true")){
                                    ((ItemMessageUserHolder) holder).greytick.setVisibility(View.GONE);
                                    ((ItemMessageUserHolder) holder).bluetick.setVisibility(View.VISIBLE);
                                }else{
                                    ((ItemMessageUserHolder) holder).greytick.setVisibility(View.VISIBLE);
                                    ((ItemMessageUserHolder) holder).bluetick.setVisibility(View.GONE);

                                    key_last.clear();
                                    key_last.add(key.getKey());

                                    //TODO:list hoga

                                    if(token_of_friend!=null) {
                                        Specific specific = new Specific();
                                        specific.noti(consersation.getListMessageData().get(position).idReceiver,name_of_friend,key.getKey(), token_of_friend);
                                    }

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ((ItemMessageUserHolder) holder).reply.setVisibility(View.GONE);

            message=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
            message.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot key:snapshot.getChildren()) {
                        Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                        String reply=snapshot.child(key.getKey()).child("reply").getValue(String.class);
                        String position1=snapshot.child(key.getKey()).child("position").getValue(String.class);
                        if (time_st==(consersation.getListMessageData().get(position).timestamp)) {
                            if(!reply.equals("")) {
                                ((ItemMessageUserHolder) holder).reply.setVisibility(View.VISIBLE);
                                ((ItemMessageUserHolder) holder).reply.setText(reply);
                            }
                            else{
                                ((ItemMessageUserHolder) holder).reply.setVisibility(View.GONE);
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            /*//implementing click listeners on texts
            ((ItemMessageUserHolder) holder).txtContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message=FirebaseDatabase.getInstance().getReference().child("message/" + consersation.getListMessageData().get(position).idReceiver);
                    message.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot key:snapshot.getChildren()) {
                                Long time_st = snapshot.child(key.getKey()).child("timestamp").getValue(Long.class);
                                String reply=snapshot.child(key.getKey()).child("reply").getValue(String.class);
                                String position1=snapshot.child(key.getKey()).child("position").getValue(String.class);
                                if (time_st==(consersation.getListMessageData().get(position).timestamp)) {
                                    if(!reply.equals("")) {

                                        ChatActivity chatActivity=new ChatActivity();
                                        chatActivity.scroll_up(context,position1,"");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });*/

            //implements image click to view.
            ((ItemMessageUserHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent sendPic_for_view=new Intent(context, ViewImage.class);
                   sendPic_for_view.putExtra("sending_pic_for_view",consersation.getListMessageData().get(position).text);
                   context.startActivity(sendPic_for_view);

                }
            });

            if(!isNetworkAvailable()){
                ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            }
        }

    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            return connected;
        }
        else {
            connected = false;
            return connected;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return consersation.getListMessageData().get(position).idSender.equals(StaticConfig.UID) ? ChatActivity.VIEW_TYPE_USER_MESSAGE : ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return consersation.getListMessageData().size();
    }

    void fun_trigger(int position) {

        text = consersation.getListMessageData().get(position).text;
        if (!text.equals("This message was deleted")) {

            if (text.contains("https://firebasestorage.googleapis.com")) {
                text = "photo";
            }
            DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("user");
            user.child(consersation.getListMessageData().get(position).idSender).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name_of_friend = snapshot.getValue(String.class);
                    ChatActivity chatActivity = new ChatActivity();
                    chatActivity.trigger_fun(context, text, name_of_friend, position + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent,reply;
    public CircleImageView avata;
    public RoundedImageView imageView;
    public ImageView greytick;
    public ImageView bluetick;
    TextView time,date_of_chat;
    ConstraintLayout relativeLayout;
    LinearLayout linearLayout2;

    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
        imageView=itemView.findViewById(R.id.imageView);
        greytick=itemView.findViewById(R.id.greytick);
        bluetick=itemView.findViewById(R.id.bluetick);
        time=itemView.findViewById(R.id.time);
        date_of_chat=itemView.findViewById(R.id.date_of_chat);
        reply=itemView.findViewById(R.id.reply);
        relativeLayout=itemView.findViewById(R.id.relativeLayout);
        linearLayout2=itemView.findViewById(R.id.linearLayout2);

    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;
    public RoundedImageView imageView;
    TextView time,date_of_chat,reply;
    ImageView newmessages;
    ConstraintLayout relativeLayout;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView3);
        imageView=itemView.findViewById(R.id.imageView);
        newmessages=itemView.findViewById(R.id.imageView2);
        time=itemView.findViewById(R.id.time);
        date_of_chat=itemView.findViewById(R.id.date_of_chat);
        reply=itemView.findViewById(R.id.reply);
        relativeLayout=itemView.findViewById(R.id.relativeLayout);

    }
}
