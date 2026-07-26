package me.stivendarsi.sellMenu;

import static me.stivendarsi.sellMenu.SellMenu.sellMenuInstance;

public class MessageHandler {
    private String receivedMessage;

    public void load() {
        //System.out.println(sellMenuInstance().getConfig().getKeys(false));
        this.receivedMessage = sellMenuInstance().getConfig().getString("messages.received", "null");
        sellMenuInstance().getLogger().info("Received message: " + receivedMessage);
    }

    public String getReceivedMessage() {
        //System.out.println(this.receivedMessage);
        return receivedMessage;
    }

}
