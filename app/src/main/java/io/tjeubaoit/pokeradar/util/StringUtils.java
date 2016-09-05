package io.tjeubaoit.pokeradar.util;

import java.util.Locale;

import io.tjeubaoit.pokeradar.model.Pokemon;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class StringUtils {

    public static String createMarkerTitleForPokemon(Pokemon pokemon) {
        return String.format(Locale.US, "Pokemon: <b>%s</b>", pokemon.getPokemonName());
    }

    public static String createMarkerSnippetForPokemon(Pokemon pokemon) {
        return String.format(Locale.US, "Time Found: <b>%s</b><br/>Lat/Lon: <b>%f,%f</b>",
                pokemon.getTimeCreatedAsText(), pokemon.latitude, pokemon.longitude);
    }
}
