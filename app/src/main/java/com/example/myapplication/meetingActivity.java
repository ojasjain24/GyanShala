package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.Models.ClassesModel;
import com.example.myapplication.Models.userModel;
import com.facebook.react.modules.core.PermissionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class meetingActivity extends FragmentActivity implements JitsiMeetActivityInterface {
    private JitsiMeetView view;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<ClassesModel> userData = new ArrayList<>();
    final Boolean[] neverInside = {true};
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            JitsiMeetActivityDelegate.onBackPressed();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        Intent i = getIntent();
        view = new JitsiMeetView(this);
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setEmail(""+firebaseuser.getEmail());

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(firebaseuser.getUid());
        userData.clear();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final userModel user = snapshot.getValue(userModel.class);
                userInfo.setDisplayName(user.getName());
                String urlString = user.getImage();
                URL myURL = null;
                try {
                    myURL = new URL(urlString);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                userInfo.setAvatar(myURL);

                JitsiMeetConferenceOptions videoOptions = new JitsiMeetConferenceOptions.Builder()
                        .setRoom("https://meet.jit.si/GyanShala_"+i.getStringExtra("key"))
                        .setWelcomePageEnabled(false)
                        .setAudioOnly(false)
                        .setVideoMuted(false)
                        .setAudioMuted(false)
                        .setUserInfo(userInfo)
                        .build();

                    view.join(videoOptions);
                setContentView(view);
                JitsiMeetViewListener listener = new JitsiMeetViewListener() {
                    @Override
                    public void onConferenceJoined(Map<String, Object> map) {

                    }

                    @Override
                    public void onConferenceTerminated(Map<String, Object> map) {
                        finish();
                    }

                    @Override
                    public void onConferenceWillJoin(Map<String, Object> map) {

                    }
                };
                view.setListener(listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view.dispose();
        view = null;
        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}