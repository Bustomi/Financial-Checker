package com.luckynineapps.financialchecker.model;

public class Common {

    private String id;
    private String judul;
    private String harga;
    private String image;

    public Common(){

    }

    public Common(String judul, String harga, String image) {
        this.judul = judul;
        this.harga = harga;
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getHarga() {
        return harga;
    }

    public String getImage() {
        return image;
    }
}
