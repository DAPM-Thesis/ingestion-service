package com.dapm2.ingestion_service.demo;

import algorithm.Algorithm;
import communication.message.Message;
import communication.message.impl.event.Event;

public class MyEventAlgorithm implements Algorithm<Message, Event> {

    @Override
    public Event run(Message event) {
        System.out.println(this.getClass().getSimpleName() + " applied.");
        try {
            Thread.sleep(2000); // ⏸️ Pause for 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Good practice
            System.err.println("Sink interrupted while sleeping.");
        }
        return (Event) event;
    }
}
