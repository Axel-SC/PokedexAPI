package com.example.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pokedex.Fragments.MovesFragment;
import com.example.pokedex.Fragments.PokeInfoFragment;
import com.example.pokedex.Fragments.TypesChartFragment;
import com.example.pokedex.models.PokemonDetails;
import com.example.pokedex.models.PokemonWantedInfo;
import com.example.pokedex.models.Pokemon_For_Firebase;
import com.example.pokedex.models.Stats;
import com.example.pokedex.models.Types;
import com.example.pokedex.pokeapi.PokeApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PokemonInfoNavigation extends AppCompatActivity {
    PokeInfoFragment pokeInfoFragment = new PokeInfoFragment();
    MovesFragment movesFragment = new MovesFragment();
    TypesChartFragment typesChartFragment = new TypesChartFragment();
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference dbReference;
    private static MainActivity myContext;
    private List<Pokemon_For_Firebase> poke_Favourites;
    private List<Pokemon_For_Firebase> poke_Captured;
    private ImageView imgPokemon;
    private ImageView img_Type1;
    private ImageView img_Type2;
    private ImageView img_PokeBall;
    private TextView tv_PokeID;
    private TextView tv_PokemonName;
    private TextView tv_SpeciesName;
    private LottieAnimationView lottieHeart;
    private Boolean like = false;
    private Boolean clickedFav = false;
    private Boolean clickedPkBall = false;
    private String pokeID;
    private String pokeName;
    private String speciesName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_info_navigation);

        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        dbReference = rootNode.getReference("Users");

        poke_Favourites = new ArrayList<Pokemon_For_Firebase>();
        poke_Captured = new ArrayList<Pokemon_For_Firebase>();

        imgPokemon = findViewById(R.id.imgPokemon);
        img_Type1 = findViewById(R.id.img_Type1);
        img_Type2 = findViewById(R.id.img_Type2);
        tv_PokeID = findViewById(R.id.tv_PokeID);
        tv_PokemonName = findViewById(R.id.tv_Description);
        tv_SpeciesName = findViewById(R.id.tv_Height);
        lottieHeart = findViewById(R.id.lottieHeart);
        img_PokeBall = findViewById(R.id.img_PokeBall);

        //Get all the info needed
        Bundle extras = getIntent().getExtras();
        String email = extras.getString("Email");
        Log.i("PruebaLogin", "Nombre del pokemon: " + email);

        Bundle InfoPokeSent = getIntent().getExtras();
        PokemonWantedInfo pokemonWantedInfo = (PokemonWantedInfo) InfoPokeSent.getSerializable("PokemonWantedInfo");
        pokeID = pokemonWantedInfo.getId();
        pokeName = pokemonWantedInfo.getName();

        Bundle pokemonInfoExtras = new Bundle();
        pokemonInfoExtras.putSerializable("PokemonWantedInfo", pokemonWantedInfo);



        //Set text to the textviews
        tv_PokemonName.setText(pokeName.toUpperCase());
        tv_PokeID.setText(pokeID.toUpperCase());
        tv_SpeciesName.setText(pokemonWantedInfo.getGenera().toUpperCase());
        setImgType(pokemonWantedInfo.getType1(), img_Type1);
        setImgType(pokemonWantedInfo.getType2(), img_Type2);

        Log.i("Serializable", "Nombre " + pokemonWantedInfo.getName());
        Log.i("Serializable", "ID " + pokemonWantedInfo.getId());
        Log.i("Serializable", "SpeciesName " + pokemonWantedInfo.getGenera());

        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + pokemonWantedInfo.getId() + ".png")
                .centerCrop()
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this.imgPokemon);

        //Load always the first fragment
        loadFragment(pokeInfoFragment);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        lottieHeart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(clickedFav==false){
                    lottieHeart.setAnimation(R.raw.like);
                    lottieHeart.playAnimation();
                    addFavourites2(pokeID, pokeName);
                    clickedFav=true;
                }else{
                    removeFavourites(pokeID, pokeName);
                    lottieHeart.setImageResource(R.drawable.twitter_like_negro_v1);
                    clickedFav=false;
                }
            }
        });

        img_PokeBall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(clickedPkBall==false){
                    img_PokeBall.setImageResource(R.drawable.pokeball_captured2);
                    addCaptured(pokeID, pokeName);
                    clickedPkBall=true;
                }else{
                    img_PokeBall.setImageResource(R.drawable.pokeball_empty);
                    removeCaptured(pokeID, pokeName);
                    clickedPkBall=false;
                }
            }
        });

    }

    private Boolean likeAnimation(LottieAnimationView LottieImg, int animation, boolean like ){
        if(!like){
            LottieImg.setAnimation(animation);
            LottieImg.playAnimation();
        } else{
            LottieImg.setImageResource(R.drawable.twitter_like_negro_v1);
        }
        return !like;
    }

    private void addFavourites2(String poke_ID, String poke_Name){

        Pokemon_For_Firebase pokemon_for_firebase = new Pokemon_For_Firebase();
        pokemon_for_firebase.setId(poke_ID);
        pokemon_for_firebase.setName(poke_Name);
        poke_Favourites.add(pokemon_for_firebase);

        dbReference.child(auth.getUid()).child("Favourites").child(poke_Name).setValue(pokemon_for_firebase);
        Log.i("Firebase", "Pokemon Stored in Favourites");
/*
        dbReference.child(auth.getUid()).child("Favourites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e("DatosDB", ""+snapshot.getValue());
                    Pokemon_For_Firebase pokemon_for_firebase = snapshot.getValue(Pokemon_For_Firebase.class);
                    poke_Favourites.add(pokemon_for_firebase);
                }
                for(Pokemon_For_Firebase p: poke_Favourites){
                    Log.e("InfoDB", ""+p.getId());
                    Log.e("InfoDB", ""+p.getName());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


        /*
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("DatosDB", ""+snapshot.getValue());
                    Pokemon_For_Firebase pokemon_for_firebase = snapshot.getValue(Pokemon_For_Firebase.class);
                    poke_Favourites.add(pokemon_for_firebase);
                }
                for(Pokemon_For_Firebase p: poke_Favourites){
                    Log.e("InfoDB", ""+p.getId());
                    Log.e("InfoDB", ""+p.getName());
                }*/

    }
    private void removeFavourites(String poke_ID, String poke_Name){
        Pokemon_For_Firebase pokemon_for_firebase = new Pokemon_For_Firebase();
        pokemon_for_firebase.setId(poke_ID);
        pokemon_for_firebase.setName(poke_Name);
        poke_Favourites.remove(pokemon_for_firebase);
        dbReference.child(auth.getUid()).child("Favourites").child(poke_Name).removeValue();
        Log.i("Firebase", "Pokemon REMOVED in Favourites");
    }
    private void addCaptured(String poke_ID, String poke_Name){
        Pokemon_For_Firebase pokemon_for_firebase = new Pokemon_For_Firebase();
        pokemon_for_firebase.setId(poke_ID);
        pokemon_for_firebase.setName(poke_Name);
        poke_Captured.add(pokemon_for_firebase);
        dbReference.child(auth.getUid()).child("Captured").setValue(poke_Captured);
        Log.i("Firebase", "Pokemon Stored in Favourites");
    }
    private void removeCaptured(String poke_ID, String poke_Name){
        Pokemon_For_Firebase pokemon_for_firebase = new Pokemon_For_Firebase();
        pokemon_for_firebase.setId(poke_ID);
        pokemon_for_firebase.setName(poke_Name);
        poke_Captured.remove(pokemon_for_firebase);
        dbReference.child(auth.getUid()).child("Captured").setValue(poke_Favourites);
        Log.i("Firebase", "Pokemon Stored in Favourites");
    }
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()){
                case R.id.pokeInfoFragment:
                    loadFragment(pokeInfoFragment);
                    return true;
                case R.id.movesFragment:
                    loadFragment(movesFragment);
                    return true;
                case R.id.typesChartFragment:
                    loadFragment(typesChartFragment);
                    return true;
            }
            return false;
        }
    };
    public void loadFragment(Fragment fragment){
        Bundle pokeID_Bundle= new Bundle();
        pokeID_Bundle.putString("POKE_ID", pokeID);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(pokeID_Bundle);
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }

    private void setImgType(String typeName, ImageView imgview){
        switch(typeName){
            case "water":
                imgview.setImageResource(R.drawable.type_water);
                break;
            case "fire":
                imgview.setImageResource(R.drawable.type_fire);
                break;
            case "bug":
                imgview.setImageResource(R.drawable.type_bug);
                break;
            case "fairy":
                imgview.setImageResource(R.drawable.type_fairy);
                break;
            case "electric":
                imgview.setImageResource(R.drawable.type_electric);
                break;
            case "dragon":
                imgview.setImageResource(R.drawable.type_dragon);
                break;
            case "dark":
                imgview.setImageResource(R.drawable.type_dark);
                break;
            case "fighting":
                imgview.setImageResource(R.drawable.type_fighting);
                break;
            case "flying":
                imgview.setImageResource(R.drawable.type_flying);
                break;
            case "ghost":
                imgview.setImageResource(R.drawable.type_ghost);
                break;
            case "grass":
                imgview.setImageResource(R.drawable.type_grass);
                break;
            case "ground":
                imgview.setImageResource(R.drawable.type_ground);
                break;
            case "normal":
                imgview.setImageResource(R.drawable.type_normal);
                break;
            case "poison":
                imgview.setImageResource(R.drawable.type_poison);
                break;
            case "psychic":
                imgview.setImageResource(R.drawable.type_psychic);
                break;
            case "rock":
                imgview.setImageResource(R.drawable.type_rock);
                break;
            case "steel":
                imgview.setImageResource(R.drawable.type_steel);
                break;
            case "ice":
                imgview.setImageResource(R.drawable.type_ice);
                break;
            default:
                imgview.setImageResource(android.R.color.transparent);
        }
    }
}