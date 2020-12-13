package com.example.aibasedattendencesystem.Model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aibasedattendencesystem.R;
import com.example.aibasedattendencesystem.Utility.Student;

import java.util.List;


public class AttendenceAdapter extends RecyclerView.Adapter<AttendenceAdapter.ViewHolder> {

    List<Student> studentsList;
    CheckBoxStateChange checkBoxStateChange;

    public AttendenceAdapter(List<Student> studentsList) {
        this.studentsList = studentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.students_list_rv_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Student student = studentsList.get(position);
        holder.name.setText(student.getName());

        holder.sno.setText(String.valueOf(position + 1));

        holder.attendenceCb.setOnCheckedChangeListener((compoundButton, b) -> checkBoxStateChange.stateChange(position,b));
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sno, name;
        CheckBox attendenceCb;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.idTVStudentName);
            sno = itemView.findViewById(R.id.idTVNumber);
            attendenceCb = itemView.findViewById(R.id.idCBCheckBox);
        }
    }

    public void setCheckBoxStateChange(CheckBoxStateChange itemClickListener) {
        checkBoxStateChange = itemClickListener;
    }

    public interface CheckBoxStateChange {
        void stateChange(int pos, boolean b);
    }
}

