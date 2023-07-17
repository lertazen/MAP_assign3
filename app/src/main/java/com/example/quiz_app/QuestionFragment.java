package com.example.quiz_app;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuestionFragment extends Fragment {

    private String question;
    private int color;
    private TextView question_textview;

    public QuestionFragment() {
        super (R.layout.fragment_question);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("question", question);
        outState.putInt("color", color);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        question_textview = view.findViewById(R.id.question_textview);

//         Get the bundle data from previous configuration
        if (savedInstanceState != null) {
            color = savedInstanceState.getInt("color");
            question = savedInstanceState.getString("question");
        } else {
            Bundle args = getArguments();
            if (args != null) {
                color = args.getInt("current_color");
                question = args.getString("new_question");
            }
        }
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), color));
        question_textview.setText(question);

        return view;
    }

}