package com.example.pokedex.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SpeciesDetails {
    private ArrayList<Genera> genera;
    private ArrayList<Flavor_text_entries> flavor_text_entries;

    public ArrayList<Flavor_text_entries> getFlavor_text_entries() {
        return flavor_text_entries;
    }

    public void setFlavor_text_entries(ArrayList<Flavor_text_entries> flavor_text_entries) {
        this.flavor_text_entries = flavor_text_entries;
    }

    public ArrayList<Genera> getGenera() {
        return genera;
    }

    public void setGenera(ArrayList<Genera> genera) {
        this.genera = genera;
    }


}
