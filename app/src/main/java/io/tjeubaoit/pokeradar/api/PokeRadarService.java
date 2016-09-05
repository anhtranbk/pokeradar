package io.tjeubaoit.pokeradar.api;

import java.util.Map;

import io.tjeubaoit.pokeradar.model.PokeRadarResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public interface PokeRadarService {

    @GET("submissions")
    Call<PokeRadarResponse> trackPokemons(@Query("minLatitude") double minLatitude,
                                          @Query("maxLatitude") double maxLatitude,
                                          @Query("minLongitude") double minLongitude,
                                          @Query("maxLongitude") double maxLongitude,
                                          @QueryMap() Map<String, String> extraFields);
}
