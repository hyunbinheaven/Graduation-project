package com.example.Greenland;

public class ChatItem {
    private String time;
    private String senderId;
    private String message;
    private String receiveId;
    private String Imglink;

    public ChatItem() {
        this.time = "";
        this.senderId = "";
        this.message = "";
        this.receiveId = "";
        this.Imglink = "";
    }

    public ChatItem(String time, String message, String senderId, String receiveId,String Imglink) {
        this.time = time;
        this.senderId = senderId;
        this.message = message;
        this.receiveId = receiveId;
        this.Imglink = Imglink;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getreceiveId() {
        return receiveId;
    }

    public void setreceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getImglink() {
        return Imglink;
    }

    public void setImglink(String Imglink) {
        this.Imglink = Imglink;
    }

}