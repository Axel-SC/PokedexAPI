package com.example.pokedex.models;

import java.util.ArrayList;

public class PokemonInfoDB {
    private String name;
    private String id;
    private String height;
    private String weight;
    private Species species;
    private ArrayList<Types> types;
    private String genera; //SpeciesName
    private String flavor_text_entrie;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public ArrayList<Types> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<Types> types) {
        this.types = types;
    }

    public String getGenera() {
        return genera;
    }

    public void setGenera(String genera) {
        this.genera = genera;
    }

    public String getFlavor_text_entrie() {
        return flavor_text_entrie;
    }

    public void setFlavor_text_entrie(String flavor_text_entrie) {
        this.flavor_text_entrie = flavor_text_entrie;
    }
}
