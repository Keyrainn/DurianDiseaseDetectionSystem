package com.example.duriandiseasedetectionsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duriandiseasedetectionsystem.model.Farmer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfile extends AppCompatActivity {

    private TextView viewName, viewEmail, viewPhone, viewPass, viewAddress;
    private FirebaseAuth mAuth;
    private DatabaseReference mProfileRef;
    private CircleImageView profilePic;
    private String mImageUrl;
    private Button mEditButton;

    //Update input field...
    private TextView updateSEmail;
    private EditText updateSPass;
    private EditText updateSNotel;
    private EditText updateSAdd;
    private Button btnUpdateUp;

    private String farmerID;
    private String farmerName;
    private String farmerAddress;
    private String farmerNoTel;
    private String farmerEmail;
    private String farmerRole;
    private String farmerImage;
    private String farmerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_view_profile);

        mAuth = FirebaseAuth.getInstance();
        mProfileRef = FirebaseDatabase.getInstance().getReference();

        //ambik current user yg tengah login
        FirebaseUser mUser = mAuth.getCurrentUser();
        //amik user punya id/email
        final String uID = mUser.getUid();
        String identifier = mUser.getEmail();

        //check id sapa tengah login sekarang
        System.out.println("/n/n Current Farmer ID is : " + uID);
        System.out.println("/n/n Current Farmer user is : " + identifier);
        mProfileRef.child("Farmer").child(mAuth.getUid());

        //link with xml
        viewEmail = findViewById(R.id.pEmail);
        viewPass = findViewById(R.id.pPassword);
        viewName = findViewById(R.id.pName);
        viewPhone = findViewById(R.id.pPhone);
        viewAddress = findViewById(R.id.pAddress);
        profilePic = findViewById(R.id.profilePicProfile);

        mEditButton = findViewById(R.id.updateBtn);

        //Call View Profile Method
        ViewProfile();

        //Inflate updatedeleteinputfield_farmer
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Edit Farmer
                AlertDialog.Builder mydialog = new AlertDialog.Builder(ViewProfile.this);
                LayoutInflater inflater = LayoutInflater.from(ViewProfile.this);

                View myview = inflater.inflate(R.layout.updatedeleteinputfield_farmer, null);
                mydialog.setView(myview);

                final AlertDialog dialog = mydialog.create();

                //collect data from field of updatedeleteinputfield
                updateSEmail = myview.findViewById(R.id.upd_farmer_email);
                //for email Read only
                updateSEmail.setEnabled(false);
                updateSPass = myview.findViewById(R.id.upd_farmer_pass);
                updateSNotel = myview.findViewById(R.id.upd_farmer_notel);
                updateSAdd = myview.findViewById(R.id.upd_farmer_address);

                //instantiate button
                btnUpdateUp = myview.findViewById(R.id.btn_update_upd);

                //Load data into updatedeleteinputfield_farmer
                updateSEmail.setText(farmerEmail);
                updateSPass.setText(farmerPassword);
                updateSNotel.setText(farmerNoTel);
                updateSAdd.setText(farmerAddress);
                if(mImageUrl != null){
                    Picasso.with(getApplicationContext()).load(mImageUrl).into(profilePic);
                }

                //Update profile
                mProfileRef.child("Farmer").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            Farmer data = dataSnapshot.getValue(Farmer.class);

                            final String pName = data.getFarmerName();
                            final String ImageUrl = data.getFarmerImage();

                            btnUpdateUp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //variable ni dari global variable
                                    farmerEmail = viewEmail.getText().toString().trim();
                                    farmerPassword = viewPass.getText().toString().trim();
                                    String newEmail = updateSEmail.getText().toString().trim();
                                    String newPass = updateSPass.getText().toString().trim();
                                    String notel = updateSNotel.getText().toString().trim();
                                    String add = updateSAdd.getText().toString().trim();

                                    System.out.println("Current email is : " + farmerEmail);
                                    System.out.println("Current password is : " + farmerPassword);

                                    System.out.println("Current User ID is : " + uID);
                                    System.out.println("New Email is : " + newEmail);
                                    System.out.println("New password :" + newPass);

                                    //Call model constructor
                                    Farmer data = new Farmer(uID,farmerName,add,notel,newEmail,farmerRole,ImageUrl,newPass);
                                    //masuk kan value dalam id tu (position dy)
                                    mProfileRef.child("Farmer").child(uID).setValue(data);


                                    //Update Password
                                    FirebaseUser updPass = FirebaseAuth.getInstance().getCurrentUser();
                                    String newPassword = newPass;

                                    updPass.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        System.out.println("Update password jadi");
                                                        startActivity(new Intent(getApplicationContext(), ViewProfile.class));
                                                    }
                                                }
                                            });

                                    Toast.makeText(getApplicationContext(),"Data Updated", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Failed to read value.", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });
    } // End of On Create

    private void ViewProfile() {

        mProfileRef.child("Farmer").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    Farmer data = dataSnapshot.getValue(Farmer.class);

                    farmerEmail = data.getFarmerEmail();
                    farmerPassword = data.getFarmerPassword();
                    farmerName = data.getFarmerName();
                    farmerNoTel = data.getFarmerNoTel();
                    farmerRole = data.getFarmerRole();
                    farmerAddress = data.getFarmerAddress();

                    viewEmail.setText(farmerEmail);
                    viewPass.setText(farmerPassword);
                    viewName.setText(farmerName);
                    viewPhone.setText(farmerNoTel);
                    viewAddress.setText(farmerAddress);

                    mImageUrl = data.getFarmerImage();

                    System.out.println("Image uri " + mImageUrl);

                    if(mImageUrl != null){
                        Picasso.with(getApplicationContext()).load(mImageUrl).into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

