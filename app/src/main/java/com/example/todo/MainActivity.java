package com.example.todo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQuery;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class MainActivity extends Activity {
    private EditText AddTaskEditText;
    private LinearLayout CheckBoxLinearLayout;
    private TaskData MainTaskData = new TaskData();
    private String MainAppDataFileName = "ToDoAppData.txt";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddTaskEditText = (EditText) findViewById(R.id.AddTaskEditText);
        CheckBoxLinearLayout = (LinearLayout) findViewById(R.id.CheckBoxLinearLayout);
        AddTaskEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String NewTaskName = AddTaskEditText.getText().toString();
                AddTaskEditText.getText().clear();
                removeFocusFromAddTaskEditText(v);
                CheckBox NewCheckBox = new CheckBox(CheckBoxLinearLayout.getContext());
                NewCheckBox.setText(NewTaskName);
                CheckBoxLinearLayout.addView(NewCheckBox);
                MainTaskData.addTask(NewTaskName);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPreviousData();
    }

    @Override
    protected void onPause() {
        storeCurrentData();
        super.onPause();
    }

    public void removeFocusFromAddTaskEditText(View view) {
        AddTaskEditText.clearFocus();
        InputMethodManager HideKeyboardManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        HideKeyboardManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void removeCompletedTasks(View view) {
        for (int I = 0; I < CheckBoxLinearLayout.getChildCount(); ++I) {
            if (CheckBoxLinearLayout.getChildAt(I).createAccessibilityNodeInfo().isChecked()) {
                MainTaskData.removeTaskByIndex(I);
                CheckBoxLinearLayout.removeViewAt(I);
                --I;
            }
        }
    }

    public void loadPreviousData() {
        MainTaskData.clear();
        SharedPreferences AppDataSharedPreferences =
                getSharedPreferences(MainAppDataFileName, Context.MODE_PRIVATE);
        Collection<?> AppDataCollection = 
                AppDataSharedPreferences.getAll().values();
        for (Object TaskNameState: AppDataCollection) {
            String TaskNameStateString = (String) TaskNameState;
            MainTaskData.addTask(new Task(TaskNameStateString.substring(0,
                    TaskNameStateString.length() - 1),
                    Character.digit(
                            TaskNameStateString.charAt(
                                    TaskNameStateString.length() - 1), 10)));
        }
        CheckBoxLinearLayout.removeAllViews();
        for (Task T: MainTaskData.getToDoList()) {
            CheckBox NewCheckBox = new CheckBox(CheckBoxLinearLayout.getContext());
            NewCheckBox.setText(T.getTaskName());
            NewCheckBox.setChecked(T.CompletionState == 1);
            CheckBoxLinearLayout.addView(NewCheckBox);
        }
    }

    public void storeCurrentData() {
        SharedPreferences AppDataSharedPreferences =
                getSharedPreferences(MainAppDataFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor AppDataEditor = AppDataSharedPreferences.edit();
        int KeyCount = 0;
        for (Task T: MainTaskData.getToDoList()) {
            AppDataEditor.putString(MessageFormat.format("{0}", KeyCount),
                    MessageFormat.format("{0}{1}", T.getTaskName(),
                            CheckBoxLinearLayout.
                                    getChildAt(KeyCount).
                                    createAccessibilityNodeInfo().isChecked() ? 1 : 0d));
            ++KeyCount;
        }
        AppDataEditor.commit();
    }
}
class Task {
    private String TaskName;
    public int CompletionState = 0;
    public Task(String TaskName) {
        this.TaskName = TaskName;
    }
    public Task(String TaskName, int State) {
        this.TaskName = TaskName;
        CompletionState = State;
    }
    public String getTaskName() {
        return TaskName;
    }
}

class TaskData {
    private ArrayList<Task> TaskList = new ArrayList<>();
    public String getTaskNamesString() {
        String TaskNamesString = "";
        for (Task T: TaskList) {
            TaskNamesString += T.getTaskName();
            TaskNamesString += "\n";
        }
        return TaskNamesString;
    }

    public void addTask(String TaskName) {
        TaskList.add(new Task(TaskName));
    }
    public void addTask(Task TaskObject) {
        TaskList.add(TaskObject);
    }

    public boolean removeTaskByValue(String TaskName) {
        int FoundPosition = -1;
        for (int I = 0; I < TaskList.size(); ++I) {
            if (TaskList.get(I).getTaskName().equals(TaskName)) {
                FoundPosition = I;
                break;
            }
        }
        if (FoundPosition >= 0) {
            TaskList.remove(FoundPosition);
            return true;
        } else {
            return false;
        }
    }

    public void removeTaskByIndex(int Index) {
        TaskList.remove(Index);
    }

    public ArrayList<Task> getToDoList() {
        return TaskList;
    }
    public void clear() {
        TaskList.clear();
    }
}
