package com.example.PIS;

public class Imatge {
    private String titol;
    private String imatge;

    public Imatge(){}

    public Imatge(String titol, String imatge){
        this.titol = titol;
        this.imatge = imatge;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getImatge() {
        return imatge;
    }

    public void setImatge(String imatge) {
        this.imatge = imatge;
    }



}
