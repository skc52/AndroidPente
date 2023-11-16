package com.example.pente_java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    /**
     * Displays a dialog to prompt the user to enter a file name and loads the selected game data.
     * Assistance received - https://www.tutorialspoint.com/how-to-save-files-on-external-storage-in-android
     */
    private void askFileNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.ask_filename, null);
        builder.setView(dialogView);


        File directory = new ContextWrapper(getApplicationContext()).getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File[] filesList = directory.listFiles();

        // Create a list of filenames ending with ".txt"
        Vector<String> filesNames = new Vector<>();
        for (File file : filesList) {
            String fileName = file.getName();
            if (fileName.endsWith(".txt")) {
                filesNames.add(fileName);
            }
        }

        // Convert the vector to an array
        final String[] fileNamesArray = new String[filesNames.size()];
        filesNames.toArray(fileNamesArray);

        // Set up the spinner
        Spinner filenameSpinner = dialogView.findViewById(R.id.filenameSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fileNamesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filenameSpinner.setAdapter(adapter);

        TextView coinTossResultTextView = dialogView.findViewById(R.id.coinTossResult);
        Button loadGameButton = dialogView.findViewById(R.id.loadGameBtn);
        Button cancelSaveBtn = dialogView.findViewById(R.id.cancelSavingBtn);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        cancelSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        loadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                coinTossResultTextView.setVisibility(View.VISIBLE);

                String selectedFileName = filenameSpinner.getSelectedItem().toString();

                File txtFile = new File(directory, selectedFileName);

                StringBuilder text = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(txtFile));
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    coinTossResultTextView.setText(text);

                    Intent intent = new Intent(MainActivity.this, PenteBoard.class);
                    intent.putExtra("filename", selectedFileName);
                    intent.putExtra("gamedata", coinTossResultTextView.getText().toString());
                    startActivity(intent);
                } catch (Exception e) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error")
                            .setMessage("Failed to read the file.")
                            .setPositiveButton("OK", null)
                            .show();
//                    Toast.makeText(getApplicationContext(), "Failed to read the file.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }


    /**
     * Initializes the main activity layout and sets up onClick listeners for new game and load game buttons.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newGameBtn = findViewById(R.id.newGame);


        /**
         * Handles the click event for the "New Game" button, creating an intent to open the PenteBoard activity.
         */
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the new activity
                Intent intent = new Intent(MainActivity.this, PenteBoard.class);
                startActivity(intent);


            }
        });

        Button loadButton = findViewById(R.id.loadGame);

        /**
         * Handles the click event for the "Load Game" button, triggering the askFileNameDialog() method.
         */
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askFileNameDialog();
            }
        });

    }





}