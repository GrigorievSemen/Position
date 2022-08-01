package com.bignerdranch.android.position;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SearchPhrase {
    private Map<LocalDateTime, String> mapResult = Collections.synchronizedMap(new TreeMap());

    private static final Pattern pattern = Pattern.compile("serp-item serp-item_card([\\s\\S]*?)li>");
    private int amount = 0; // для цикла while в классе ShowResultSearch

    public void doSearch() throws Exception {

        for (Map.Entry<LocalDateTime, Phrase> entry : Phrase.mapPhrase.entrySet()) {
            if (entry.getValue().getIdPhrase().equals(IdPhrase.PHRASE_FOR_SEARCH)) {
                mapResult.put(entry.getKey(), entry.getValue().getName_phrase() + " /" + " - ИДЕТ ПОДСЧЕТ");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean flag = false;
                            int sum = 0;
                            int number_of_requests = entry.getValue().getNumber_of_requests();
                            int pause = entry.getValue().getPause();
                            String phraseName = entry.getValue().getName_phrase();

                            for (int i = 0; i < number_of_requests; i++) {
                                String url = get_String_from_Document(doUrl(phraseName));
                                String valueUrlCompany = "<b>" + MainActivity.getValueSpinner().trim().toLowerCase(Locale.ROOT) + "</b>";
                                Matcher matcher = pattern.matcher(url);
                                while (matcher.find()) {
                                    String result = url.substring(matcher.start(), matcher.end());
                                    if (result.contains(valueUrlCompany)) {
                                        ++sum;
                                        flag = true;
                                        break;
                                    } else {
                                        ++sum;
                                    }
                                }
                                try {
                                    Thread.sleep(pause * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            mapResult.put(entry.getKey(), flag ? entry.getValue().getName_phrase() + " /" + Integer.toString(sum / number_of_requests) :entry.getValue().getName_phrase() + " /" + "НЕ НАЙДЕНО");
                            amount++;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }


    public String doUrl(String phrase) {
        String[] words_phrase = phrase.trim().split(" ");

        for (int i = 0; i < words_phrase.length; i++) {

            if (i != words_phrase.length - 1) {
                phrase = phrase + words_phrase[i] + "+";
            } else {
                phrase = phrase + words_phrase[i];
            }
        }
        String part1 = "https://yandex.ru/search/direct?text=";
        String part2 = "&filters_docs=direct_cm&lr=213";
        return part1 + phrase + part2;
    }

    private String get_String_from_Document(String url) throws Exception {
        Callable task = () -> {// переопределяем метод call
            Document document = null;

            while (document == null) {

                try {
                    document = (Document) Jsoup.connect(url).userAgent("Mozilla").get();
                } catch (IOException e) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }

            return document.toString();

        };
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future = executor.submit(task);

        return future.get();
    }

    public Map<LocalDateTime, String> getMapResult() {
        return mapResult;
    }

    public int getAmount() {
        return amount;
    }
}

