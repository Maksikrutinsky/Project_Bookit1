package co.il.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class registerActivity extends AppCompatActivity {


    EditText signupName,signupEmail,signupPassword,signupUserName;
    TextView LoginText;
    Button sigUpButton;

    /*Data Base*/
    FirebaseDatabase database;
    DatabaseReference reference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signupName = findViewById(R.id.signupUsername);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupUserName = findViewById(R.id.signupnickname);
        sigUpButton = findViewById(R.id.signupButton);
        LoginText = findViewById(R.id.textView);

        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registerActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        sigUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = signupName.getText().toString();
                final String password = signupPassword.getText().toString();
                final String userName = signupUserName.getText().toString();
                final String email = signupEmail.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email)) {
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");

                    // Check if the username already exists
                    Query usernameQuery = userReference.orderByChild("userName").equalTo(userName);
                    Query emailQuery = userReference.orderByChild("email").equalTo(email);

                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
                            if (usernameSnapshot.exists()) {
                                // Username already exists
                                Toast.makeText(registerActivity.this, "Username already exists. Choose a different username.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Check if the email already exists
                                emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot emailSnapshot) {
                                        if (emailSnapshot.exists()) {
                                            // Email already exists
                                            Toast.makeText(registerActivity.this, "Email already exists. Choose a different email.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Check if user has already registered
                                            DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference("users").child(userName);
                                            currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        // User has already registered
                                                        Toast.makeText(registerActivity.this, "You have already registered. Cannot register again.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Username and email are unique, proceed with registration
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userName);
                                                        helperClass helperClass = new helperClass(name, email, userName, password);
                                                        reference.setValue(helperClass);

                                                        Toast.makeText(registerActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(registerActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Handle onCancelled
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle onCancelled
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });
                } else {
                    Toast.makeText(registerActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}