package com.example.pokedex.models;

import java.util.ArrayList;

public class TypeResearch {
    /* Esto despues
    private damage_relations;
    private double_damage_from;
    private double_damage_to;
    private half_damage_from;
    private half_damage_to;
    private no_damage_from;
    private ArrayList<Game_indices> game_indices;
    private ArrayList<Generation>generation;
    private id;
    private ArrayList<Move_damage_class>move_damage_class;
    private ArrayList<Moves>moves;
    private name;
    private ArrayList<Names>names;*/
    private String id;
    private String name;
    private ArrayList<Moves>moves;
    private ArrayList<WantedTypePoke>pokemon; //Tengo que comprobar si esta clase me sirve
    public ArrayList<Moves> getMoves() {
        return moves;
    }

    public void setMoves(ArrayList<Moves> moves) {
        this.moves = moves;
    }


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

    public ArrayList<WantedTypePoke> getPokemon() {
        return pokemon;
    }

    public void setPokemon(ArrayList<WantedTypePoke> pokemon) {
        this.pokemon = pokemon;
    }
}
