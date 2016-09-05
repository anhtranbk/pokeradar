package io.tjeubaoit.pokeradar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokeRadarResponse {

    public List<Pokemon> data;
    public boolean success;
    public String[] errors;
}
