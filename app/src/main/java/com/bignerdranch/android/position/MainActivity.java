package com.bignerdranch.android.position;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Intent intent_PhraseDate;//создаем экземляр класса для перехода
    private Intent intent_ChangePhrase;//создаем экземляр класса для перехода
    private Intent intent_ShowResultSearch ;//создаем экземляр класса для перехода
    private TableLayout tableLayout; // создаем экземпляр лаяута таблицы
    private Drawable background; // создаем экземпляр графического контура для строчек в таблице

    private static Spinner spinner_url_company; // спинер для названий компаний
    private final List<String> listToSpinner = new ArrayList<>(); // лист для спинера

    private ArrayAdapter<String> adapter;


    private static final int SWIPE_MIN_DISTANCE = 130;
    private static final int SWIPE_MAX_DISTANCE = 300;
    private static final int SWIPE_MIN_VELOCITY = 200;
    private GestureDetectorCompat lSwipeDetector;// для обработки свайпа
    private volatile LocalDateTime writedKeyPhrase; // сюда записываеться строка при нажати на нее
    private final static String SAVE = "save.txt";// имя файла сохранения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deserialize();

        ActionBar actionBar = getSupportActionBar();//скрываем заголовок программы
        actionBar.hide();

        //кнопка для вызова метода по добавлению запроса
        Button button_add_request = findViewById(R.id.Button_add_request);
        //кнопка вызывает метод для поиска позиции с поисковика яндекса
        Button button_find_out_position_number = findViewById(R.id.Button_find_out_position_number);
        //для добавления данных в спиннер
        Button button_add_toSpinner = findViewById(R.id.Button_add_toSpinner);
        //для удаления данных в спиннер
        Button button_delete_toSpinner = findViewById(R.id.Button_delete_toSpinner);


        intent_PhraseDate = new Intent(MainActivity.this, AddPhraseForSearch.class);
        intent_ChangePhrase = new Intent(MainActivity.this, ChangePhrase.class);
        intent_ShowResultSearch = new Intent(MainActivity.this, ShowResultSearch.class);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        tableLayout.setColumnShrinkable(1, true);
        background = getResources().getDrawable(R.drawable.frame_edittext);

        for (Map.Entry<LocalDateTime, Phrase> entry : Phrase.mapPhrase.entrySet()) {
            if (entry.getValue().getIdPhrase().equals(IdPhrase.PHRASE_URL_COMPANY)) {
                listToSpinner.add(entry.getValue().getName_phrase());
            }
        }
        spinner_url_company = findViewById(R.id.Spinner_url_company);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listToSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_url_company.setAdapter(adapter);
        spinner_url_company.setSelection(0);

        table_result(); //заполняем таблицу

    }

    public void add_toSpinner(View view) {// метод нажатия кнопки, добавляет элементы в спинер
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);// всплывающее окно
        EditText et = new EditText(MainActivity.this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                listToSpinner.add(et.getText().toString()); // добавляев в лист значения
                new Phrase(IdPhrase.PHRASE_URL_COMPANY, et.getText().toString());
                serialize();
                adapter.notifyDataSetChanged(); // обновляем адаптер спинера
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void delete_toSpinner(View view) {
        String spinnerTex = String.valueOf(spinner_url_company.getSelectedItem());
        listToSpinner.remove(spinnerTex);
        Phrase.removeByPhrase(spinnerTex);
        serialize();
        spinner_url_company.setSelection(0);
        adapter.notifyDataSetChanged(); // обновляем адаптер спинера
    }

    public void add_request(View viev) {//показывает экран с возможностью добавления нового запроса
        startActivity(intent_PhraseDate);
    }


    public void find_out_position_number(View view) throws Exception {//метод обработки запросов
        startActivity(intent_ShowResultSearch);
    }


    public void table_result() { //заполняем таблицу
        table_remove();
        if (Phrase.mapPhrase.size() != 0) {
            int number_string = 1;
            int number_table = 0;
            for (Map.Entry<LocalDateTime, Phrase> entry : Phrase.mapPhrase.entrySet()) {
                if (entry.getValue().getIdPhrase().equals(IdPhrase.PHRASE_FOR_SEARCH)) {
                    String number_pos = " " + number_string + ".  ";
                    String phraseForSearch = " " + entry.getValue().getName_phrase() + "  ";

                    TableRow tableRow = new TableRow(this); // создаем и заполняем таблицу фразами и результатами
                    tableRow.setPadding(0, 20, 0, 20); // добавляем отступы между строками
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    tableRow.setBackground(background);


                    MyGestureListener myGestureListener = new MyGestureListener(entry.getKey());
                    lSwipeDetector = new GestureDetectorCompat(this, myGestureListener); //присваиваем tableRow возможность использовать свайп, передаем в объект класса номер строки
                    tableRow.setOnTouchListener(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            writedKeyPhrase = myGestureListener.getKeyPhrase();// присваиваем строку до которой дотронулись
                            return lSwipeDetector.onTouchEvent(event);
                        }
                    });

                    TextView textView = new TextView(this);
                    textView.setText(number_pos);
                    tableRow.addView(textView, 0);

                    TextView textView1 = new TextView(this);
                    textView1.setText(phraseForSearch);
                    tableRow.addView(textView1, 1);

                    tableLayout.addView(tableRow, number_table++);

                    number_string++;
                }
            }
        }
    }

    public void table_remove() {//очищаем таблицу
        tableLayout.removeAllViews();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener { //класс для обработки свайпа
        private LocalDateTime keyPhrase; // присваиваем ключ фразу

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public LocalDateTime getKeyPhrase() {
            return keyPhrase;
        }

        MyGestureListener(LocalDateTime keyPhrase) {// необходимый конструктор, для определения строки в массиве
            this.keyPhrase = keyPhrase;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_DISTANCE)
                return false;

            if (Math.abs(e2.getX() - e1.getX()) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {  //обработка сдвига вправо и влево
                Phrase.mapPhrase.remove(writedKeyPhrase); //удаляем объект из массива, по номеру строки до которой дотронулись в методе onTouch
                serialize();
                table_remove();
                table_result();
            }
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onLongPress(MotionEvent e) {
            intent_ChangePhrase.putExtra("phraseForChange", writedKeyPhrase); // переносим в вызываемое активное окно номер елемента в массиве для изменения
            startActivity(intent_ChangePhrase);
        }
    }


    public static String getValueSpinner() {
        return String.valueOf((spinner_url_company.getSelectedItem()));// записываем значение спинера (" url сайта")
    }

    @Override
    public void onBackPressed() { // замена работы аппаратной кнопки назад
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Конечно!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        quitDialog.setNegativeButton("НЕТ                 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
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

    public  void deserialize() {
        File directory = getApplication().getCacheDir();
        File file = new File(directory, "/" + SAVE);
        try
             {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream oisPerson = new ObjectInputStream(fis);
            if (fis != null) {
                Phrase.mapPhrase = (Map<LocalDateTime, Phrase>) oisPerson.readObject();
                oisPerson.close();
            }
        } catch (Exception e) {
            System.out.println("deserialize" + e.toString());
        }
    }
}




