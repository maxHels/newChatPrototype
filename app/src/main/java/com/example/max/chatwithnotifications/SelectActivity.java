package com.example.max.chatwithnotifications;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.max.chatwithnotifications.Fragments.ChatListFragment;
import com.example.max.chatwithnotifications.Fragments.FragmentAllUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.max.chatwithnotifications.Fragments.FragmentAllUsers.*;

public class SelectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnFragmentInteractionListener,
        ChatListFragment.OnFragmentInteractionListener {

    private FirebaseUser user;
    private FragmentAllUsers fragmentAllUsers;
    private AppUser appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentAllUsers=new FragmentAllUsers();
        user=UserLoader.getCurrentFirebaseUser(this,this);

        appUser=new AppUser(user.getDisplayName(),user.getUid(),user.getPhotoUrl().toString());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Establishing view describing current user
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headLayout=navigationView.inflateHeaderView(R.layout.nav_header_select);
        TextView userName=headLayout.findViewById(R.id.user_name);
        ImageView userPhoto=headLayout.findViewById(R.id.user_photo);
        userName.setText(user.getDisplayName());
        new ImageDownloader(userPhoto).execute(user.getPhotoUrl().toString());

        //Set default screen as contacts
        launchFragment(fragmentAllUsers);
    }

    //Replace current fragment by other
    //Example: replace contacts screen by chat screen
    private void launchFragment(Fragment fragment)
    {
        FragmentTransaction fr=getFragmentManager().beginTransaction();
        fr.replace(R.id.fragment_container,fragment);
        fr.commit();
    }
    //Same action as previous, but can transfer some data
    private void launchFragment(Fragment fragment, Bundle bundle)
    {
        fragment.setArguments(bundle);
        launchFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.contacts)
        {
            launchFragment(fragmentAllUsers);
        }
        if(id==R.id.all_chats_act)
        {
            launchFragment(new ChatListFragment());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction() {
        Bundle chatName=new Bundle();
        chatName.putParcelable("chatName",fragmentAllUsers.tappedUser());
        Intent intent=new Intent(this,Act.class);
        ArrayList<AppUser> temp=new ArrayList<>(2);
        temp.add(appUser);
        temp.add(fragmentAllUsers.tappedUser());
        intent.putExtra("users",temp);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
