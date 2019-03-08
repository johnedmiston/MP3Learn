package com.lightsys.audioapp;

import android.content.Intent;
import android.nfc.FormatException;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.media.MediaPlayer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class lessonActivity extends AppCompatActivity {

    public final int ADVANCE = 1;
    private View mMediaControlsView;    //Top (audio) controls

    //Media & Notes Declarations
    private MediaPlayer media = null;
    private ImageButton play;
    private ImageButton back10;
    private ImageButton fwd10;
    private ImageButton prev;
    private ImageButton next;
    private SeekBar seek;
    private EditText note;

    //Other Declarations
    private Intent inputIntent;
    private boolean mIsPlaying = false;
    private Runnable mRunnable;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inputIntent = getIntent();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lesson);
        mMediaControlsView = findViewById(R.id.media_controls);
        //mMaterialView = findViewById(R.id.material_fragment);

        //Enable back button
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Get lesson data from input Intent
        final String mp3 = inputIntent.getStringExtra("lesson_mp3");
        final String name = inputIntent.getStringExtra("lesson_name");
        final String course = inputIntent.getStringExtra("course_name");
        final boolean autoPlay = inputIntent.getBooleanExtra("autoplay",false);


        setTitle(name);
        Lesson lessonCheck = new Lesson(name,course);
        try {
            media = createMedia(mp3);
            DatabaseConnection db = new DatabaseConnection(getApplicationContext());
            int seekTime = db.getSeekTime(lessonCheck);
            media.seekTo(seekTime);
        } catch (FormatException e) {
            Toast.makeText(getApplicationContext(), "Incorrect file format", Toast.LENGTH_SHORT).show();
            mainActivity();
        }

        //If note already exists, load it
        //Otherwise, start a new note

        note = findViewById(R.id.notes_edit_text);
        DatabaseConnection database = new DatabaseConnection(getApplicationContext());
        note.setText(database.getNotes(lessonCheck));


        //If audio is playing, clicking play button pauses it
        //Otherwise, play audio
        play = findViewById(R.id.play_button);
        play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(media.isPlaying()){
                    media.pause();
                    play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    mIsPlaying = false;
                } else {
                    media.start();
                    play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    mIsPlaying = true;
                }
            }
        });

        //Seek to 10 seconds behind current position
        back10 = findViewById(R.id.back10_button);
        back10.setImageDrawable(getResources().getDrawable(R.drawable.ic_back10));
        back10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media.seekTo(media.getCurrentPosition() - 10000);
                seek.setProgress(media.getCurrentPosition());
            }
        });

        //Seek to 10 seconds ahead of current position
        fwd10 = findViewById(R.id.fwd10_button);
        fwd10.setImageDrawable(getResources().getDrawable(R.drawable.ic_fwd10));
        fwd10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media.seekTo(media.getCurrentPosition() + 10000);
                seek.setProgress(media.getCurrentPosition());
            }
        });

        //Seek back to start of audio
        prev = findViewById(R.id.previous_button);
        prev.setImageDrawable(getResources().getDrawable(R.drawable.ic_prev));
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media.seekTo(0);
                seek.setProgress(media.getCurrentPosition());
            }
        });

        //Seek to end of audio
        //TODO: When end of audio reached, prompt to load next lesson
        next = findViewById(R.id.next_button);
        next.setImageDrawable(getResources().getDrawable(R.drawable.ic_next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media.seekTo(media.getDuration());
                seek.setProgress(media.getCurrentPosition());
                Intent ret = new Intent();
                ret.putExtra("course_name",course);
                ret.putExtra("lesson_name",name);
                setResult(ADVANCE,ret);
                finish();
            }
        });

        seek = findViewById(R.id.seek_bar);
        initSeekBar();
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    media.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mIsPlaying){
                    media.pause();  //Only pause if currently playing
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mIsPlaying){
                    media.start();  //Only resume if previously playing
                }
            }
        });

        //This will fire when the end of the media is encountered.
        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Lesson update = new Lesson();
                update.setName(inputIntent.getStringExtra("lesson_name"));
                update.setCourse(inputIntent.getStringExtra("course_name"));
                update.setSeekTime(0);
                update.setNotes(getNote());
                DatabaseConnection db = new DatabaseConnection(getApplicationContext());
                db.updateLesson(update);
                Intent ret = new Intent();
                ret.putExtra("course_name",course);
                ret.putExtra("lesson_name",name);
                setResult(ADVANCE,ret);
                finish();
            }
        });
        if(autoPlay){
            media.start();
            play.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            mIsPlaying = true;
        }
    }

    //Creates and prepares media to be played
    //Throws FormatException if file is not an mp3
    private MediaPlayer createMedia(String mp3) throws FormatException {
        if(mp3.contains(".mp3")) {
            String file = mp3.replace(".mp3", "");
            int id = getResources().getIdentifier(file,"raw", getPackageName());
            MediaPlayer mediaPlayer = MediaPlayer.create(lessonActivity.this, id);
            return mediaPlayer;
        } else {
            throw new FormatException();
        }
    }

    //Initialize SeekBar to auto-update position with audio
    protected void initSeekBar(){
        seek.setMax(media.getDuration());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(media != null) {
                    seek.setProgress(media.getCurrentPosition());
                }
                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }
    
    //Returns from lesson to MainActivity
    //and closes current lesson
    private void mainActivity() {
        Intent main = new Intent(lessonActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    //Open menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lesson, menu);
        return true;
    }

    //Menu item actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO: Description
        if (id == R.id.action_notes) {
            Toast.makeText(this, "Take Some Good Notes", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //When lesson activity closes, save information
    //and close MediaPlayer before exiting
    @Override
    protected void onStop() {
        //If we close before the audio is finished, save current position
        int currentPosition = media.getCurrentPosition();
        DatabaseConnection db = new DatabaseConnection(getApplicationContext());
        Lesson update = new Lesson();
        if(currentPosition != media.getDuration()){
            update.setName(inputIntent.getStringExtra("lesson_name"));
            update.setCourse(inputIntent.getStringExtra("course_name"));
            update.setSeekTime(currentPosition);
        }else{
            //Executes when you have finished
            update.setName(inputIntent.getStringExtra("lesson_name"));
            update.setCourse(inputIntent.getStringExtra("course_name"));
            update.setSeekTime(0);
            update.setNotes(getNote());
            db.updateLesson(update);
        }
        //TODO: Add stuff for notes
        update.setNotes(getNote());
        //Put it in the DB
        db.updateLesson(update);
        media.release();
        media = null;
        super.onStop();
    }
    public String getNote(){
        return note.getText().toString();
    }
}
