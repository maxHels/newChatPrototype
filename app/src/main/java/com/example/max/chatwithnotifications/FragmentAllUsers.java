package com.example.max.chatwithnotifications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
 * {@link FragmentAllUsers.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAllUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAllUsers extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String USERS_TABLE="USERS_TABLE";
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<AppUser> users;
    private AppUser user;
    private ListView usersList;

    private OnFragmentInteractionListener mListener;

    public FragmentAllUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAllUsers.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAllUsers newInstance(String param1, String param2) {
        FragmentAllUsers fragment = new FragmentAllUsers();
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
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseUser=mFirebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference(USERS_TABLE);


        if(mFirebaseUser==null)
        {
            startActivity(new Intent(this.getContext(),GoogleSignIn.class));
            getActivity().finish();
            database.setPersistenceEnabled(true);
            return;
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<AppUser>();
                try {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        AppUser user = postSnapshot.getValue(AppUser.class);
                        users.add(user);
                    }
                    updateUI();
                }
                catch (Exception e)
                {
                    Log.d("d",e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI()
    {
        user=new AppUser(mFirebaseUser.getDisplayName(),mFirebaseUser.getUid(),
                mFirebaseUser.getPhotoUrl().toString());

        if(!users.contains(user))
        {
            databaseReference.child(USERS_TABLE).push().setValue(user);
        }
        else {
            users.remove(user);
        }

        usersList.setAdapter(new ArrayUsersAdapter(this.getContext(),users));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_all_users, container, false);
        usersList=v.findViewById(R.id.users_listt);
        //updateUI();
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

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