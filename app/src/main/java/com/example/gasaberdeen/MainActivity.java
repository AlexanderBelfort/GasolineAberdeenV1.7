package com.example.gasaberdeen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gasaberdeen.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {



    Button sign_in_button,app_regn_button;
    RelativeLayout rootLayout;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize firebase

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");


        //initialize button view

        app_regn_button = (Button)findViewById(R.id.app_regn_button) ;
        sign_in_button = (Button)findViewById(R.id.sign_in_button);
        rootLayout =  (RelativeLayout)findViewById(R.id.rootLayout);

        //event

        app_regn_button.setOnClickListener(new View.OnClickListener() {

                                               @Override
                                               public void onClick(View view) {
                                                   showLOGINnew();
                                               }
                                           });



        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showREGISTERnew();
            }
        });

        //here is our spinner or dropdown menu






        //HERE ENDS OUR TOOLBAR UP


        //here we will
        //initialize and assign variables

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        //we will set home as selected

        bottomNavigationView.setSelectedItemId(R.id.home);

       //we will perform ItemSelectedListener

       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId()){
                   case R.id.dashboard:
                       startActivity(new Intent(getApplicationContext(),
                               Dashboard.class));
                       overridePendingTransition(0, 0);
                       return true;
                   case R.id.home:
                       return true;

                   case R.id.filter:
                       startActivity(new Intent(getApplicationContext(),
                               Filter.class));
                       overridePendingTransition(0, 0);
                       return true;

                   case R.id.about:
                       startActivity(new Intent(getApplicationContext(),
                               About.class));
                       overridePendingTransition(0, 0);
                       return true;
               }
               return false;
           }
       });


       //TEST TOOLBAR



        //TEST TOOLBAR END




    }

    private void showREGISTERnew() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register to Gasoline Aberdeen.");


        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_login = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText editEmail = layout_login.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = layout_login.findViewById(R.id.editPassword);


        dialog.setView(layout_login);

        //set btton

        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //check validation

                if(TextUtils.isEmpty(editEmail.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }



                if(TextUtils.isEmpty(editPassword.getText().toString())){
                    Snackbar.make(rootLayout, "Please enter your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(editPassword.getText().toString().length() < 6){
                    Snackbar.make(rootLayout, "Password too short.", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }



                //reg new user

                auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //save user to db
                                User user = new User();
                                user.setEmail(editEmail.getText().toString());

                                //user setphone
                                //user setname

                                user.setPassword(editPassword.getText().toString());
                                //use email to key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "You have registered successfully.", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Registration failed."+e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Registration failed."+e.getMessage(), Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });

            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }
//change name here
    private void showLOGINnew() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("LOG IN");
        dialog.setMessage("Please use email to LOG IN");


        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText editEmail = register_layout.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = register_layout.findViewById(R.id.editPassword);
        //final MaterialEditText editName = register_layout.findViewById(R.id.editName);
        //final MaterialEditText editPhone = register_layout.findViewById(R.id.editPhone);

        dialog.setView(register_layout);

        //set btton

        dialog.setPositiveButton("LOG IN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //check validation

                        if (TextUtils.isEmpty(editEmail.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                    //    if (TextUtils.isEmpty(editPhone.getText().toString())) {
                    //        Snackbar.make(rootLayout, "Please enter your phone number", Snackbar.LENGTH_SHORT)
                    //                .show();
                    //        return;
                    //    }

                        if (TextUtils.isEmpty(editPassword.getText().toString())) {
                            Snackbar.make(rootLayout, "Please enter your password", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        }

                        if (editPassword.getText().toString().length() < 6) {
                            Snackbar.make(rootLayout, "Password too short.", Snackbar.LENGTH_SHORT)
                                    .show();
                            return;
                        }


                        //login

                        auth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        startActivity(new Intent(MainActivity.this, Dashboard.class));

                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Failed, please try again."+e.getMessage(),Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });


                    }
                });






        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });



        dialog.show();


    }
}
