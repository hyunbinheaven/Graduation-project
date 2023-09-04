package com.example.Greenland.DB;

public class product {

    String productname;
    String productprice;
    String productcontents;
    String imageUrl;
    String email;
    String datetime;
    String nickname;
    String randomKey;

    public product(){}

    public String getproductname() {
        return productname;
    }

    public void setproductname(String productname) {
        this.productname = productname;
    }

    public String getproductprice() {
        return productprice;
    }

    public void setproductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getproductcontents() {
        return productcontents;
    }

    public void setproductcontents(String productcontents) {
        this.productcontents = productcontents;
    }
    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getdatetime() {
        return datetime;
    }

    public void setdatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getnickname() {
        return nickname;
    }

    public void setnickname(String nickname) {
        this.nickname = nickname;
    }


    public product(String productname, String productprice, String productcontents, String imageUrl, String email, String datetime, String nickname){
        this.productname = productname;
        this.productprice = productprice;
        this.productcontents = productcontents;
        this.imageUrl = imageUrl;
        this.email = email;
        this.datetime = datetime;
        this.nickname = nickname;

    }
}