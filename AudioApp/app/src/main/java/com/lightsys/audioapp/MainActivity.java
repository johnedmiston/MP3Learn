package com.lightsys.audioapp;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    ArrayList Courses;
    ArrayList Notes;
    int selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getCourseData();
        getNotesData();
        initCourseRecyclerView();
        initNotesRecyclerView();
    }

    private void getCourseData() {
        Courses = new ArrayList<Course>();//init the Array List

        //This section grabs the data from the directory file.
        String input = "";
        InputStream temp = getApplicationContext().getResources().openRawResource(R.raw.file_dir);
        try{
            Scanner readFile = new Scanner(temp);
            while(readFile.hasNextLine()){
                input += readFile.nextLine();
            }
        }
        catch(Exception e){

        }
        DatabaseConnection db = new DatabaseConnection(this.getApplicationContext());
        //Now we populate the Courses object
        while(input.indexOf("<course>")>=0){//look for a course
            String courseSubstring = input.substring(input.indexOf("<course>")+8,input.indexOf("</course>")).trim();//isolate the course
            Course newCourse = new Course(courseSubstring.substring(courseSubstring.indexOf("name:")+5,courseSubstring.indexOf(";")));
            courseSubstring = courseSubstring.substring(newCourse.getName().length()+5).trim();
            while(courseSubstring.indexOf("<lesson>")>=0){//look for a lesson
                Lesson newLesson = new Lesson();//make new lesson
                newLesson.setCourse(newCourse.getName());
                String lessonSubstring = courseSubstring.substring(courseSubstring.indexOf("<lesson>")+8,courseSubstring.indexOf("</lesson>"));
                if(lessonSubstring.indexOf("name:")>=0){
                    int startPoint = lessonSubstring.indexOf("name:")+5;
                    newLesson.setName(lessonSubstring.substring(startPoint,lessonSubstring.indexOf(";",startPoint)));
                }
                if(lessonSubstring.indexOf("mp3:")>=0){
                    int startPoint = lessonSubstring.indexOf("mp3:")+4;
                    newLesson.setMp3(lessonSubstring.substring(startPoint,lessonSubstring.indexOf(";",startPoint)));
                }
                if(lessonSubstring.indexOf("text:")>=0){
                    int startPoint = lessonSubstring.indexOf("text:")+5;
                    newLesson.setTextData(lessonSubstring.substring(startPoint,lessonSubstring.indexOf(";",startPoint)));
                }
                db.addLesson(newLesson);
                courseSubstring = courseSubstring.substring(courseSubstring.indexOf("</lesson>")+9).trim();
                newCourse.addLesson(newLesson);//put lesson in course
            }
            Courses.add(newCourse);//add the course to courses
            //refind the course
            courseSubstring = input.substring(input.indexOf("<course>"),input.indexOf("</course")+9);//isolate the course
            //remove it from the other screen

            input = input.substring(courseSubstring.length());
        }
    }

    private void getNotesData() {
        Notes = new ArrayList<Note>();
        Note sample = new Note("Hi Note","Note1.txt");
        Notes.add(sample);
    }

    private void initCourseRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_course);
        courseRecyclerView adapter = new courseRecyclerView(Courses, this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void initLessonRecyclerView(Course c){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_lesson);
        lessonRecyclerView adapter = new lessonRecyclerView(c.lessons, this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initNotesRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_note); //TODO: Load notes from DB
        notesRecyclerView adapter = new notesRecyclerView(Notes, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //Open menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    //Menu item actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Put Action Code here when they select it.
        }
        else if(id == R.id.action_about){
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        else if(id == R.id.action_all_notes){
            minimizeCourses();
            minimizeLessons();
            expandNotes();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void minimizeCourses() {
        LinearLayout courses = findViewById(R.id.content_courses);
        courses.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0,0);
        courses.setLayoutParams(size);
    }
    
    public void expandCourses(){
        LinearLayout courses = findViewById(R.id.content_courses);
        courses.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        courses.setLayoutParams(size);
    }
    
    public void minimizeLessons() {
        LinearLayout lessons = findViewById(R.id.content_lessons);
        lessons.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0,0);
        lessons.setLayoutParams(size);
    }
    
    public void expandLessons(){
        LinearLayout lessons = findViewById(R.id.content_lessons);
        lessons.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lessons.setLayoutParams(size);
    }

    public void minimizeNotes() {
        LinearLayout notes = findViewById(R.id.content_all_notes);
        notes.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0,0);
        notes.setLayoutParams(size);
    }

    public void expandNotes(){
        LinearLayout notes = findViewById(R.id.content_all_notes);
        notes.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        notes.setLayoutParams(size);
    }
    
    public void setSelectedCourse(int position) {
        selectedCourse = position;
        initLessonRecyclerView((Course) Courses.get(position));
    }
    
    public void gotoAudio(int position, String lesson_name) {
        Intent audio = new Intent(this,lessonActivity.class);
        Course select = (Course) Courses.get(selectedCourse);
        Lesson selectedLesson = (Lesson) select.getLessons().get(position);
        audio.putExtra("course_name",select.name);
        audio.putExtra("lesson_name",lesson_name);
        audio.putExtra("lesson_mp3",selectedLesson.mp3);
        startActivity(audio);
    }
}
