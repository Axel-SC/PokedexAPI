package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.pokedex.Adapters.GridViewAdapter;
import com.example.pokedex.Adapters.ListaPokemonAdapter;
import com.example.pokedex.Adapters.typesGridView;
import com.example.pokedex.models.Flavor_text_entries;
import com.example.pokedex.models.Genera;
import com.example.pokedex.models.Moves;
import com.example.pokedex.models.Pokemon;
import com.example.pokedex.models.PokemonDetails;
import com.example.pokedex.models.PokemonRespuesta;
import com.example.pokedex.models.SpeciesDetails;
import com.example.pokedex.models.TypeResearch;
import com.example.pokedex.models.Types;
import com.example.pokedex.models.WantedTypePoke;
import com.example.pokedex.pokeapi.PokeApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity {

    public static Activity act;
    public static TextView txtDisplay;
    public static TextView txtNameNumber;
    public static TextView txtSpecie;
    public static TextView txtDesc;
    public static TextView txtHeight;
    public static TextView txtWeight;
    private GridView types_gridView;

    private GridAdapter gridAdapter;
    private List<typesGridView> typesList;

    public static ImageView imgPok;
    private Retrofit retrofit;
    private static final String TAG = "POKEDEX";
    private RequestBuilder<PictureDrawable> requestBuilder;
    private String numPkx = "1";
    private int numAux;
    private int min=1;
    private int max=898;
    private String urlSprites;
    private String tipo;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;
    private int offset;
    public static ImageView[] imgType;

    private static MainActivity myContext;

    public MainActivity() {
        myContext = this;
    }

    public static MainActivity getInstance() {
        return myContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        offset = 0;
        act = this;

        imgType = new ImageView[2];

        //Asigno los layouts
        txtDisplay = findViewById(R.id.txtDisplay);
        imgPok = findViewById(R.id.imgPok);
        imgType[0] = findViewById(R.id.imgType0);
        imgType[1] = findViewById(R.id.imgType1);
        txtNameNumber = this.findViewById(R.id.txtNameNumber);
        txtSpecie = findViewById(R.id.txtSpecie);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        txtDesc = findViewById(R.id.txtDesc);

        showPkmDetails(null, numPkx);
        showPkmSpeciesDetails(numPkx);

        final Button btnPokedex = this.findViewById(R.id.btn_Pokedex);
        btnPokedex.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent toPokedex = new Intent(MainActivity.getInstance(), PokedexList.class);
                MainActivity.getInstance().startActivity(toPokedex);

            }
        });


        final Button btnRight = findViewById(R.id.btnRight);
        btnRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numAux = Integer.parseInt(numPkx);
                if(numAux<max){ //Thats the max Id for a pokemon
                    numAux++;
                    numPkx=String.valueOf(numAux);
                    showPkmDetails(null, numPkx);
                }else{
                    Toast.makeText(MainActivity.getInstance(), "¡This is the last Pokemon!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final Button btnLeft = findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numAux = Integer.parseInt(numPkx);
                if(numAux>min){ //Min Id for a Pokemon
                    numAux--;
                    numPkx=String.valueOf(numAux);
                    showPkmDetails(null, numPkx);
                }else{
                    Toast.makeText(MainActivity.getInstance(), "¡This is the first Pokemon!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showTxtSearch();
            }
        });

        ImageButton btnTypes = findViewById(R.id.btnTypes);
        btnTypes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tipo = "fire";
                type_research(tipo);
                open_CustomDialog();
            }
        });

    }

    public void showTxtSearch() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Search a Pokemon");

        final EditText input = new EditText(this);
        input.setHint("Pokemon");
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pokSearch = input.getText().toString().toLowerCase();
                showPkmDetails(pokSearch, numPkx);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }
/*
    private void showPkmList(String name, int id) { //Creo que esto no lo necesito, revisar
        PokeApiService service = retrofit.create(PokeApiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall = service.obtenerListaPokemon(20, offset);

        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {

                if (response.isSuccessful()) {

                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonRespuesta.getResults();

                    for (int i = 0; i < listaPokemon.size(); i++) {
                        Pokemon p = listaPokemon.get(i);
                        if (name.equals(p.getName())||id==p.getSpriteNumber()) {
                            String name = p.getName();
                            String number = String.valueOf(p.getSpriteNumber());
                            String nameNumber = number + " " + name;
                            txtWeight.setText(p.getWeight());
                            Log.i("prova2", "Texto Peso " + txtWeight.getText());
                            txtNameNumber.setText(nameNumber);
                            Log.i("prova2", "Texto Number " + txtWeight.getText());
                            Glide.with(MainActivity.getInstance())
                                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + p.getSpriteNumber() + ".png")
                                    .centerCrop()
                                    .transition(withCrossFade())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(MainActivity.getInstance().imgPok);
                        }

                    }


                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {

                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }*/
    private void showPkmDetails(String name, String id){
        showPkmSpeciesDetails(id);
        String clave="0";

        PokeApiService service = retrofit.create(PokeApiService.class);
        if(name==null){
            clave = id;
        }else if(name!=null){
            clave = name;
        }
        Call<PokemonDetails> pokemonDetailsCall = service.getPkmDetails(clave);

        pokemonDetailsCall.enqueue(new Callback<PokemonDetails>() {
            @Override
            public void onResponse(Call<PokemonDetails> call, Response<PokemonDetails> response) {

                if (response.isSuccessful()) {

                    PokemonDetails pokemonDetails = response.body();

                    txtNameNumber.setText(" "+ pokemonDetails.getId()+" "+pokemonDetails.getName());

                    String redHeigth = "<font color='red'>Height: </font>"+pokemonDetails.getHeight();
                    txtHeight.setText(Html.fromHtml(redHeigth), TextView.BufferType.SPANNABLE);

                    String redWeigth ="<font color='red'>Weight: </font>"+pokemonDetails.getWeight();
                    txtWeight.setText(Html.fromHtml(" "+redWeigth), TextView.BufferType.SPANNABLE);

                    Glide.with(MainActivity.getInstance())
                            .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + pokemonDetails.getId() + ".png")
                            .centerCrop()
                            .transition(withCrossFade())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(MainActivity.getInstance().imgPok);

                    Log.i("Peso", "Texto Peso " + txtWeight.getText());
                    Log.i("Altura", "Texto Number " + txtHeight.getText());

                    //Pruebas Objetos Details, funciona bien
                    pokemonDetails.getSpecies().getName();
                    pokemonDetails.getSpecies().getUrl();
                    Log.i("prova", "Nombre Specie " + pokemonDetails.getSpecies().getName());
                    Log.i("prova", "Url Specie " + pokemonDetails.getSpecies().getUrl());//Funciona
                    for (int i = 0; i < pokemonDetails.getTypes().size(); i++) {
                        Types t= pokemonDetails.getTypes().get(i);
                        Log.i("prova", "Nombre Nombre Typo " + t.getSlot());
                        Log.i("prova", "Nombre Url Type " + t.getType().getName());
                        setImgType(t.getType().getName(), imgType[i]);
                        //Reset the second type if it doesnt have anyone
                        if(i<1){
                            imgType[1].setImageResource(android.R.color.transparent);
                        }
                    }
                    numPkx = pokemonDetails.getId(); //Obtengo siempre la id actual del pokemon que se esta mostrando
                    Log.i("prova", "Numero Pkm Actual " + numPkx);
                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonDetails> call, Throwable t) {

                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }private void showPkmSpeciesDetails(String id){

        PokeApiService service = retrofit.create(PokeApiService.class);

        Call<SpeciesDetails> pokemonSpeciesDetailsCall = service.getSpeciesDetails(id);
        pokemonSpeciesDetailsCall.enqueue(new Callback<SpeciesDetails>() {
            @Override
            public void onResponse(Call<SpeciesDetails> call, Response<SpeciesDetails> response) {
                if (response.isSuccessful()) {
                    SpeciesDetails speciesDetails = response.body();
                    Genera g = speciesDetails.getGenera().get(7); //7 means Enlish Language in the API
                    Log.i("prova", "GENUS:  " +g.getGenus());
                    txtSpecie.setText(" "+g.getGenus());
                    //Hacer un for que recorra todos los textox y meta en un array todos lo que tengan en o sume un contador para saber cuantos tiene

                    int randomNum = new Random().nextInt((16 - 0) + 0); //0-16 means all the entries for English
                    Log.i("prova", "NumRandom " + randomNum);
                    Flavor_text_entries f = speciesDetails.getFlavor_text_entries().get(randomNum);
                    Log.i("prova", "FlavorText " + f.getFlavor_text());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        txtDesc.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                        txtDesc.setText(f.getFlavor_text().replaceAll("\n", " "));
                    }

                }
            }

            @Override
            public void onFailure(Call<SpeciesDetails> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
}
    public void getClickType(View v){
        Bundle extras = new Bundle();
        extras.putString("Wanted_type", v.getTag().toString());


        Intent intent = new Intent(this, TypesList.class);
        intent.putExtras(extras);
        startActivity(intent);
        Log.i("logTest", ""+ v.getTag());
    }
        private void open_CustomDialog(){
            final AlertDialog diag = new AlertDialog.Builder(this)
                    .setView(R.layout.type_images)
                    .create();
            diag.show();
            /*
            Dialog dialog = new Dialog(this);
            dialog.setTitle("Pokemon Types: ");
            dialog.setContentView(R.layout.type_images);
            dialog.show();*/



        /*
 types_gridView = (GridView) findViewById(R.id.types_GridView);
            gridAdapter = new GridAdapter(this);
            types_gridView.setAdapter(gridAdapter);
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.types_gridview);

            dialog.create();
            dialog.show();
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.types_gridview);
            ImageView imageView=dialog.findViewById(R.id.fotoImgView);
            dialog.show();



        AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Select Types");
            LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customDialog = inflater.inflate(R.layout.types_gridview, null, false);

            GridView gridView = customDialog.findViewById(R.id.types_GridView);
            GridViewAdapter gridViewAdapter = new GridViewAdapter(this, R.layout.item_pokemon, typesList);
            gridView.setAdapter(gridViewAdapter);
            alert.setView(customDialog);

            AlertDialog dialog = alert.create();
            dialog.show();
/*
            final EditText input = new EditText(this);
            input.setHint("Pokemon");
            alert.setView(input);*/

        }
        private void type_research(String type){
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

                        }/*
                        for (int i = 0; i < typeResearch.getMoves().size(); i++) {
                            Moves m = typeResearch.getMoves().get(i);
                            Log.i("prova", "Moves con ese type: " + m.getName());
                        }*/
                    }
                }

                @Override
                public void onFailure(Call<TypeResearch> call, Throwable t) {
                    Log.e(TAG, " onFailure: " + t.getMessage());
                }
            });


        }
    public List<typesGridView> getTypesList() {

        typesList = new ArrayList<>();
        typesList.add(new typesGridView(R.drawable.fire, "Fire"));
        typesList.add(new typesGridView(R.drawable.water, "Water"));
        typesList.add(new typesGridView(R.drawable.grass, "Grass"));
        typesList.add(new typesGridView(R.drawable.electric, "Electric"));
        typesList.add(new typesGridView(R.drawable.dragon, "Dragon"));

        return typesList;
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