package io.tjeubaoit.pokeradar.api;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class ServiceGenerator {

    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl("https://www.pokeradar.io/api/v1/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    public static <T> T create(Class<T> tClass) {
        return RETROFIT.create(tClass);
    }
}
