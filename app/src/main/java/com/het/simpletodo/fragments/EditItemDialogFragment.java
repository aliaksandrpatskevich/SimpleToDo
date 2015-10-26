package com.het.simpletodo.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.het.simpletodo.R;
import com.het.simpletodo.Todo;

import java.util.Calendar;

public class EditItemDialogFragment extends DialogFragment
        implements Button.OnClickListener {

    EditText etText;
    String todoDate;
    EditText etComments;
    Spinner spLevel;
    CalendarView cvCalendar;
    ImageView ivPriorityLevel;

    public interface EditItemDialogListener {
        void onFinishEditItemDialog(Todo todoEdit);
    }

    public EditItemDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static EditItemDialogFragment newInstance(Todo todoEdit) {

        EditItemDialogFragment frag = new EditItemDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", todoEdit.text);
        args.putString("date", todoEdit.date);
        args.putString("comments", todoEdit.comments);
        args.putString("level", todoEdit.level);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_item_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        String todoText = getArguments().getString("text");
        todoDate = getArguments().getString("date");
        String todoComments = getArguments().getString("comments");

        etText = (EditText) view.findViewById(R.id.etText);
        etComments = (EditText) view.findViewById(R.id.etComments);
        spLevel = (Spinner) view.findViewById(R.id.spLevel);
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        cvCalendar = (CalendarView) view.findViewById(R.id.cvCalendar);
        ivPriorityLevel = (ImageView) view.findViewById(R.id.ivPriorityLevel);

        String parts[] = todoDate != null ? todoDate.split("/") : new String[0];

        Calendar calendar = Calendar.getInstance();
//        if date has been entered before
        if (parts.length > 1) {
            int month = Integer.parseInt(parts[0]) - 1;
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }

        long milliTime = calendar.getTimeInMillis();
        cvCalendar.setDate(milliTime, true, true);

        cvCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                todoDate = String.valueOf((month + 1) + "/" + day + "/" + year);
            }
        });

        etText.setText(todoText);
        etText.setSelection(etText.getText().length());  //set cursor at the end
        etComments.setText(todoComments);
        etComments.setSelection(etComments.getText().length());  //set cursor at the end

        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.level, R.layout.spinner_item);
        // Apply the adapter to the spinner
        spLevel.setAdapter(spinnerAdapter);
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (getArguments().getString("level").equals(spinnerAdapter.getItem(i).toString())) {
                spLevel.setSelection(i);
                setImageLevel(i);
                break;
            }
        }

        btnSave.setOnClickListener(this);

        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {
                setImageLevel(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return view;
    }

    private void setImageLevel(int position) {
        switch (position) {
            case 0:
                ivPriorityLevel.setBackgroundResource(R.drawable.ic_priority_high);
                break;
            case 1:
                ivPriorityLevel.setBackgroundResource(R.drawable.ic_priority_medium);
                break;
            case 2:
                ivPriorityLevel.setBackgroundResource(R.drawable.ic_priority_low);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Todo todoEdit = new Todo();

        todoEdit.text = String.valueOf(etText.getText());
        todoEdit.date = todoDate;
        todoEdit.comments = String.valueOf(etComments.getText());
        todoEdit.level = String.valueOf(spLevel.getSelectedItem().toString());

        EditItemDialogListener listener = (EditItemDialogListener) getActivity();
        listener.onFinishEditItemDialog(todoEdit);

        dismiss();
    }
}