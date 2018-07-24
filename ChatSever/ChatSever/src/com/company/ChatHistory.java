package com.company;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.List;

public class ChatHistory implements Observable{
    private static ChatHistory chathistory = new ChatHistory();
    private List<ChatMessage> messagelist;
    private ConcurrentSkipListSet<Observer> observers;
    List<ChatMessage> chathistoryoutput(){
        return messagelist;
    }
    public static ChatHistory getInstance() {
        return chathistory;
    }
    private ChatHistory() {
        messagelist= Collections.synchronizedList(new LinkedList<>());
        observers = new ConcurrentSkipListSet <>();
    }
    public void insert (ChatMessage message) {
        messagelist.add(message);
        notifyObserver(message);
    }
    public void remove (User user){
        synchronized (messagelist){
            messagelist.removeIf(message -> message.userOwnBy() == user);
        }
    }
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    @Override
    public void notifyObserver(ChatMessage message) {
        synchronized (observers) {
            for (Observer observer : observers) {
                observer.update(message);
            }
        }
    }
}
