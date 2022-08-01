package com.bignerdranch.android.position;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AddPhraseForSearch extends AppCompatActivity implements Serializable {
    private TextView TextView_enter_request;//текст информационный - просьба ввести запрос в поле ниже
    private EditText EditText_enter_request;//поля для ввода запроса
    private TextView TextView_number_of_request;//текст информационный - просьба ввести запрос в поле ниже
    private Spinner Spinner_number_of_requests;//поля для ввода кол-ва повторов запроса
    private TextView TextView_pause_between_requests;//текст информационный - просьба ввести запрос в поле ниже
    private Spinner Spinner_pause_between_requests;// поля для ввода время паузы между запросами
    private Button Button_add;//кнопка добавляет запрос в массив
    private Intent intent_MainActivity;
    private final static String SAVE = "save.txt";// имя файла сохранения



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phrase_date);

        ActionBar actionBar = getSupportActionBar();//скрываем заголовок программы
        actionBar.hide();

        TextView_enter_request = findViewById(R.id.TextView_enter_request);
        EditText_enter_request = findViewById(R.id.EditText_enter_request);
        TextView_number_of_request = findViewById(R.id.TextView_number_of_request);
        Spinner_number_of_requests = findViewById(R.id.Spinner_number_of_requests);
        TextView_pause_between_requests = findViewById(R.id.TextView_pause_between_requests);
        Spinner_pause_between_requests = findViewById(R.id.Spinner_pause_between_requests);
        Button_add = findViewById(R.id.Button_add);
        intent_MainActivity = new Intent( AddPhraseForSearch.this, MainActivity.class);

        Spinner_number_of_requests.setSelection(0); //ставим спиннер в начальное положение
        Spinner_pause_between_requests.setSelection(0); //ставим спиннер в начальное положение
        EditText_enter_request.setText("");//обнуляем поле текста



        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);// объект клавиатуры
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);//выводим клавиатуру

        EditText_enter_request.requestFocus();
        EditText_enter_request.setOnEditorActionListener(new TextView.OnEditorActionListener() {//обработка нажатия клавиши ENTER
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);//скрываем клавиатуру
                    return true;
                }
                return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void add(View view) throws IOException {//формирует объект класса нового запроса и добавляет его в массив объектов

        String name_phrase = EditText_enter_request.getText().toString();
        if(name_phrase.equals("")){
            Toast toast = Toast.makeText(AddPhraseForSearch.this, "Не заполненно поле с запросом", Toast.LENGTH_LONG);
            toast.show();
        } else {
            int number_of_requests = Integer.parseInt(Spinner_number_of_requests.getSelectedItem().toString());
            int pause = Integer.parseInt(Spinner_pause_between_requests.getSelectedItem().toString());

            new Phrase(IdPhrase.PHRASE_FOR_SEARCH, name_phrase, number_of_requests, pause);

            serialize();
            startActivity(intent_MainActivity);
        }
    }

    public  void serialize() {
        File directory = getApplication().getCacheDir();
        File file = new File(directory, "/" + SAVE);

        try(FileOutputStream fos =  new FileOutputStream(file)) {
            ObjectOutputStream oosPerson = new ObjectOutputStream(fos);
            oosPerson.writeObject(Phrase.mapPhrase);
            oosPerson.close();
        } catch (IOException e) {
            System.out.println("serialize" + e.toString());
        }
    }
}
