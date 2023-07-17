package com.example.quiz_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionsNumberFragment extends DialogFragment {
    private EditText userInput;
    private Button btnOK, btnCancel;
    private TextView dialogTitle;
    private int maxQuestionCount;

    public static QuestionsNumberFragment newInstance(int maxQuestionCount) {
        QuestionsNumberFragment fragment = new QuestionsNumberFragment();
        fragment.maxQuestionCount = maxQuestionCount;
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions_number, container, false);

        btnOK = view.findViewById(R.id.change_OK);
        btnCancel = view.findViewById(R.id.change_cancel);
        userInput = view.findViewById(R.id.edit_question_count);
        dialogTitle = view.findViewById(R.id.dialog_textview);

        dialogTitle.setText(getString(R.string.dialog_enter_number) + " " + maxQuestionCount);

        // Set click listener for cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
//
//        // Set click listener for OK
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = userInput.getText().toString();
                if(!userInput.equals("") || (Integer.parseInt(input) < maxQuestionCount)) {
                    ((MainActivity)getActivity()).changeQuestionsCount(Integer.parseInt(input));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
            }
        });

        return view;
    }

    public static String TAG = "ChangeQuestionCountDialog";
}