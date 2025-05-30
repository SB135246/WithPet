package com.example.withpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    private EditText mEtEmail, mEtPwd, mEtPwdCheck, mEtNick;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_active);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("WithPet");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwdCheck = findViewById(R.id.et_check);
        mEtNick = findViewById(R.id.et_nick);
        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEtEmail.getText().toString().trim();
                String pwd = mEtPwd.getText().toString().trim();
                String pwdCheck = mEtPwdCheck.getText().toString().trim();
                String nick = mEtNick.getText().toString().trim();

                if (email.isEmpty() || pwd.isEmpty() || pwdCheck.isEmpty() || nick.isEmpty()) {
                    Toast.makeText(Register.this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pwd.equals(pwdCheck)) {
                    Toast.makeText(Register.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //  닉네임 중복 체크
                mDatabaseRef.child("UserAccount").orderByChild("nickname").equalTo(nick)
                        .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Toast.makeText(Register.this, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // 중복 아님 → 회원가입 진행
                                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                                        UserAccount account = new UserAccount();
                                                        account.setIdToken(firebaseUser.getUid());
                                                        account.setEmailId(email);
                                                        account.setPassword(pwd);
                                                        account.setNickname(nick);

                                                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                                                        Toast.makeText(Register.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(Register.this, Login.class));
                                                        finish();
                                                    } else {
                                                        Exception e = task.getException();
                                                        if (e instanceof FirebaseAuthWeakPasswordException) {
                                                            Toast.makeText(Register.this, "비밀번호가 너무 약합니다.", Toast.LENGTH_SHORT).show();
                                                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                            Toast.makeText(Register.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                                                            Toast.makeText(Register.this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(Register.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                                Toast.makeText(Register.this, "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}