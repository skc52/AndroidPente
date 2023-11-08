package com.example.pente_java;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOAD_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newGameBtn = findViewById(R.id.newGame);

        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the new activity
                Intent intent = new Intent(MainActivity.this, PenteBoard.class);
                startActivity(intent);


            }
        });

        Button loadButton = findViewById(R.id.loadGame);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilePicker();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // You can specify the MIME type of your saved game files here
        startActivityForResult(intent, REQUEST_CODE_LOAD_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOAD_FILE && resultCode == Activity.RESULT_OK) {
            Uri selectedFileUri = data.getData();
            loadGameFromUri(selectedFileUri);
        }
    }

    private void loadGameFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            // Handle the loaded game data (content) as needed
            String gameData = content.toString();
            // Update your game state with the loaded data
            // For Pente, you'd parse the game data to set the board configuration and player turns, for example.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}