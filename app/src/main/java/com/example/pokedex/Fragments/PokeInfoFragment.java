package com.example.pokedex.Fragments;

import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pokedex.R;
import com.example.pokedex.models.Flavor_text_entries;
import com.example.pokedex.models.Genera;
import com.example.pokedex.models.PokemonDetails;
import com.example.pokedex.models.PokemonWantedInfo;
import com.example.pokedex.models.SpeciesDetails;
import com.example.pokedex.models.Stats;
import com.example.pokedex.pokeapi.PokeApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PokeInfoFragment extends Fragment {
    protected View pokeInfoFragment;
    private TextView tv_HP;
    private TextView tv_Atack;
    private TextView tv_Defense;
    private TextView tv_Special_Atack;
    private TextView tv_Special_Defense;
    private TextView tv_Speed;
    private TextView tv_Height;
    private TextView tv_Weight;
    private TextView tv_Descripcion;
    private TextView tv_FirstSkill;
    private TextView tv_SecondSkill;
    private TextView tv_HiddenSkill;
    private Retrofit retrofit;
    public  TextView [] tx_base_stats;

    public PokeInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pokeInfoFragment = inflater.inflate(R.layout.fragment_poke_info, container, false);

        Bundle poke_ID_Sent = getArguments();
        String poke_ID = poke_ID_Sent.getString("POKE_ID");
        Log.i("Pokemon ID", "ID del pokemon: " + poke_ID);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tv_HP = pokeInfoFragment.findViewById(R.id.tv_HP);
        tv_Atack = pokeInfoFragment.findViewById(R.id.tv_Atack);
        tv_Defense = pokeInfoFragment.findViewById(R.id.tv_Defense);
        tv_Special_Atack = pokeInfoFragment.findViewById(R.id.tv_Special_Atack);
        tv_Special_Defense = pokeInfoFragment.findViewById(R.id.tv_Special_Defense);
        tv_Speed = pokeInfoFragment.findViewById(R.id.tv_Speed);
        tv_Descripcion =  pokeInfoFragment.findViewById(R.id.tv_Description);
        tv_Weight =  pokeInfoFragment.findViewById(R.id.tv_Weight);
        tv_Height =  pokeInfoFragment.findViewById(R.id.tv_Height);
        tv_FirstSkill =  pokeInfoFragment.findViewById(R.id.tv_FirstSkill);
        tv_SecondSkill =  pokeInfoFragment.findViewById(R.id.tv_SecondSkill);
        tv_HiddenSkill =  pokeInfoFragment.findViewById(R.id.tv_HiddenSkill);

        tx_base_stats = new TextView[6];
        tx_base_stats[0] = tv_HP;
        tx_base_stats[1] = tv_Atack;
        tx_base_stats[2] = tv_Defense;
        tx_base_stats[3] = tv_Special_Atack;
        tx_base_stats[4] = tv_Special_Defense;
        tx_base_stats[5] = tv_Speed;


        showPokemonDetailsData(poke_ID);
        showPkmSpeciesDetailsData(poke_ID);

        return pokeInfoFragment;
    }
    public void showPokemonDetailsData(String pokeID){

        PokeApiService service = retrofit.create(PokeApiService.class);
        Call<PokemonDetails> pokemonDetailsCall = service.getPkmDetails(pokeID);

        pokemonDetailsCall.enqueue(new Callback<PokemonDetails>() {
            @Override
            public void onResponse(Call<PokemonDetails> call, Response<PokemonDetails> response) {

                if (response.isSuccessful()) {
                    PokemonDetails pokemonDetails = response.body();
                    fillPokemonStats(pokemonDetails);
                    fillWeightAndHeight(pokemonDetails);
                } else {
                    Log.e("TAG", " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonDetails> call, Throwable t) {

                Log.e("TAG", " onFailure: " + t.getMessage());
            }
        });
    }
    public void fillPokemonStats(PokemonDetails pokemonDetails){
        for (int i = 0; i < pokemonDetails.getStats().size(); i++) {
            Stats s= pokemonDetails.getStats().get(i);
            Log.i("prova", "Nombre Typo " + s.getBase_stat());
            tx_base_stats[i].setText(s.getBase_stat());
        }
        //Vida, Ataque, Defensa, AS, DF, Velocidad
        tv_HP.setText(tx_base_stats[0].getText());;
        tv_Atack.setText(tx_base_stats[1].getText());;
        tv_Defense.setText(tx_base_stats[2].getText());;
        tv_Special_Atack.setText(tx_base_stats[3].getText());;
        tv_Special_Defense.setText(tx_base_stats[4].getText());;
        tv_Speed.setText(tx_base_stats[5].getText());;

    }

    public void fillWeightAndHeight(PokemonDetails pokemonDetails){
        float Height = Float.parseFloat(pokemonDetails.getHeight())/10;
        float Weight = Float.parseFloat(pokemonDetails.getWeight())/10;

        tv_Height.setText("ALTURA: "+Height+" m");
        tv_Weight.setText("PESO: "+Weight+" kg");
    }

    public void showPkmSpeciesDetailsData(String id){

        PokeApiService service = retrofit.create(PokeApiService.class);

        Call<SpeciesDetails> pokemonSpeciesDetailsCall = service.getSpeciesDetails(id);
        pokemonSpeciesDetailsCall.enqueue(new Callback<SpeciesDetails>() {
            @Override
            public void onResponse(Call<SpeciesDetails> call, Response<SpeciesDetails> response) {
                if (response.isSuccessful()) {
                    SpeciesDetails speciesDetails = response.body();
                    fillDescription(speciesDetails);
                }
            }

            @Override
            public void onFailure(Call<SpeciesDetails> call, Throwable t) {
                Log.e("TAG", " onFailure: " + t.getMessage());
            }
        });
    }

    public void fillDescription(SpeciesDetails speciesDetails){
        Flavor_text_entries f = speciesDetails.getFlavor_text_entries().get(1); //It means the first description in English
        Log.i("prova", "FlavorText " + f.getFlavor_text());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tv_Descripcion.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tv_Descripcion.setText(f.getFlavor_text().replaceAll("\n", " "));
        }
    }
}