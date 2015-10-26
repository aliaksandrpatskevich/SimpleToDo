package com.het.simpletodo.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.het.simpletodo.R;
import com.het.simpletodo.Todo;
import com.het.simpletodo.TodoAdapter;
import com.het.simpletodo.TodoDatabaseHelper;
import com.het.simpletodo.fragments.ConfirmDeleteDialogFragment;
import com.het.simpletodo.fragments.DatePickerDialogFragment;
import com.het.simpletodo.fragments.EditItemDialogFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener,
        DatePickerDialogFragment.DatePickerFragmentListener,
        EditItemDialogFragment.EditItemDialogListener {


    ArrayList<Todo> arrayOfTodo;
    TodoAdapter adapter;
    ListView lvItems;
    private int posEditedItem;
    Todo todoEdit;
    Todo todoDel;
    TextView tvLevel;
    TextView tvDate;
    EditText etNewItem;
    EditText etComments;
    Spinner spLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        lvItems = (ListView) findViewById(R.id.lvItems);
        tvDate = (TextView) findViewById(R.id.tvDate);
        spLevel = (Spinner) findViewById(R.id.spLevel);

        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.level, R.layout.spinner_item);
        // Apply the adapter to the spinner
        spLevel.setAdapter(spinnerAdapter);
        spLevel.setSelection(1);//medium priority

        arrayOfTodo = new ArrayList<Todo>();
        adapter = new TodoAdapter(this, arrayOfTodo);
        lvItems.setAdapter(adapter);

        readItemsSQL();
        setupListViewListener();
    }


    public void onAddItem(View v) {

        etNewItem = (EditText) findViewById(R.id.etNewItem);
        etComments = (EditText) findViewById(R.id.etComments);

        String dueDate = "";

//        if due date has been chosen
        if (tvDate.getText().toString().matches(".*\\d.*")) {
            dueDate = tvDate.getText().toString();
        }

        Todo newTodo = new Todo(
                etNewItem.getText().toString(),
                dueDate,
                etComments.getText().toString(),
                spLevel.getSelectedItem().toString());

        arrayOfTodo.add(newTodo);
        adapter.add(newTodo);
        addTodo(newTodo);

//        reset fields
        etNewItem.setText("");
        tvDate.setText("");
        etComments.setText("");
        spLevel.setSelection(1);
    }

    private void addTodo(Todo newTodo) {
        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        databaseHelper.addTodo(newTodo);
    }


    private void setupListViewListener() {

        tvDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (tvDate.hasFocus())
                    showDatePickerDialog(v);
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View item, int pos,
                                           long id) {
                todoDel = (Todo) adapterView.getItemAtPosition(pos);
                confirmDeleteDialog(todoDel);
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int pos, long id) {
                todoEdit = (Todo) adapterView.getItemAtPosition(pos);
                posEditedItem = pos;
                EditItemDialog(todoEdit);
            }
        });

        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {
                tvLevel = (TextView) findViewById(R.id.tvLevel);
                switch (position) {
                    case 0:
                        tvLevel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_priority_high, 0, 0, 0);
                        break;
                    case 1:
                        tvLevel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_priority_medium, 0, 0, 0);
                        break;
                    case 2:
                        tvLevel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_priority_low, 0, 0, 0);
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void readItemsSQL() {
        // Get singleton instance of database
        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        // Get all items from database
        arrayOfTodo = (ArrayList<Todo>) databaseHelper.getAllTodo();
        adapter.addAll(arrayOfTodo);
    }

    private void writeItemsSQL() {
        // Get singleton instance of database
        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        databaseHelper.deleteAllTodo();

        for (Todo todo : arrayOfTodo) {
            databaseHelper.addTodo(todo);
        }
    }

    private void deleteTodo(Todo todoDel) {
        TodoDatabaseHelper databaseHelper = TodoDatabaseHelper.getInstance(this);
        databaseHelper.deleteTodo(todoDel);
    }

    private void confirmDeleteDialog(Todo todoDel) {
        FragmentManager fm = getSupportFragmentManager();
        ConfirmDeleteDialogFragment confirmDeleteDialog = ConfirmDeleteDialogFragment.newInstance(todoDel.text);
        confirmDeleteDialog.show(fm, "fragment_delete_item");
    }

    @Override
    public void onFinishConfirmDeleteDialog(boolean confirm) {
        if (confirm) {
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            delete_confirmed();
        }
    }

    private void delete_confirmed() {
        arrayOfTodo.remove(todoDel);
        adapter.remove(todoDel);
        deleteTodo(todoDel);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onFinishDatePick(int year, int month, int day) {
        tvDate.setText((month + 1) + "/" + day + "/" + year);
    }


    private void EditItemDialog(Todo todoEdit) {
        FragmentManager fm = getSupportFragmentManager();
        EditItemDialogFragment editItemDialogFragment = EditItemDialogFragment.newInstance(todoEdit);
        editItemDialogFragment.show(fm, "fragment_delete_item");
    }


    @Override
    public void onFinishEditItemDialog(Todo todoEdit) {
        arrayOfTodo.set(posEditedItem, todoEdit);
        adapter.clear();
        writeItemsSQL();
        readItemsSQL();
    }
}