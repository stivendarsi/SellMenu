package me.stivendarsi.sellMenu;

public class MainHandler {
    private final MoneyHandler moneyHandler;
    private final MessageHandler messageHandler;

    public MainHandler(){
        this.moneyHandler = new MoneyHandler();
        this.messageHandler = new MessageHandler();
    }

    public void load(){
        this.moneyHandler.load();
        this.messageHandler.load();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public MoneyHandler getMoneyHandler() {
        return moneyHandler;
    }
}
