package com.example.withpet;

// 사용자 계정 정보 모델 클래스
public class UserAccount {
    private String idToken;   // Firebase Uid (고유 토큰 정보)
    private String emailId;   // 이메일 아이디
    private String password;  // 비밀번호
    private String nickname;  // 닉네임 ← 추가됨

    public UserAccount() { }

    // Getter & Setter for idToken
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    // Getter & Setter for emailId
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    // Getter & Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Getter & Setter for nickname
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

