package com.example.max.chatwithnotifications;

import android.content.RestrictionEntry;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Act extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messengerTextView = itemView.findViewById(R.id.messengerTextView);
            messengerImageView = itemView.findViewById(R.id.messengerImageView);
        }
    }

    private static final String TAG = "MainActivity";
    private static final String CHAT_REFERENCE="CHATS";
    private static final String CHAT_INFO_REFERENCE="USER_CHAT_INFO";
    private String MESSAGES_CHILD;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    // Firebase instance variables
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference firebaseChatInfoReference;
    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>
            mFirebaseAdapter;

    //AppUsers
    private AppUser user;
    private ArrayList<AppUser> chatParticipants;

    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);
        Intent intent = getIntent();
        chat=intent.getParcelableExtra("chat");
        this.setTitle(chat.toString());
        chatParticipants=chat.ChatParticipants;
        MESSAGES_CHILD=ChatCreator.makeChatRef(chatParticipants);
        chat.Reference=MESSAGES_CHILD;
        final FirebaseUser us= FirebaseAuth.getInstance().getCurrentUser();
        user = chatParticipants.get(chatParticipants.lastIndexOf(new AppUser(us)));
        chatParticipants.remove(user);


        firebaseChatInfoReference=FirebaseDatabase.getInstance().getReference(CHAT_INFO_REFERENCE);

           /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this , this )
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();*/

            // Initialize ProgressBar and RecyclerView.
            mProgressBar = findViewById(R.id.progressBar);
            mMessageRecyclerView = findViewById(R.id.messageRecyclerView);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(CHAT_REFERENCE);
            SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
                @Override
                public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
                    ChatMessage friendlyMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (friendlyMessage != null) {
                        friendlyMessage.setId(dataSnapshot.getKey());
                    }
                    return friendlyMessage;
                }
            };

            DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
            FirebaseRecyclerOptions<ChatMessage> options =
                    new FirebaseRecyclerOptions.Builder<ChatMessage>()
                            .setQuery(messagesRef, parser)
                            .build();
            mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
                @Override
                public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                    return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
                }

                @Override
                protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                                int position,
                                                ChatMessage friendlyMessage) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (friendlyMessage.getText() != null) {
                        viewHolder.messageTextView.setText(friendlyMessage.getText());
                        viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                        viewHolder.messageImageView.setVisibility(ImageView.GONE);
                    } else {
                        String imageUrl = friendlyMessage.getImageUrl();
                        if (imageUrl.startsWith("gs://")) {
                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(imageUrl);
                            storageReference.getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String downloadUrl = task.getResult().toString();
                                                Glide.with(viewHolder.messageImageView.getContext())
                                                        .load(downloadUrl)
                                                        .into(viewHolder.messageImageView);
                                            } else {
                                                Log.w(TAG, "Getting download url was not successful.",
                                                        task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Glide.with(viewHolder.messageImageView.getContext())
                                    .load(friendlyMessage.getImageUrl())
                                    .into(viewHolder.messageImageView);
                        }
                        viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                        viewHolder.messageTextView.setVisibility(TextView.GONE);
                    }


                    viewHolder.messengerTextView.setText(friendlyMessage.getName());
                    if (friendlyMessage.getPhotoUrl() == null) {
                        viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(Act.this,
                                R.drawable.ic_account_image));
                    } else {
                        Glide.with(Act.this)
                                .load(friendlyMessage.getPhotoUrl())
                                .into(viewHolder.messengerImageView);
                    }

                }
            };

            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                    int lastVisiblePosition =
                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (friendlyMessageCount - 1) &&
                                    lastVisiblePosition == (positionStart - 1))) {
                        mMessageRecyclerView.scrollToPosition(positionStart);
                    }
                }
            });

            mMessageRecyclerView.setAdapter(mFirebaseAdapter);

            mMessageEditText = findViewById(R.id.messageEditText);
            mMessageEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        mSendButton.setEnabled(true);
                    } else {
                        mSendButton.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            mSendButton = findViewById(R.id.sendButton);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChatMessage friendlyMessage = new
                            ChatMessage(mMessageEditText.getText().toString(),
                            user.Name,
                            user.PhotoUrl,
                            null /* no image */);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
                    ArrayList<AppUser> part=new ArrayList<AppUser>();
                    part.add(user);
                    part.addAll(chatParticipants);
                    firebaseChatInfoReference.child(user.Uid)
                    .child(MESSAGES_CHILD)
                            .setValue(new Chat(chat.toString(),MESSAGES_CHILD,
                                    mMessageEditText.getText().toString(),chat.PhotoUrl,part));
                    if(chatParticipants.size()!=1) {
                        chat.ChatParticipants=part;
                        chat.LastMessage = mMessageEditText.getText().toString();
                        updateUsersChatInfo(chat);
                    }
                    else{
                        updateUsersChatInfo(new Chat(user.toString(),MESSAGES_CHILD,
                                mMessageEditText.getText().toString(),user.PhotoUrl,part));
                    }
                    mMessageEditText.setText("");
                }
            });

            mAddMessageImageView = findViewById(R.id.addMessageImageView);
            mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Select image for image message on click.
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            });
    }

    private void updateUsersChatInfo(Chat chat)
    {
        for (AppUser u:
             chatParticipants) {
            firebaseChatInfoReference.child(u.Uid).child(MESSAGES_CHILD).setValue(chat);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        mFirebaseAdapter.startListening();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                UserLoader.signOutFromFirebase(this,this,this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    ChatMessage tempMessage = new ChatMessage(null, user.Name, user.PhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri,final String key) {
        storageReference.putFile(uri).addOnCompleteListener(Act.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ChatMessage friendlyMessage =
                                    new ChatMessage(null, user.Name, user.PhotoUrl,
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
}
