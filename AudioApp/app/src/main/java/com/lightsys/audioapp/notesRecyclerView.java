package com.lightsys.audioapp;

import android.support.v7.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class notesRecyclerView extends RecyclerView.Adapter<notesRecyclerView.ViewHolder>{
    private static final String TAG = "Recycler_View_Adapter";
    //The names of the courses to be displayed
    private ArrayList<String> mNotes = new ArrayList<>();

    private Context mContext;
    private MainActivity parent;
    private int size;

    //Default constructor
    public notesRecyclerView(ArrayList<Note> notes, Context context, Activity parent){
        mContext = context;
        for (int i = 0; i < notes.size(); i++){
            mNotes.add(notes.get(i).getTitle());
        }
        size = mNotes.size();
        this.parent = (MainActivity) parent;
    }

    @NonNull
    @Override
    //Recycling the view holders
    public notesRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_course_listitem, viewGroup, false);
        notesRecyclerView.ViewHolder holder = new notesRecyclerView.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull notesRecyclerView.ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called."); //Log for debugging

        viewHolder.courseName.setText(mNotes.get(position));
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {

            /**
             *  This function is called when a list item is clicked/tapped.
             *  More functionality should be added to this function
             */
            @Override
            public void onClick(View v) {
                //Logging for debugging
                Log.d(TAG, "onClick: Clicked on : " + mNotes.get(position));
                Toast.makeText(mContext, mNotes.get(position), Toast.LENGTH_SHORT).show();
                parent.gotoAudio(position,mNotes.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        /** Variables */

        //Course name
        TextView courseName;

        //The layout for each list item
        LinearLayout parentLayout;

        /** Constructor */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
