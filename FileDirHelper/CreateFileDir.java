package createfiledir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CreateFileDir
 * @author DSHADE
 * Goal: The goal of this class is to build file_dir.txt for the android app
 * AudioApp built during a LightSys Spring Break Code-a-thon for Cybermissions.
 * 
 */
public class CreateFileDir {
    

    public static void main(String[] args) {
        String output = "";
        File[] courses;
        File filesToAdd = new File("files_to_add");
        filesToAdd.mkdir();
        File course_dir = new File("Courses");
        if(course_dir.isDirectory()){
            courses = course_dir.listFiles();
            if(courses.length <=1){
                return;
            }
            for(File course : courses){
                if(course.isDirectory()){
                    output+="<course>";
                    String courseName = course.getName();
                    output+="name:"+courseName+";";
                    File[] lessons = course.listFiles();
                    sortFileArray(lessons);
                    for (int j = 0;j<lessons.length;j++){
                        File lesson = lessons[j];
                        if(lesson.isDirectory()){
                            String lessonName = lesson.getName();
                            output+="<lesson>";
                            output+="name:"+lessonName+";";
                            File[] lessonMaterials = lesson.listFiles();
                            String mp3 = "";
                            String text = "";
                            for(File lessonContent : lessonMaterials){
                                String ext = getExtention(lessonContent);
                                File fileToAdd = null;
                                switch(ext){
                                    case ".txt":
                                        if(isValidName(lessonContent.getName())){
                                            text = lessonContent.getName();
                                            fileToAdd= new File(filesToAdd.getPath()+"/"+lessonContent.getName());
                                        }else{
                                            fileToAdd = new File(filesToAdd.getPath()+"/"+newName(lessonContent.getName()));       
                                            text = newName(fileToAdd.getName());
                                        }
                                        output +="text:"+text+";";
                                        break;
                                    case ".mp3":
                                        if(isValidName(lessonContent.getName())){
                                            mp3 = lessonContent.getName();
                                            fileToAdd= new File(filesToAdd.getPath()+"/"+lessonContent.getName());
                                        }else{
                                            fileToAdd = new File(filesToAdd.getPath()+"/"+newName(lessonContent.getName()));       
                                            mp3 = newName(fileToAdd.getName());
                                        }
                                        output+="mp3:"+mp3+";";
                                        break;
                                }
                                try {
                                    copyFile(lessonContent,fileToAdd);
                                } catch (IOException ex) {
                                    Logger.getLogger(CreateFileDir.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            output += "</lesson>";
                        }
                    }
Test                    output += "</course>";
                }
                
            }
        
        }  
        File fileDir = new File(filesToAdd.getPath()+"/"+"file_dir.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileDir);
            fos.write(output.getBytes());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateFileDir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateFileDir.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(CreateFileDir.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    public static String getExtention(File f){
        return f.getName().substring(f.getName().lastIndexOf("."));
    }
    public static String getExtention(String name){
        return name.substring(name.lastIndexOf("."));
    }
    private static String newName(String name) {
        String ext = getExtention(name);
        name = name.substring(0,name.indexOf(ext));
        Deque<Character> charStack = new ArrayDeque<>();
        while((name.charAt(0)>='0' && name.charAt(0)<= '9') || name.charAt(0) == '-' || name.charAt(0) == '_'){
            charStack.push(name.charAt(0));
            name = name.substring(1);
        }
        while(!charStack.isEmpty()){
            name += charStack.pop();
        }
        name +=ext;
        return name;
    }
    private static boolean isValidName(String name){
        return !((name.charAt(0)>='0' && name.charAt(0)<= '9') || name.charAt(0) == '-' || name.charAt(0) == '_');
    }
    private static void copyFile(File source, File dest) throws IOException{
        if(source == null || dest == null){
            return;
        }
        InputStream sourceStream = null;
        OutputStream destStream = null;
        try{
            sourceStream = new FileInputStream(source);
            destStream = new FileOutputStream(dest);
            byte[] b = new byte[1024];
            int l;
            while((l = sourceStream.read(b))>0){
                destStream.write(b, 0, l);
            }
        }
        finally{
            if(sourceStream != null){
                sourceStream.close();
            }
            if(destStream != null){
                destStream.close();
            }
        }
    }
    public static void sortFileArray(File[] input){
        Arrays.sort(input, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int i = 0;
                char n1 = o1.getName().charAt(i);
                char n2 = o2.getName().charAt(i);
                i++;
                while(n1 == n2){
                    n1 = o1.getName().charAt(i);
                    n2 = o2.getName().charAt(i);
                    i++;
                }
                return n1 - n2;
            }
        });
        
    }
}
