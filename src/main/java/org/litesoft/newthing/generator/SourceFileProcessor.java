package org.litesoft.newthing.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.litesoft.annotations.Significant;

public class SourceFileProcessor implements BiConsumer<String, Path> {
    public static final String PACKAGE_ = "package ";
    private final List<String> newLines = new ArrayList<>();
    private final Substitutions substitutions;
    private final String fromName;
    private final String toName;
    private boolean verifyOnly = false;

    private Path newDirPath;
    private String newFileName;

    public SourceFileProcessor( @Significant String fromName, @Significant String toName ) {
        this.fromName = Significant.AssertArgument.namedValue( "fromName", fromName );
        this.toName = Significant.AssertArgument.namedValue( "toName", toName );
        substitutions = new Substitutions( fromName, toName );
    }

    public SourceFileProcessor verifyOnly() {
        verifyOnly = true;
        return this;
    }

    private void setNewFileName( String[] fileNameParts ) {
        // clear other fields
        newLines.clear();
        newDirPath = null;

        StringBuilder sb = new StringBuilder().append( fileNameParts[0] );
        for ( int i = 1; i < fileNameParts.length; i++ ) {
            sb.append( toName ).append( fileNameParts[i] );
        }
        newFileName = sb.toString();
    }

    private String[] splitFileName( String fileName ) {
        String[] parts = fileName.split( fromName );
        if ( parts.length < 2 ) {
            throw new IllegalStateException( "Expected file name to contain '" + fromName + "', but file name was: " + fileName );
        }
        return parts;
    }

    private void populateNewDirPath( String target, String line ) {
        if ( !line.endsWith( ";" ) ) {
            throw new IllegalStateException( "Expected 'package '... line to end with a semicolon, but got: '" + line + "'" );
        }
        List<String> pathParts = new ArrayList<>();
        pathParts.add( target );
        pathParts.add( "java" );
        pathParts.addAll( Arrays.asList( line.substring( PACKAGE_.length(), line.length() - 1 ).trim().split( "\\." ) ) );
        newDirPath = Path.of( "src", pathParts.toArray( new String[0] ) );
    }

    private void processLine( String target, String line ) {
        String newLine = substitutions.apply( line.stripTrailing() );
        newLines.add( newLine );
        if ( newLine.startsWith( PACKAGE_ ) ) {
            populateNewDirPath( target, newLine );
        }
    }

    private void saveNewFile()
            throws IOException {
        String targetDir = (newDirPath != null) ? newDirPath.toString() : "???";
        System.out.println( "save " + newFileName + " (" + newLines.size() + " lines) to: " + targetDir );
        if ( newDirPath == null ) {
            throw new IOException( "no path created for: " + newFileName );
        }
        Files.createDirectories( newDirPath );
        Path filePath = newDirPath.resolve( newFileName );
        Files.write( filePath, newLines, StandardCharsets.UTF_8 ); // default StandardOpenOption(s): WRITE, CREATE, TRUNCATE_EXISTING
    }

    @Override
    public void accept( String target, Path path ) {
        File file = path.toFile();
        String[] fileNameParts = splitFileName( file.getName() ); // includes validation for "fromName"
        if ( !verifyOnly ) {
            setNewFileName( fileNameParts );
            try ( Stream<String> lines = Files.lines( path, StandardCharsets.UTF_8 ) ) {
                lines.forEach( line -> processLine( target, line ) );
                saveNewFile();
            }
            catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }
    }
}