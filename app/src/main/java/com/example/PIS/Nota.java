package com.example.PIS;

public class Nota {
    private String titol;
    private String content;

    public Nota(){}

    public Nota(String titol, String content){
        this.titol = titol;
        this.content = content;
    }

    public String getTitol() {
        return titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String contingut) {
        this.content = contingut;
    }



}
