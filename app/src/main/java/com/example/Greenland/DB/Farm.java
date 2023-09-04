package com.example.Greenland.DB;

public class Farm {
    String farmEmail;
    String farmName;
    String farmDescription;
    String farmImageUrl;

    public Farm(){}

    public String  getfarmEmail() {
        return farmEmail;
    }

    public void setfarmEmail(String farmEmail) {
        this.farmEmail = farmEmail;
    }

    public String getfarmName() {
        return farmName;
    }

    public void setfarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getfarmDescription() {
        return farmDescription;
    }

    public void setfarmDescription(String farmDescription) {
        this.farmDescription = farmDescription;
    }
    public String getfarmImageUrl() {
        return farmImageUrl;
    }

    public void setfarmImageUrl(String farmImageUrl) {
        this.farmImageUrl = farmImageUrl;
    }

    public Farm(String farmEmail, String farmName, String farmDescription, String farmImageUrl){
        this.farmEmail = farmEmail;
        this.farmName = farmName;
        this.farmDescription = farmDescription;
        this.farmImageUrl = farmImageUrl;
    }
}
