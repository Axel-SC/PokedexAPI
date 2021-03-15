package com.example.pokedex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ActionBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pokedex.Adapters.GridAdapter;
import com.example.pokedex.Adapters.ListaPokemonAdapter;
import com.example.pokedex.Adapters.typesGridView;
import com.example.pokedex.models.Flavor_text_entries;
import com.example.pokedex.models.Genera;
import com.example.pokedex.models.PokemonDetails;
import com.example.pokedex.models.PokemonWantedInfo;
import com.example.pokedex.models.Pokemon_For_Firebase;
import com.example.pokedex.models.SpeciesDetails;
import com.example.pokedex.models.TypeResearch;
import com.example.pokedex.models.Types;
import com.example.pokedex.models.WantedTypePoke;
import com.example.pokedex.pokeapi.PokeApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class PokedexInfo_Fragment extends Fragment {


    public static Activity act;
    public static TextView txtDisplay;
    public static TextView txtNameNumber;
    public static TextView txtSpecie;
    public static TextView txtDesc;
    public static TextView txtHeight;
    public static TextView txtWeight;
    private GridView types_gridView;

    enum ProviderType {
        BASIC
    }

    private GridAdapter gridAdapter;
    private List<typesGridView> typesList;
    public PokemonWantedInfo pokemonWantedInfo;

    public static ImageView imgPok;
    private Retrofit retrofit;
    private static final String TAG = "POKEDEX";
    private RequestBuilder<PictureDrawable> requestBuilder;
    private String pokeID = "1"; //First Pokemon
    private String pokeName = "Bulbasaur"; //First Pokemon
    private int numAux;
    private int min = 1;
    private int max = 898;
    private String urlSprites;
    private String tipo;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;
    private int offset;
    public static ImageView[] imgType;

    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference dbReference;
    private static PokedexInfo_Fragment myContext;
    private List<Pokemon_For_Firebase> poke_Favourites;
    private List<Pokemon_For_Firebase> poke_Captured;

    public PokedexInfo_Fragment() {
        myContext = this;
    }

    public static PokedexInfo_Fragment getInstance() {
        return myContext;
    }
    Context pokedexInfo_context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pokedexInfo_context = container.getContext();
        View pokedexInfoFragment = inflater.inflate(R.layout.fragment_pokedex_info_, container, false);
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        dbReference = rootNode.getReference("Users");
        poke_Favourites = new ArrayList<Pokemon_For_Firebase>();
        poke_Captured = new ArrayList<Pokemon_For_Firebase>();
        pokemonWantedInfo = new PokemonWantedInfo();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        offset = 0;

        imgType = new ImageView[2];

        //Asigno los layouts
        txtDisplay = pokedexInfoFragment.findViewById(R.id.txtDisplay);
        imgPok = pokedexInfoFragment.findViewById(R.id.imgPok);
        imgType[0] = pokedexInfoFragment.findViewById(R.id.imgType0);
        imgType[1] = pokedexInfoFragment.findViewById(R.id.imgType1);
        txtNameNumber = pokedexInfoFragment.findViewById(R.id.txtNameNumber);
        txtSpecie = pokedexInfoFragment.findViewById(R.id.txtSpecie);
        txtHeight = pokedexInfoFragment.findViewById(R.id.txtHeight);
        txtWeight = pokedexInfoFragment.findViewById(R.id.txtWeight);
        txtDesc = pokedexInfoFragment.findViewById(R.id.txtDesc);
        pokemonWantedInfo = new PokemonWantedInfo();

        showPkmDetails(pokeID);
        showPkmSpeciesDetails(pokeID);

        //Pruebas
        // Bundle extras = getIntent().getExtras();
        //String email = extras.getString("Email");
        //String provider = extras.getString("Provider");
        //Log.i("PruebaLogin", "Nombre del pokemon: " + email + provider);

        //Declaracion Botones y Listeners

        imgPok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle pokemonInfoExtras = new Bundle();
                pokemonInfoExtras.putSerializable("PokemonWantedInfo", pokemonWantedInfo);

                Intent toPokemonInfoNavigation = new Intent(pokedexInfo_context , PokemonInfoNavigation.class);
                toPokemonInfoNavigation.putExtras(pokemonInfoExtras);
                Log.i("Serializable", "Nombre " + pokemonWantedInfo.getName());
                Log.i("Serializable", "ID " + pokemonWantedInfo.getId());
                startActivity(toPokemonInfoNavigation);

            }
        });

        final Button btnRight = pokedexInfoFragment.findViewById(R.id.btnRight);
        btnRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numAux = Integer.parseInt(pokeID);
                if (numAux < max) { //Thats the max Id for a pokemon
                    numAux++;
                    pokeID = String.valueOf(numAux);
                    showPkmDetails(pokeID);
                } else {
                    Toast.makeText(pokedexInfo_context, "¡This is the last Pokemon!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final Button btnLeft = pokedexInfoFragment.findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numAux = Integer.parseInt(pokeID);
                if (numAux > min) { //Min Id for a Pokemon
                    numAux--;
                    pokeID = String.valueOf(numAux);
                    showPkmDetails(pokeID);
                } else {
                    Toast.makeText(pokedexInfo_context, "¡This is the first Pokemon!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ImageButton btnSearch = pokedexInfoFragment.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showTxtSearch();
            }
        });

        ImageButton btnTypes = pokedexInfoFragment.findViewById(R.id.btnTypes);
        btnTypes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open_CustomDialog();
            }
        });

        ImageButton btnStore = pokedexInfoFragment.findViewById(R.id.btnStore);
        btnStore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addFavourites(pokeID, pokeName);
            }
        });

        ImageButton btnPokedex = pokedexInfoFragment.findViewById(R.id.btn_Pokedex);
        btnPokedex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent toPokedex = new Intent(pokedexInfo_context, PokedexList.class);
                pokedexInfo_context.startActivity(toPokedex);

            }
        });

        return pokedexInfoFragment;
    }

    public void addFavourites(String poke_ID, String poke_Name){
        Pokemon_For_Firebase pokemon_for_firebase = new Pokemon_For_Firebase();
        pokemon_for_firebase.setId(poke_ID);
        pokemon_for_firebase.setName(poke_Name);
        poke_Favourites.add(pokemon_for_firebase);
        dbReference.child(auth.getUid()).child("Favourites").setValue(poke_Favourites);
        Log.i("Firebase", "Pokemon Stored in Favourites");
    }

    public void addCaptured(String poke_ID, String poke_Name){
        Pokemon_For_Firebase pokemon_for_firebase = new Pokemon_For_Firebase();
        pokemon_for_firebase.setId(poke_ID);
        pokemon_for_firebase.setName(poke_Name);
        poke_Favourites.add(pokemon_for_firebase);
        dbReference.child(auth.getUid()).child("Captured").setValue(poke_Favourites);
        Log.i("Firebase", "Pokemon Stored in Favourites");
    }







    public void showTxtSearch() {
        AlertDialog.Builder alert = new AlertDialog.Builder(pokedexInfo_context);
        alert.setTitle("Search a Pokemon");

        final EditText input = new EditText(pokedexInfo_context);
        input.setHint("Pokemon");
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pokeSearch = input.getText().toString().toLowerCase();
                showPkmDetails(pokeSearch);
                showPkmSpeciesDetails(pokeSearch);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
    public void showPkmDetails(String pokeSearch){
        showPkmSpeciesDetails(pokeSearch);

        PokeApiService service = retrofit.create(PokeApiService.class);
        Call<PokemonDetails> pokemonDetailsCall = service.getPkmDetails(pokeSearch);

        pokemonDetailsCall.enqueue(new Callback<PokemonDetails>() {
            @Override
            public void onResponse(Call<PokemonDetails> call, Response<PokemonDetails> response) {

                if (response.isSuccessful()) {

                    PokemonDetails pokemonDetails = response.body();
                    fillPokemonDetailsInfo(pokemonDetails);


                    pokeID = pokemonDetails.getId();
                    pokeName = pokemonDetails.getName();
                    Log.i("prova", "Numero Pkm Actual " + pokeID);
                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonDetails> call, Throwable t) {

                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }
    public void showPkmSpeciesDetails(String id){

        PokeApiService service = retrofit.create(PokeApiService.class);

        Call<SpeciesDetails> pokemonSpeciesDetailsCall = service.getSpeciesDetails(id);
        pokemonSpeciesDetailsCall.enqueue(new Callback<SpeciesDetails>() {
            @Override
            public void onResponse(Call<SpeciesDetails> call, Response<SpeciesDetails> response) {
                if (response.isSuccessful()) {
                    SpeciesDetails speciesDetails = response.body();
                    fillPokemonSpeciesDetailsInfo(speciesDetails);
                }
            }

            @Override
            public void onFailure(Call<SpeciesDetails> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }

    public void fillPokemonSpeciesDetailsInfo(SpeciesDetails speciesDetails){
        Genera g = speciesDetails.getGenera().get(7); //7 means English Language in the API
        Log.i("prova", "GENUS:  " +g.getGenus());
        txtSpecie.setText(" "+g.getGenus());
        pokemonWantedInfo.setGenera(g.getGenus());
        Flavor_text_entries f = speciesDetails.getFlavor_text_entries().get(1); //It means the first description in English
        pokemonWantedInfo.setFlavor_text(f.getFlavor_text());
        Log.i("prova", "FlavorText " + f.getFlavor_text());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txtDesc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            txtDesc.setText(f.getFlavor_text().replaceAll("\n", " "));
        }
    }
    public void fillPokemonDetailsInfo(PokemonDetails pokemonDetails){

        txtNameNumber.setText(" "+ pokemonDetails.getId()+" "+pokemonDetails.getName()); //SEPARA ESTO
        pokemonWantedInfo.setName(pokemonDetails.getName());
        pokemonWantedInfo.setId(pokemonDetails.getId());

        String redHeigth = "<font color='red'>Height: </font>"+pokemonDetails.getHeight();
        txtHeight.setText(Html.fromHtml(redHeigth), TextView.BufferType.SPANNABLE);
        pokemonWantedInfo.setHeight(pokemonDetails.getHeight());

        String redWeigth ="<font color='red'>Weight: </font>"+pokemonDetails.getWeight();
        txtWeight.setText(Html.fromHtml(" "+redWeigth), TextView.BufferType.SPANNABLE);
        pokemonWantedInfo.setHeight(pokemonDetails.getWeight());

        Glide.with(pokedexInfo_context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + pokemonDetails.getId() + ".png")
                .centerCrop()
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgPok);

        Log.i("Peso", "Texto Peso " + txtWeight.getText());
        Log.i("Altura", "Texto Number " + txtHeight.getText());

        //Pruebas Objetos Details, funciona bien

        for (int i = 0; i < pokemonDetails.getTypes().size(); i++) {
            Types t= pokemonDetails.getTypes().get(i);
            Log.i("prova", "Nombre Typo " + t.getSlot());
            Log.i("prova", "Nombre Url Type " + t.getType().getName());
            setImgType(t.getType().getName(), imgType[i]);
            //Reset the second type if it doesnt have anyone
            if(i<1){
                pokemonWantedInfo.setType1(t.getType().getName());
                imgType[1].setImageResource(android.R.color.transparent);
                pokemonWantedInfo.setType2("None");
            }else{
                pokemonWantedInfo.setType2(t.getType().getName());
            }
        }
    }

    public void getClickType(View v){
        Bundle extras = new Bundle();
        extras.putString("Wanted_type", v.getTag().toString());

        Intent intent = new Intent(pokedexInfo_context, TypesList.class);
        intent.putExtras(extras);
        startActivity(intent);
        Log.i("logTest", ""+ v.getTag());
    }
    private void open_CustomDialog(){
        final AlertDialog diag = new AlertDialog.Builder(pokedexInfo_context)
                .setView(R.layout.type_images)
                .create();
        diag.show();

    }
    private void type_research(String type){ //Not Used
        PokeApiService service = retrofit.create(PokeApiService.class);

        Call<TypeResearch> pokemonTypeResearchCall = service.getTypeResearch(type);
        pokemonTypeResearchCall.enqueue(new Callback<TypeResearch>() {
            @Override
            public void onResponse(Call<TypeResearch> call, Response<TypeResearch> response) {
                if (response.isSuccessful()) {
                    TypeResearch typeResearch = response.body();
                    ArrayList<WantedTypePoke> TypeWantedPokes = typeResearch.getPokemon();
                    Log.i("prova", "Pokemon con ese type " + TypeWantedPokes.size());
                    Log.i("prova", "Pokemon con ese type " + typeResearch.getPokemon().size());
                    Log.i("prova", "Nombre del tipo " + typeResearch.getName());
                    Log.i("prova", "Id del tipo " + typeResearch.getId());

                    for (int i = 0; i < typeResearch.getPokemon().size(); i++) {
                        WantedTypePoke poke= typeResearch.getPokemon().get(i);
                        Log.i("prova", "Nombre del Poke " + poke.getPokemon().getName());
                        Log.i("prova", "Url del poke " + poke.getPokemon().getUrl());

                    }
                }
            }

            @Override
            public void onFailure(Call<TypeResearch> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });


    }
    private void setImgType(String typeName, ImageView imgview){
        switch(typeName){
            case "water":
                imgview.setImageResource(R.drawable.water);
                break;
            case "fire":
                imgview.setImageResource(R.drawable.fire);
                break;
            case "bug":
                imgview.setImageResource(R.drawable.bug);
                break;
            case "fairy":
                imgview.setImageResource(R.drawable.fairy);
                break;
            case "electric":
                imgview.setImageResource(R.drawable.electric);
                break;
            case "dragon":
                imgview.setImageResource(R.drawable.dragon);
                break;
            case "dark":
                imgview.setImageResource(R.drawable.dark);
                break;
            case "fighting":
                imgview.setImageResource(R.drawable.fighting);
                break;
            case "flying":
                imgview.setImageResource(R.drawable.flying);
                break;
            case "ghost":
                imgview.setImageResource(R.drawable.ghost);
                break;
            case "grass":
                imgview.setImageResource(R.drawable.grass);
                break;
            case "ground":
                imgview.setImageResource(R.drawable.ground);
                break;
            case "normal":
                imgview.setImageResource(R.drawable.normal);
                break;
            case "poison":
                imgview.setImageResource(R.drawable.poison);
                break;
            case "psychic":
                imgview.setImageResource(R.drawable.psychic);
                break;
            case "rock":
                imgview.setImageResource(R.drawable.rock);
                break;
            case "steel":
                imgview.setImageResource(R.drawable.steel);
                break;
            case "ice":
                imgview.setImageResource(R.drawable.ice);
                break;
            default:
                imgview.setImageResource(android.R.color.transparent);
        }
    }

}

