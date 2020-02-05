package com.example.geoappmidlle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.geoappmidlle.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {
    private Button signIn,register;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addOnClicListener();
    }
    public void addOnClicListener(){
        signIn=(Button)findViewById(R.id.buttonSignIn);
        register=(Button)findViewById(R.id.buttonRegister);

        //link with a database
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference("Users");
        //--

        root=findViewById(R.id.root_element);


        signIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowSignInWindow();
                    }
                }
        );
        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRegisterWindow();
                    }
                }
        );
    }

    private void ShowSignInWindow() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Log IN");
        dialog.setMessage("Complete all required fields for signIn please");
        LayoutInflater inflater=LayoutInflater.from(this);
        View signInview=inflater.inflate(R.layout.authorisation,null);
        dialog.setView(signInview);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final MaterialEditText email=signInview.findViewById(R.id.emailfield);
        final MaterialEditText pass=signInview.findViewById(R.id.passfield);
        dialog.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root,"please enter your email",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(pass.getText().toString().length()<5){
                    Snackbar.make(root,"password can not be shorter than 5 symbols",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                           startActivity(new Intent(MainActivity.this,Map.class));
                           finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(root,"acces denied!!!" +e.getMessage(),Snackbar.LENGTH_SHORT).show();
                        }
                    })

                ;

            }
        });
        dialog.show();

    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Inregistrare");
        dialog.setMessage("Complete all fields for registration please");

        LayoutInflater inflater=LayoutInflater.from(this);
        View register_window=inflater.inflate(R.layout.register_window,null);
        dialog.setView(register_window);

        final MaterialEditText email=register_window.findViewById(R.id.emailfield);
        final MaterialEditText pass=register_window.findViewById(R.id.passfield);
        final MaterialEditText name=register_window.findViewById(R.id.namefield);
        final MaterialEditText phone=register_window.findViewById(R.id.phonefield);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Register a new user", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root,"put your email please",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(name.getText().toString())){
                    Snackbar.make(root,"put your name please",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phone.getText().toString())){
                    Snackbar.make(root,"put your phone please",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(pass.getText().toString().length()<5){
                    Snackbar.make(root,"put a password witch has more than 5 symbols please",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                //Inregistram userul

                auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                        .addOnSuccessListener(
                                new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        User user=new User();
                                        user.setEmail(email.getText().toString());
                                        user.setPass(pass.getText().toString());
                                        user.setName(name.getText().toString());
                                        user.setPhone(phone.getText().toString());
                                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Snackbar.make(root,"user was added!!",Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                        )
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root,"Registration Errror"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        dialog.show();
    }
}
