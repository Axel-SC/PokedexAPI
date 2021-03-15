package com.example.pokedex.models;


import java.io.Serializable;

public class Types extends Type implements Serializable {
    private String slot;
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSlot() {
        return slot;
    }



    public void setSlot(String slot) {
        this.slot = slot;
    }


}
