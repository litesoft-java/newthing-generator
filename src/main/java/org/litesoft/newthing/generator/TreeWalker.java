package org.litesoft.newthing.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class TreeWalker {
    private final String walkRooDir;
    private final BiConsumer<String, Path> pathProcessor;

    public TreeWalker( String walkRooDir, BiConsumer<String, Path> pathProcessor ) {
        this.walkRooDir = walkRooDir;
        this.pathProcessor = pathProcessor;
    }

    public TreeWalker( BiConsumer<String, Path> pathProcessor, String dir, String... moreDirs ) {
        this( Paths.get( dir, moreDirs ).toString(), pathProcessor );
    }

    public TreeWalker walk( String target )
            throws IOException {
        Path path = Paths.get( walkRooDir, target + "..." );
        if ( !path.toFile().isDirectory() ) {
            throw new IOException( "expected directory at: " + path );
        }
        try ( Stream<Path> walk = Files.walk( path ) ) {
            walk.filter( Files::isRegularFile ).forEach( filePath -> pathProcessor.accept( target, filePath ) );
        }
        return this;
    }
}
