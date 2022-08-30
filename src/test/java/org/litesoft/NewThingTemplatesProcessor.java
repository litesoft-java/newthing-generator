package org.litesoft;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.litesoft.newthing.generator.SourceFileProcessor;
import org.litesoft.newthing.generator.TreeWalker;

@SuppressWarnings("NewClassNamingConvention")
class NewThingTemplatesProcessor {
    static final String NEW_THING_NEW_NAME = "Organization";

    @Test
    @Disabled
    void generateFiles()
            throws IOException {
        System.out.println( "************* NewThingTemplatesProcessor.processFiles *************" );
        SourceFileProcessor processor = new SourceFileProcessor( "NewThing", NEW_THING_NEW_NAME );
        new TreeWalker( processor, System.getProperty( "user.dir" ), "sampleNewThingsTemplate" )
                .walk( "main" )
                .walk( "test" );
    }

    @Test
    void verifyFiles()
            throws IOException {
        SourceFileProcessor processor = new SourceFileProcessor( "NewThing", NEW_THING_NEW_NAME )
                .verifyOnly();
        new TreeWalker( processor, System.getProperty( "user.dir" ), "sampleNewThingsTemplate" )
                .walk( "main" )
                .walk( "test" );
    }
}
