package com.bignerdranch.android.position;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Phrase implements Serializable {
    static Map<LocalDateTime, Phrase> mapPhrase = Collections.synchronizedMap(new TreeMap<>());
    private String name_phrase; // фраза запроса
    private int number_of_requests;// кол-во запросов
    private int pause; // пауза между запросами
    private IdPhrase idPhrase;
    private final static String SAVE = "save.txt";// имя файла сохранения


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Phrase(IdPhrase idPhrase, String name_phrase) {
        this.idPhrase = idPhrase;
        this.name_phrase = name_phrase;
        mapPhrase.put(LocalDateTime.now(), this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Phrase(IdPhrase idPhrase, String name_phrase, int number_of_requests, int pause) {
        this.idPhrase = idPhrase;
        this.name_phrase = name_phrase;
        this.number_of_requests = number_of_requests;
        this.pause = pause;
        mapPhrase.put(LocalDateTime.now(), this);
    }

    public IdPhrase getIdPhrase() {
        return idPhrase;
    }

    public String getName_phrase() {
        return name_phrase;
    }

    public int getNumber_of_requests() {
        return number_of_requests;
    }

    public int getPause() {
        return pause;
    }

    public void setName_phrase(String name_phrase) {
        this.name_phrase = name_phrase;
    }

    public void setNumber_of_requests(int number_of_requests) {
        this.number_of_requests = number_of_requests;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }

    static void removeByPhrase(String phrase) {
        LocalDateTime key = null;
        if (phrase != null) {
            for (Map.Entry<LocalDateTime, Phrase> entry : mapPhrase.entrySet()) {
                if (entry.getValue().getName_phrase().equals(phrase)) {
                    key = entry.getKey();
                    break;
                }
            }
        }
        if (key != null) {
            mapPhrase.remove(key);
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        System.out.println("Our writeObject");
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        System.out.println("Our readObject");
    }
}
