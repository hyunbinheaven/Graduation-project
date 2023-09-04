package com.example.Greenland.DB;

public class User_DB {

    String Email;
    String Password;
    String Nickname;

    public User_DB(){}



    //여기서부터 get,set 함수를 사용하는데 이부분을 통해 값을 가져옴
    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassowrd(String Password) {
        this.Password = Password;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    public User_DB(String Email, String Password, String Nickname) {
        this.Email = Email;
        this.Password = Password;
        this.Nickname = Nickname;
    }
}