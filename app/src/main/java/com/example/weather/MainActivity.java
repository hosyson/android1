package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    TextView next_24_hours;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

    floatingActionButton = findViewById(R.id.floatingActionButton4);
    floatingActionButton.setOnClickListener(view -> {
        Intent intent = new Intent(MainActivity.this, AddCity.class);
        startActivity(intent);
    });



    next_24_hours = findViewById(R.id.next_24_hours);
    next_24_hours.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this,Weather24h.class);
            intent.putExtra("name","city");
            startActivity(intent);
        }
    });
    }
}
