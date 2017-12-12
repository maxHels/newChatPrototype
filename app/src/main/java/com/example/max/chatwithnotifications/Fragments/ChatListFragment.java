package com.example.max.chatwithnotifications.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.max.chatwithnotifications.Act;
import com.example.max.chatwithnotifications.AppUser;
import com.example.max.chatwithnotifications.Chat;
import com.example.max.chatwithnotifications.ChatAdapter;
import com.example.max.chatwithnotifications.R;
import com.example.max.chatwithnotifications.UserLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView chatList;
    private FirebaseUser mFirebaseUser;
    private final String USERS_TABLE="USER_CHAT_INFO";
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<Chat> chats;

    public ChatListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatListFragment newInstance(String param1, String param2) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        database=FirebaseDatabase.getInstance();
        mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= database.getReference(USERS_TABLE).child(mFirebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chats=new ArrayList<>();
                for (DataSnapshot chat:dataSnapshot.getChildren()
                     ) {
                    Chat ch=chat.getValue(Chat.class);
                    chats.add(ch);
                }
                updateUI();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateUI() {
        if(this.getContext()!=null)
            chatList.setAdapter(new ChatAdapter(chats,this.getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chat_list,container,false);
        chatList=v.findViewById(R.id.chat_list);
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startAct(Act.class,chatParticipants(getClickedChat(i)));
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private Chat getClickedChat(int pos)
    {
        return (Chat)chatList.getItemAtPosition(pos);
    }

    private ArrayList<AppUser> chatParticipants(Chat chat)
    {
        return chat.ChatParticipants;
    }

    private void startAct(Class actClass,ArrayList<AppUser> users)
    {
        Intent intent=new Intent(getContext(),actClass);
        intent.putExtra("users",users);
        getContext().startActivity(intent);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
