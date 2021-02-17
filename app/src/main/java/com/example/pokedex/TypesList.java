package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.pokedex.Adapters.ListaPokemonAdapter;
import com.example.pokedex.models.Pokemon;
import com.example.pokedex.models.PokemonRespuesta;
import com.example.pokedex.models.TypeResearch;
import com.example.pokedex.models.WantedTypePoke;
import com.example.pokedex.pokeapi.PokeApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TypesList extends AppCompatActivity {

    private static final String TAG = "POKEDEX";

    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;

    private int offset;

    private boolean aptoParaCargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex_list);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        listaPokemonAdapter = new ListaPokemonAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Bundle extras = getIntent().getExtras();
        String type_wanted = extras.getString("Wanted_type");
        Log.e(TAG, " Valor del tipo: " + type_wanted);
        aptoParaCargar = true;
        offset = 0;
        type_research(type_wanted);



    }

    private void type_research(String type){
        PokeApiService service = retrofit.create(PokeApiService.class);

        Call<TypeResearch> pokemonTypeResearchCall = service.getTypeResearch(type);
        pokemonTypeResearchCall.enqueue(new Callback<TypeResearch>() {
            @Override
            public void onResponse(Call<TypeResearch> call, Response<TypeResearch> response) {
                aptoParaCargar = true;
                if (response.isSuccessful()) {
                    TypeResearch typeResearch = response.body();
                    ArrayList<Pokemon> TypeList = new ArrayList<>();


                    for (int i = 0; i < typeResearch.getPokemon().size(); i++) {
                        WantedTypePoke wp= typeResearch.getPokemon().get(i);
                        Pokemon poke = new Pokemon(wp.getPokemon().getName(), wp.getPokemon().getUrl());
                        Log.e(TAG, " Url del pokemon: " + wp.getPokemon().getName());
                        Log.e(TAG, " Url del pokemon: " + wp.getPokemon().getUrl());
                       TypeList.add(poke);

                    }
                    Log.e(TAG, " Tamaño de la lista: " + TypeList.size());
                    listaPokemonAdapter.addPokemonList(TypeList);

                }
            }

            @Override
            public void onFailure(Call<TypeResearch> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });


    }
}