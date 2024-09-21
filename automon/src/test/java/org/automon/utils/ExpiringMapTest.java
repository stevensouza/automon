package org.automon.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpiringMapTest {


    @BeforeEach
    public void setUp() throws Exception {

    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void testRemove_IfEmpty() {
        ExpiringMap<String, Expirable> map = new ExpiringMap<>();
        map.removeOldEntries();
        // no exception should be throw
    }

    @Test
    public void testRemove_Every() {
        ExpiringMap<String, Expirable> map = new ExpiringMap<>();
        Expirable object1 = mock(Expirable.class);
        when(object1.isExpired()).thenReturn(true);
        // Note the sequence is to input the new value and then remove any so if an object is put in already in an
        // expired state it will always be removed too.
        map.put("Hello", object1);
        map.put("Cruel", object1);
        map.put("World", object1);
        assertThat(map).isEmpty();
    }

    @Test
    public void testRemove_None() {
        ExpiringMap<String, Expirable> map = new ExpiringMap<>();
        Expirable object1 = mock(Expirable.class);
        when(object1.isExpired()).thenReturn(false);
        // Note the sequence is to input the new value and then remove any so if an object is put in already in an
        // expired state it will always be removed too.
        map.put("Hello", object1);
        map.put("Cruel", object1);
        map.put("World", object1);
        assertThat(map).containsOnlyKeys("Hello", "Cruel", "World");
    }

    @Test
    public void testRemove_Expired() {
        ExpiringMap<String, Expirable> map = new ExpiringMap<>();
        Expirable object1 = mock(Expirable.class);
        Expirable object2 = mock(Expirable.class);
        Expirable object3 = mock(Expirable.class);
        Expirable object4 = mock(Expirable.class);

        when(object1.isExpired()).thenReturn(false);
        when(object2.isExpired()).thenReturn(false);
        when(object3.isExpired()).thenReturn(false);
        when(object4.isExpired()).thenReturn(false);

        // Note the sequence is to input the new value and then remove any so if an object is put in already in an
        // expired state it will always be removed too.
        map.put("Hello", object1);
        map.put("Cruel", object2);
        assertThat(map).containsOnlyKeys("Hello", "Cruel");
        map.put("World", object3);
        assertThat(map).containsOnlyKeys("Hello", "Cruel", "World");

        // expire objects so they will be removed on next put.
        when(object1.isExpired()).thenReturn(true); // object1 is the oldest so it will trigger removal loop on next put
        when(object2.isExpired()).thenReturn(true);
        map.put("Now", object4);
        assertThat(map).containsOnlyKeys("World", "Now");
    }
}