package org.litesoft.newthing.generator;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubstitutionsTest {

    @Test
    void verifyMappings() {
        new Checker( "NewThing", "CamelCase" )
                .assertContains( "NewThing", "CamelCase" )
                .assertContains( "newThing", "camelCase" )
                .assertContains( "new_thing", "camel_case" )
                .assertContains( "new_things", "camel_cases" )
                .assertAllChecked();

        new Checker( "NewThing", "Capital" )
                .assertContains( "NewThing", "Capital" )
                .assertContains( "newThing", "capital" )
                .assertContains( "new_thing", "capital" )
                .assertContains( "new_things", "capitals" )
                .assertAllChecked();
    }

    static class Checker {
        private final Map<String, String> substitutions;

        public Checker( String camelFrom, String camelTo ) {
            substitutions = new Substitutions( camelFrom, camelTo ).substitutions;
        }

        Checker assertContains( String formFrom, String formTo ) {
            String value = substitutions.remove( formFrom );
            assertNotNull( value, () -> "No entry with 'formFrom' of: " + formFrom );
            assertEquals( formTo, value, () -> "Wrong value for 'formFrom' of: " + formFrom );
            return this;
        }

        void assertAllChecked() {
            assertTrue( substitutions.isEmpty(), () -> "Entries not checked: " + substitutions.entrySet() );
        }
    }
}
