package com.example.pente_java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOAD_FILE = 1;

    private void askFileNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.ask_filename, null);
        builder.setView(dialogView);


        EditText filenameEditText = dialogView.findViewById(R.id.filenameEditText);
        TextView coinTossResultTextView = dialogView.findViewById(R.id.coinTossResult);
        Button loadGameButton = dialogView.findViewById(R.id.loadGameBtn);
        Button cancelSaveBtn = dialogView.findViewById(R.id.cancelSavingBtn);
        loadGameButton.setEnabled(false);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        filenameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this example, but you must implement it.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check the text in filenameEditText and enable/disable loadGameButton accordingly
                if (charSequence.toString().trim().isEmpty()) {
                    loadGameButton.setEnabled(false);
                } else {
                    loadGameButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        cancelSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        loadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coinTossResultTextView.setVisibility(View.VISIBLE);
                // on below line we are checking the self permissions for reading sms.
                ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                // on below line creating a directory for file and specifying the file name.
                File directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                Log.e("TAG", "download is : " + directory.getAbsolutePath() + "" + directory);
                File txtFile = new File(directory,  filenameEditText.getText().toString()+ ".txt");
                // on below line creating a string builder.
                StringBuilder text = new StringBuilder();
                try {
                    // on below line creating and initializing buffer reader.
                    BufferedReader br = new BufferedReader(new FileReader(txtFile));
                    // on below line creating a string variable/
                    String line;
                    // on below line setting the data to text
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    coinTossResultTextView.setText(text);

                    Intent intent = new Intent(MainActivity.this, PenteBoard.class);
                    intent.putExtra("gamedata", coinTossResultTextView.getText().toString());
                    startActivity(intent);
                    // on below line handling the exception
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Fail to read the file..", Toast.LENGTH_SHORT).show();
                }

//                dialog.dismiss();
            }
        });


        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newGameBtn = findViewById(R.id.newGame);

//        // on below line creating and initializing variable for context wrapper.
//        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
//        // on below line creating a directory for file and specifying the file name.
//        File directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        // on below line creating a text file.
//        File txtFile = new File(directory, "game3" + ".txt");
//        // on below line writing the text to our file.
//        FileOutputStream fos = null;
//        String gameData = "Board:\n" +
//                "OOOWWBOOOOOOOOOOOOO\n" +
//                "OOWOOBOOOOOOOBBOOOO\n" +
//                "OOWOOOOOOOOOOOOOOOO\n" +
//                "OOBOOOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOBOOOOO\n" +
//                "OOOWOOOOOOOOOOOOOOO\n" +
//                "OOOOWOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "OOOOOOWOOOOOOOOOOOO\n" +
//                "OOOOOOOWOWOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "OOOOOBBOOBBOOOOOOOO\n" +
//                "OOOOOWOOOOOOOOOOOOO\n" +
//                "OOOOOWOOOOOOOOBOOOO\n" +
//                "OOBWWOOOOOOOOOOOOOO\n" +
//                "OOOOOOOOOOOOOOOOOOO\n" +
//                "\n" +
//                "Human:\n" +
//                "Captured pairs: 1\n" +
//                "Score: 0\n" +
//                "\n" +
//                "Computer:\n" +
//                "Captured pairs: 0\n" +
//                "Score: 0\n" +
//                "\n" +
//                "Next Player: Human - Black";
//
//
//        try {
//            fos = new FileOutputStream(txtFile);
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            osw.write(gameData);
//            osw.flush();
//            osw.close();
//            fos.close();
//            Toast.makeText(contextWrapper, "File write successful..", Toast.LENGTH_SHORT).show();
////            msgEdt.setText("");
//        } catch (Exception e) {
//            // on below line handling the exception.
//            e.printStackTrace();
//        }


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
                askFileNameDialog();
            }
        });

    }





}