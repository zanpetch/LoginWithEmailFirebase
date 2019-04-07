package com.example.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private Button loginBtn;
    private FirebaseDatabase database;
    private DatabaseReference refUsers;

    private String username;
    private String password;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        refUsers = database.getReference("users");

        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_button);

        intent = new Intent(this, MainActivity.class);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameView.getText().toString();
                password = passwordView.getText().toString();

                //if username or password == ''
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "This field is required", Toast.LENGTH_SHORT).show();
                } else {
                    refUsers.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                Map map = (Map)dataSnapshot.getValue();
                                String usernameDatabase = String.valueOf(map.get("username")); //read child username in usersID
                                String passwordDatabase = String.valueOf(map.get("password")); //read child password in usersID

                                //check if Authentication Failed
                                if (!username.equals(usernameDatabase) || !password.equals(passwordDatabase)){
                                    Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    passwordView.setText("");
                                }

                                //check if Authentication Success go to mainActivity
                                else {
                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            }

                            //Map Null because refUsers.child(username) --> child(username) not in database
                            catch (NullPointerException e){
                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                passwordView.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();
                            usernameView.setText("");
                            passwordView.setText("");
                        }
                    });
                }
            }
        });
    }


}
