package com.lightsys.audioapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class notesActivity extends AppCompatActivity {
    private EditText EditText1;
    private Intent inputIntent;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inputIntent = getIntent();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save("Note1.txt");
            }
        });

        EditText1 = findViewById(R.id.EditText1);
        EditText1.setText(Open(inputIntent.getStringExtra("filename")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void Save(String fileName) {
        try {
            if(note.getLesson() != null){
                note.getLesson().setNotes(note.getFilename());
            }
            //TODO Set note to a lesson if none present
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(fileName, 0));
            out.write(EditText1.getText().toString());
            out.close();
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public String Open(String fileName) {
        String content = "";
        if (FileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
    }

    public boolean FileExists(String filename) {
        File file = getBaseContext().getFileStreamPath(filename);
        return file.exists();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_all_notes) {
            Intent myIntent = new Intent(notesActivity.this, NoteSelect.class);
            notesActivity.this.startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}