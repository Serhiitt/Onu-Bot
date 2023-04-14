package com.heroku.java;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleEchoBot extends TelegramLongPollingBot {
    public static String GREEN = "\uD83D\uDFE2";
    public static String YELLOW = "\uD83D\uDFE1";
    public static String RED = "\uD83D\uDD34";

    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> online = new ArrayList<>();
    ArrayList<String> offline = new ArrayList<>();
    ArrayList<String> down = new ArrayList<>();
    ArrayList<String> badSignal = new ArrayList<>();
    ArrayList<String> up = new ArrayList<>();
    int onuCount;

    public boolean messageSize(){
        List<String> list1 = new ArrayList<>(Arrays.asList(list.get(0).split("\n")));
        List<String> list2 = new ArrayList<>(Arrays.asList(list.get(1).split("\n")));
        if (list1.size() == list2.size()){
            return true;
        } else {
            return false;
        }
    }

    public void sort(){
        List<String> list1 = new ArrayList<>(Arrays.asList(list.get(0).split("\n")));
        List<String> list2 = new ArrayList<>(Arrays.asList(list.get(1).split("\n")));
        onuCount = list1.size();

        for (int i = 0; i < list1.size(); i++){
            String strBefore = list1.get(i);
            String strAfter = list2.get(i);
            if (strBefore.contains(GREEN) && strAfter.contains(GREEN)){
                online.add(strBefore + "\n");
            }
            if ((strBefore.contains(GREEN) || strBefore.contains(YELLOW)) && strAfter.contains(RED)){
                down.add(strAfter + "\n");
            }
            if (strBefore.contains(RED) && strAfter.contains(RED)){
                offline.add(strBefore + "\n");
            }
            if (strBefore.contains(YELLOW) || strAfter.contains(YELLOW)){
                badSignal.add(strBefore + "\n");
            }
            if (strBefore.contains(RED) && (strAfter.contains(GREEN) || strBefore.contains(YELLOW))){
                up.add(strAfter + "\n");
            }
        }
    }

    public void clear(){
        list.clear();
        online.clear();
        offline.clear();
        down.clear();
        badSignal.clear();
        up.clear();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String textFromUser = update.getMessage().getText();
        String line = textFromUser;
        Long userId = update.getMessage().getChatId();
        if (line.equals("/start")){
            SendMessage firstMessage = SendMessage.builder().chatId(userId.toString()).text("Пришліть 2 списка ону, бот порівняє їх").build();
            try {
                this.sendApiMethod(firstMessage);
            } catch (TelegramApiException e ){
                e.getMessage();
            }
        } else {
            list.add(line);

            if (list.size() == 2 && messageSize() == true){
                sort();
                String onlineSize = String.valueOf(online.size());
                String offlineSize = String.valueOf(offline.size());
                String badSignalSize = String.valueOf(badSignal.size());
                String downSize = String.valueOf(down.size());
                String upSize = String.valueOf(up.size());
                SendMessage sendSize = SendMessage.builder().chatId(userId.toString()).text("Всього ону: " + onuCount).build();
                SendMessage sendOnline = SendMessage.builder().chatId(userId.toString()).text("Онлайн: " + onlineSize + "\n" + online.toString().replace(",", "").replace("[", "").replace("]", "")).build();
                SendMessage sendOffline = SendMessage.builder().chatId(userId.toString()).text("Були оффлайн: " + offlineSize + "\n" + offline.toString().replace(",", "").replace("[", "").replace("]", "")).build();
                SendMessage sendDown = SendMessage.builder().chatId(userId.toString()).text("Упали: " + downSize + "\n" + down.toString().replace(",", "").replace("[", "").replace("]", "")).build();
                SendMessage sendBadSignal = SendMessage.builder().chatId(userId.toString()).text("Поганий сигнал: " + badSignalSize + "\n" + badSignal.toString().replace(",", "").replace("[", "").replace("]", "")).build();
                SendMessage sendUp = SendMessage.builder().chatId(userId.toString()).text("Піднялись: " + upSize + "\n" + up.toString().replace(",", "").replace("[", "").replace("]", "")).build();
                try {
                    this.sendApiMethod(sendSize);
                    this.sendApiMethod(sendOnline);
                    this.sendApiMethod(sendOffline);
                    this.sendApiMethod(sendDown);
                    this.sendApiMethod(sendBadSignal);
                    this.sendApiMethod(sendUp);
                } catch (TelegramApiException e) {
                    e.getMessage();
                }

                clear();

            } else if (list.size() == 2 && messageSize() == false){
                SendMessage sendTryAgain = SendMessage.builder().chatId(userId.toString()).text("Кількість ONU не співпадає, спробуйте ще раз").build();
                clear();
                try {
                    this.sendApiMethod(sendTryAgain);
                } catch (TelegramApiException e){
                    e.getMessage();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "sortBot";
    }

    @Override
    public String getBotToken() {
        return "5261839501:AAEuWqAJL3wxhyaBWA2Yy-ldpZFSi4s50Rk";
    }
}
