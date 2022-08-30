package org.litesoft.newthing.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.litesoft.annotations.PackageFriendlyForTesting;

public class Substitutions implements Function<String, String> {
    @PackageFriendlyForTesting final Map<String, String> substitutions = new HashMap<>();

    @Override
    public String apply( String line ) {
        if ( line.isEmpty() ) {
            return line;
        }
        Processor processor = new Processor( line );
        substitutions.entrySet().forEach( processor );
        return processor.toString();
    }

    // "NewThing" -> "CamelCase"
    // "NewThing" -> "Capital"
    //
    // "newThing" -> "camelCase"
    // "newThing" -> "capital"
    //
    // "new_thing" -> "camel_case"
    // "new_thing" -> "capital"
    //
    // "new_things" -> "camel_cases"
    // "new_things" -> "capitals"
    public Substitutions( String camelFrom, String camelTo ) {
        List<String> upperCaseChunksFrom = camelChunk( camelFrom );
        List<String> lowerCaseChunksFrom = upperCaseChunksFrom.stream().map( String::toLowerCase ).toList();
        List<String> upperCaseChunksTo = camelChunk( camelTo );
        List<String> lowerCaseChunksTo = upperCaseChunksTo.stream().map( String::toLowerCase ).toList();

        String underscoredFrom = makeUnderscored( lowerCaseChunksFrom );
        String underscoredTo = makeUnderscored( lowerCaseChunksTo );

        substitutions.put( camelFrom, camelTo ); // No munging
        substitutions.put( makeFirstLower( lowerCaseChunksFrom, upperCaseChunksFrom ), makeFirstLower( lowerCaseChunksTo, upperCaseChunksTo ) );
        substitutions.put( underscoredFrom, underscoredTo );
        substitutions.put( underscoredFrom + "s", underscoredTo + "s" );
    }

    private static String makeFirstLower( List<String> lowerCaseChunks, List<String> upperCaseChunks ) {
        StringBuilder sb = new StringBuilder();
        sb.append( lowerCaseChunks.get( 0 ) );
        for ( int i = 1; i < lowerCaseChunks.size(); i++ ) {
            sb.append( upperCaseChunks.get( i ) );
        }
        return sb.toString();
    }

    private static String makeUnderscored( List<String> lowerCaseChunks ) {
        StringBuilder sb = new StringBuilder();
        sb.append( lowerCaseChunks.get( 0 ) );
        for ( int i = 1; i < lowerCaseChunks.size(); i++ ) {
            sb.append( '_' ).append( lowerCaseChunks.get( i ) );
        }
        return sb.toString();
    }

    private static List<String> camelChunk( String camel ) {
        List<String> chunks = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if ( !Character.isUpperCase( camel.codePointAt( 0 ) ) ) {
            throw new IllegalStateException( "Did NOT start with an uppercase character: '" + camel + "'" );
        }
        camel.codePoints().forEach( point -> {
            if ( Character.isUpperCase( point ) ) {
                if ( !sb.isEmpty() ) {
                    chunks.add( sb.toString() );
                    sb.setLength( 0 );
                }
            }
            sb.append( Character.toChars( point ) );
        } );
        if ( !sb.isEmpty() ) {
            chunks.add( sb.toString() );
        }
        return chunks;
    }

    private static class Processor implements Consumer<Map.Entry<String, String>> {
        private final StringBuilder sb;

        private Processor( String line ) {
            sb = new StringBuilder( line );
        }

        private void replace( String key, String value ) {
            int at, from = 0;
            while ( -1 != (at = sb.indexOf( key, from )) ) {
                sb.replace( at, at + key.length(), value );
                from = at + value.length();
            }
        }

        @Override
        public void accept( Map.Entry<String, String> subPair ) {
            replace( subPair.getKey(), subPair.getValue() );
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
