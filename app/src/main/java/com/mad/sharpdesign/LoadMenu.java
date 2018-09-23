package com.mad.sharpdesign;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoadMenu extends AppCompatActivity {
    private Button mButtonURL, mButtonFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_menu);
        mButtonURL = (Button) findViewById(R.id.btn_loadURL);
        final AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Enter or paste URL");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_url, (ViewGroup) findViewById(android.R.id.content), false);
        final TextInputEditText input = (TextInputEditText) viewInflated.findViewById(R.id.input_URL);
        builder.setView(viewInflated);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); 
            }
        });
        //change this
        builder.setNeutralButton("Paste from Clipboard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mButtonURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.show();
            }
        });
    }
}
