package org.litesoft.generated;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewThingImplTest {
    @Test
    void testGetters() {
        NewThing org = new NewThingImpl();
        assertEquals( "Fred", org.getContactName() );
        assertEquals( "fred@example.com", org.getContactEmail() );
    }
}