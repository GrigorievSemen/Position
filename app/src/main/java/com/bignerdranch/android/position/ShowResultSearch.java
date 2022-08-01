package com.bignerdranch.android.position;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ShowResultSearch extends AppCompatActivity {

    private final SearchPhrase searchPhrase = new SearchPhrase();
    private Drawable background; // создаем экземпляр графического контура для строчек в таблице
    private TableLayout tableL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showresultsearch);

        ActionBar actionBar = getSupportActionBar();//скрываем заголовок программы
        actionBar.hide();

        try {
            searchPhrase.doSearch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tableL = (TableLayout) findViewById(R.id.table);
        tableL.setColumnShrinkable(1, true);
        background = getResources().getDrawable(R.drawable.frame_edittext);

        print();
    }

    private void print() {
        int amount = 0;
        for (Map.Entry<LocalDateTime, Phrase> entry : Phrase.mapPhrase.entrySet()) {
            if (entry.getValue().getIdPhrase().equals(IdPhrase.PHRASE_FOR_SEARCH)) {
                amount++;
            }
        }


        int finalAmount = amount;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (finalAmount != searchPhrase.getAmount()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int i = 1;

                                List<String> list = new ArrayList<>();

                                for (Map.Entry<LocalDateTime, String> entry : searchPhrase.getMapResult().entrySet()) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(" ").append(i++).append(". ").append("/").append(entry.getValue());
                                    list.add(sb.toString());
                                }
                                printTable(list);
                            }
                        });
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int i = 1;

                                List<String> list = new ArrayList<>();

                                for (Map.Entry<LocalDateTime, String> entry : searchPhrase.getMapResult().entrySet()) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(" ").append(i++).append(". ").append("/").append(entry.getValue());
                                    list.add(sb.toString());
                                }
                                printTable(list);
                            }
                        });
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }

    public void printTable(List<String> list) {
        tableL.removeAllViews();

        int number_table = 0;
        for (int i = 0; i < list.size(); i++) {
            String array[] = list.get(i).split("/");
            String num = array[0];
            String phrase = array[1];
            String result = array[2];
            TableRow tableRow = new TableRow(this); // создаем и заполняем таблицу фразами и результатами
            tableRow.setPadding(0, 20, 0, 20); // добавляем отступы между строками
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));
            tableRow.setBackground(background);


            TextView textView = new TextView(this);
            textView.setText(num);
            tableRow.addView(textView, 0);

            TextView textView1 = new TextView(this);
            textView1.setText(phrase);
            tableRow.addView(textView1, 1);

            TextView textView2 = new TextView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            textView2.setLayoutParams(params);
            textView2.setText(result);

            tableRow.addView(textView2, 2);

            tableL.addView(tableRow, number_table++);
        }
    }
}


