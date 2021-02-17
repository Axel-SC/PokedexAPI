package com.example.pokedex.models;



public class Types extends Type{
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
