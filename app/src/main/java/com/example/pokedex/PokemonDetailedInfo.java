package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;

public class PokemonDetailedInfo extends AppCompatActivity {
    Boolean like = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detailed_info);


        LottieAnimationView likePkm = findViewById(R.id.likePokemonView);
        Bundle extras = getIntent().getExtras();
        String pokeID = extras.getString("PokeID");
        Log.e("PruebaId", "Nombre del pokemon: " + pokeID);
        // MainActivity mainActivity = new MainActivity();
        // mainActivity.showPkmDetails(null, pokeID);

        MainActivity.getInstance().showPkmDetails(null, pokeID); //Llamada correcta, metodo incorrecto. Tendria que modificarlo
        MainActivity.getInstance().showPkmSpeciesDetails(pokeID);


        TextView txtNumPkm = findViewById(R.id.txtNumPkm);
        txtNumPkm.setText(pokeID);
        likePkm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
          like = likeAnimation(likePkm, R.raw.like, like);

            }
        });
    }private Boolean likeAnimation(LottieAnimationView LottieImg, int animation, boolean like ){
        if(!like){
            LottieImg.setAnimation(animation);
            LottieImg.playAnimation();

        } else{
            LottieImg.setImageResource(R.drawable.twitter_like_negro);
        }
    return !like;
    }
}