package com.example.PIS;

import androidx.annotation.NonNull;

public class Nota {
    private String titol;
    private String contingut;

    public Nota(){}

    public Nota(String title, String content){
        this.titol = title;
        this.contingut = content;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getContingut() {
        return contingut;
    }

    public void setContingut(String contingut) {
        this.contingut = contingut;
    }


    @NonNull
    @Override
    public String toString() {
        return this.titol;
        
    }



}
