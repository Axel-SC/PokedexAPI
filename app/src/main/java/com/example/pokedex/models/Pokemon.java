package com.example.pokedex.models;

public class Pokemon {
    private int spriteNumber;
    private String name;
    private String url;
    private String weight;


    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getSpriteNumber() {
        String [] urlPartes = url.split("/"); //Divido la url en varias partes, usando el slash
        return Integer.parseInt(urlPartes[urlPartes.length-1]); //Cojo el ultimo dato que es el numero y lo parseo
    }

    public void setSpriteNumber(int spriteNumber) {
        this.spriteNumber = spriteNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
