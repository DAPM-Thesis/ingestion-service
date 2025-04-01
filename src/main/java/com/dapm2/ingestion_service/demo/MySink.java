package com.dapm2.ingestion_service.demo;

import communication.message.Message;
import communication.message.impl.event.Event;
import pipeline.processingelement.Sink;

import java.util.HashMap;
import java.util.Map;

public class MySink extends Sink {

    @Override
    public void observe(Message message) {
        System.out.println(this + " received: " + message);
        try {
            Thread.sleep(2000); // ⏸️ Pause for 2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Good practice
            System.err.println("Sink interrupted while sleeping.");
        }
    }

    @Override
    protected Map<Class<? extends Message>, Integer> setConsumedInputs() {
        Map<Class<? extends Message>, Integer> map = new HashMap<>();
        map.put(Event.class, 1);
        return map;
    }
}
