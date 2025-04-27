package com.tihonya.datingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisitCounterServiceTest {

    private VisitCounterService visitCounterService;

    @BeforeEach
    void setUp() {
        visitCounterService = new VisitCounterService();
    }

    @Test
    void testIncrementAndGetAllCounts_singleIncrement() {
        visitCounterService.increment("/test");

        Map<String, Integer> counts = visitCounterService.getAllCounts();

        assertEquals(1, counts.get("/test"));
    }

    @Test
    void testIncrementMultipleTimes() {
        visitCounterService.increment("/test");
        visitCounterService.increment("/test");
        visitCounterService.increment("/test");

        Map<String, Integer> counts = visitCounterService.getAllCounts();

        assertEquals(3, counts.get("/test"));
    }

    @Test
    void testIncrementMultipleUrls() {
        visitCounterService.increment("/url1");
        visitCounterService.increment("/url2");
        visitCounterService.increment("/url1");

        Map<String, Integer> counts = visitCounterService.getAllCounts();

        assertEquals(2, counts.get("/url1"));
        assertEquals(1, counts.get("/url2"));
    }
}
