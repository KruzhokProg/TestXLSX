package com.example.testxlsx;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class ScheduleViewHolder extends RecyclerView.ViewHolder {

    public TextView tv_num_lesson;
    public TextView tv_name_lesson;
    public TextView tv_room_lesson;

    public ScheduleViewHolder(@NonNull View itemView) {
        super(itemView);

        tv_num_lesson = itemView.findViewById(R.id.tvNumLesson);
        tv_name_lesson = itemView.findViewById(R.id.tvLessonName);
        tv_room_lesson = itemView.findViewById(R.id.tvLessonRoom);
    }
}
