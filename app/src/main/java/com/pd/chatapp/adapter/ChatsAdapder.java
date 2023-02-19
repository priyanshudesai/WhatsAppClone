package com.pd.chatapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pd.chatapp.R;
import com.pd.chatapp.model.chat.Chats;
import com.pd.chatapp.tools.AudioService;
import com.pd.chatapp.view.MainActivity;
import com.pd.chatapp.view.activities.display.ImageViewerActivity;
import com.pd.chatapp.view.activities.profile.UserProfileActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsAdapder extends RecyclerView.Adapter<ChatsAdapder.ViewHolder> {
    private List<Chats> list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;
    private ImageButton tmpBtnPlay;
    private AudioService audioService;

    private FirebaseAuth mAuth;

    public ChatsAdapder(List<Chats> list, Context context) {
        this.list = list;
        this.context = context;
        this.audioService = new AudioService(context);
    }

    public void setList(List<Chats> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chats chat = list.get(position);

        if (position == list.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

        holder.bind(list.get(position), position, holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage, txt_seen;
        private LinearLayout layoutText, layoutImage, layoutVoice;
        private ImageView imageMessage;
        private ImageButton btnPlay;
        private ViewHolder tmpHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.tv_text_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            layoutImage = itemView.findViewById(R.id.layout_image);
            layoutText = itemView.findViewById(R.id.layout_text);
            imageMessage = itemView.findViewById(R.id.image_chat);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            btnPlay = itemView.findViewById(R.id.btn_play_chat);
        }

        void bind(final Chats chats, final int position, final ViewHolder holder) {
            //Check chat type..

            switch (chats.getType()) {
                case "TEXT":
                    layoutText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);

                    textMessage.setText(chats.getTextMessage());

                    layoutText.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (list.get(position).getSender().equals(firebaseUser.getUid())) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Delete for me",
                                                "Cancel",
                                                "Delete for Everyone"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Delete Message");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        } else if (i == 2) {
                                            deleteMessageForEveryOne(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }else {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Delete for me",
                                                "Cancel"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Delete Message");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            deleteReceiveMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                            return false;
                        }
                    });


                    break;
                case "IMAGE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    layoutVoice.setVisibility(View.GONE);

                    layoutImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                            intent.putExtra("url", list.get(position).getUrl());
                            //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.getContext(), imageProfile, "image");
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });

                    layoutImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (list.get(position).getSender().equals(firebaseUser.getUid())) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Delete for me",
                                                "View This Image",
                                                "Cancel",
                                                "Delete for Everyone"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Delete Message");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        } else if (i == 1) {
                                            Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                            intent.putExtra("url", list.get(position).getUrl());
                                            //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.getContext(), imageProfile, "image");
                                            holder.itemView.getContext().startActivity(intent);
                                        } else if (i == 3) {
                                            deleteMessageForEveryOne(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }else {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Delete for me",
                                                "View This Image",
                                                "Cancel"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Delete Message");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        } else if (i == 1) {
                                            Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                            intent.putExtra("url", list.get(position).getUrl());
                                            //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.getContext(), imageProfile, "image");
                                            holder.itemView.getContext().startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                            return false;
                        }
                    });

                    Glide.with(context).load(chats.getUrl()).into(imageMessage);
                    break;
                case "VOICE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.VISIBLE);

                    layoutVoice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tmpBtnPlay != null) {
                                tmpBtnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                            }

                            btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
                            audioService.playAudioFromUrl(chats.getUrl(), new AudioService.OnPlayCallBack() {
                                @Override
                                public void onFinished() {
                                    btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
                                }
                            });

                            tmpBtnPlay = btnPlay;

                        }
                    });

                    layoutVoice.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (list.get(position).getSender().equals(firebaseUser.getUid())) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Delete for me",
                                                "Cancel",
                                                "Delete for Everyone"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Delete Message");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        } else if (i == 2) {
                                            deleteMessageForEveryOne(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }else {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Delete for me",
                                                "Cancel"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Delete Message");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            deleteReceiveMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                            return false;
                        }
                    });


                    break;
            }

            String messageSenderId = mAuth.getCurrentUser().getUid();
            final Chats messages = list.get(position);


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (list.get(position).getSender().equals(firebaseUser.getUid())) {
                        if (list.get(position).getType().equals("PDF") || list.get(position).getType().equals("DOCX")) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Delete for me",
                                            "Download and View This Document",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (i == 1) {
//                                    Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList));
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                    } else if (i == 3) {

                                        deleteMessageForEveryOne(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        } else if (list.get(position).getType().equals("TEXT") || list.get(position).getType().equals("VOICE")) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Delete for me",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (i == 2) {
                                        deleteMessageForEveryOne(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        } else if (list.get(position).getType().equals("IMAGE")) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Delete for me",
                                            "View This Image",
                                            "Cancel",
                                            "Delete for Everyone"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        deleteSentMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (i == 1) {
                                        Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                        intent.putExtra("url", list.get(position).getUrl());
                                        //ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.getContext(), imageProfile, "image");
                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (i == 3) {
                                        deleteMessageForEveryOne(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                    } else {
                        if (list.get(position).getType().equals("pdf") || list.get(position).getType().equals("docx")) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Delete for me",
                                            "Download and View This Document",
                                            "Cancel"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        deleteReceiveMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (i == 1) {
//                                    Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList));
//                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        } else if (list.get(position).getType().equals("TEXT")  || list.get(position).getType().equals("VOICE")) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Delete for me",
                                            "Cancel"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        deleteReceiveMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        } else if (list.get(position).getType().equals("IMAGE")) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Delete for me",
                                            "View This Image",
                                            "Cancel"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Delete Message");

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        deleteReceiveMessage(position, holder);
//                                        Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (i == 1) {
                                        Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                        intent.putExtra("url", list.get(position).getUrl());
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                }
                            });
                            builder.show();
                        }
                    }
                    return false;
                }
            });

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    private void deleteSentMessage(final int position, final ViewHolder holder) {

        Map messageTextBody = new HashMap();
        messageTextBody.put("sendBoolean", "0");
        //messageTextBody.put("recBoolean", "0");

//        Map messageBodyDetails = new HashMap();
//        messageBodyDetails.put( "Chats" + "/" + list.get(position).getMessageId(), messageTextBody);


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Chats")
                .child(list.get(position).getMessageId())
                .updateChildren(messageTextBody)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteReceiveMessage(final int position, final ViewHolder holder) {

        Map messageTextBody = new HashMap();
//        messageTextBody.put("sendBoolean", "0");
        messageTextBody.put("recBoolean", "0");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Chats")
                .child(list.get(position).getMessageId())
                .updateChildren(messageTextBody)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteMessageForEveryOne(final int position, final ViewHolder holder) {
        Map messageTextBody = new HashMap();
        messageTextBody.put("sendBoolean", "0");
        messageTextBody.put("recBoolean", "0");
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Chats")
                .child(list.get(position).getMessageId())
                .updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    rootRef.child("Messages")
                            .child(list.get(position).getSender())
                            .child(list.get(position).getReceiver())
                            .child(list.get(position).getMessageId())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
