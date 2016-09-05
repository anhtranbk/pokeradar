package io.tjeubaoit.pokeradar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.tjeubaoit.pokeradar.ui.Resources;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pokemon {

    public static final String POKE_RADAR_PREDICTION = "(Poke Radar Prediction)";

    public String id;
    public long created;
    public int downvotes;
    public int upvotes;
    public double latitude;
    public double longitude;
    public int pokemonId;
    public String trainerName;
    public String userId;
    public String deviceId;

    public String getTimeCreatedAsText() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa", Locale.US);
        return sdf.format(new Date(created * 1000));
    }

    public String getPokemonName() {
        String name = Resources.getPokemonName(pokemonId);
        if (!Character.isUpperCase(name.charAt(0))) {
            name = Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
        }
        return name;
    }
}
