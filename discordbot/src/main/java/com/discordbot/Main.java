package com.discordbot;

import events.Botevents;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class Main {

    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault("Insert Token here"); // ---------------------------------- EDIT HERE ----------------------------------
       
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        
        
        
        builder.addEventListeners(new Botevents());
        builder.build();
    }
}