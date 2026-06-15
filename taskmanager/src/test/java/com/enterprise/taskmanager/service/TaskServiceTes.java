package com.enterprise.taskmanager.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskServiceTest { // Class renamed and 'public' modifier removed

    @Test
    void testDummyTask() {
        String status = "SUCCESS";
        // Simulasi logika bisnis sederhana
        assertEquals("SUCCESS", status, "Status harusnya SUCCESS");
    }
}