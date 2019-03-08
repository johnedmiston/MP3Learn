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
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    ArrayList Courses;
    ArrayList Notes;
    int selectedCourse;
    Button backToCourses;
    public boolean coursesOpen;
    public final int NEXT = 0;
    public final int ADVANCE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getCourseData();
        initCourseRecyclerView();
        backToCourses = findViewById(R.id.back_to_courses);
        backToCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCourses();
            }
        });
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
            input = input.substring(courseSubstring.length());//remove the course from input
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
            Courses.add(newCourse);//add the course to Courses
        }
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

        if(id == R.id.action_about){
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        else if(id == R.id.action_restart_lessons){
            resetLessons();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetLessons() {
        ArrayList<Lesson> allLessons = new ArrayList<>();
        for(Object c: Courses){
            Course course = (Course) c;
            for(Object lesson:course.lessons){
                allLessons.add((Lesson) lesson);
            }
        }
        for(Lesson lesson:allLessons){
            DatabaseConnection db = new DatabaseConnection(getApplicationContext());
            lesson.setSeekTime(0);
            lesson.setNotes(db.getNotes(lesson));
            db.updateLesson(lesson);
        }
    }
  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEXT) {
            // Make sure the request was successful
            if (resultCode == ADVANCE) {
                String courseName = data.getStringExtra("course_name");
                String lessonName = data.getStringExtra("lesson_name");
                ArrayList theLessons = findCourse(courseName);
                int next = findNext(theLessons,lessonName);
                if(next != -1){
                    gotoAudio(next,true);
                }
            }
        }
    }

    private ArrayList findCourse(String courseName) {
        for(Object course : Courses){
            Course c = (Course) course;
            if(c.name.equalsIgnoreCase(courseName)){
                return c.lessons;
            }
        }
        //This won't happen. It is just to make the editor happy.
        return null;
    }

    private int findNext(ArrayList lessons, String lessonName) {
        for(int i = 0;i<lessons.size();i++){
            Lesson lesson = (Lesson) lessons.get(i);
            if(lesson.getName().equalsIgnoreCase(lessonName)){
                if(i+1 <lessons.size()){
                    return i+1;
                }else{
                    return -1;
                }
            }
        }
        return -1;
    }

    public void expandCourses(){
        LinearLayout courses = findViewById(R.id.content_courses);
        courses.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        courses.setLayoutParams(size);
        //Set coursesOpen to true because we just opened it
        coursesOpen = true;
        //minimize the lesson
        LinearLayout lessons = findViewById(R.id.content_lessons);
        lessons.setVisibility(View.INVISIBLE);
        size = new LinearLayout.LayoutParams(0,0);
        lessons.setLayoutParams(size);
    }

    public void expandLessons(){
        LinearLayout lessons = findViewById(R.id.content_lessons);
        lessons.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lessons.setLayoutParams(size);
        //Set coursesOpen to false because we are closing it
        coursesOpen = false;
        //minimize Courses
        LinearLayout courses = findViewById(R.id.content_courses);
        courses.setVisibility(View.INVISIBLE);
        size = new LinearLayout.LayoutParams(0,0);
        courses.setLayoutParams(size);
    }
    
    public void setSelectedCourse(int position) {
        selectedCourse = position;
        initLessonRecyclerView((Course) Courses.get(position));
    }

    public void gotoAudio(int position,boolean autoplay) {
        Intent audio = new Intent(this,lessonActivity.class);
        Course select = (Course) Courses.get(selectedCourse);
        Lesson selectedLesson = (Lesson) select.getLessons().get(position);
        audio.putExtra("course_name",select.name);
        audio.putExtra("lesson_name",selectedLesson.name);
        audio.putExtra("lesson_mp3",selectedLesson.mp3);
        audio.putExtra("lesson_text", selectedLesson.textData);
        audio.putExtra("autoplay",autoplay);
        startActivityForResult(audio,NEXT);
    }

}
