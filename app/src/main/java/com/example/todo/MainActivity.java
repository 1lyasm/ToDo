package com.example.todo;

import android.app.Activity;
import android.content.Context;
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

import java.text.MessageFormat;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private EditText AddTaskEditText;
    private LinearLayout CheckBoxLinearLayout;
    private TaskData MainTaskData = new TaskData();
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
}
class Task {
    private String TaskName;
    public Task(String TaskName) {
        this.TaskName = TaskName;
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
}
