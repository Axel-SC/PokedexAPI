package com.example.pokedex.pokeapi;

import com.example.pokedex.models.Pokemon;
import com.example.pokedex.models.PokemonDetails;
import com.example.pokedex.models.PokemonRespuesta;
import com.example.pokedex.models.SpeciesDetails;
import com.example.pokedex.models.TypeResearch;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {
    @GET("pokemon")
    Call<PokemonRespuesta> obtenerListaPokemon(@Query("limit") int limit, @Query("offset") int offset);
    @GET("pokemon/{name}")
    Call<PokemonDetails> getPkmDetails(@Path("name") String name);
    @GET("pokemon-species/{id}")
    Call<SpeciesDetails> getSpeciesDetails(@Path("id") String id);
    @GET("type/{name}")
    Call<TypeResearch> getTypeResearch(@Path("name") String name);



}
