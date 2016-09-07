package io.tjeubaoit.pokeradar.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.tjeubaoit.pokeradar.R;
import io.tjeubaoit.pokeradar.api.PokeRadarService;
import io.tjeubaoit.pokeradar.api.ServiceGenerator;
import io.tjeubaoit.pokeradar.model.PokeRadarResponse;
import io.tjeubaoit.pokeradar.model.Pokemon;
import io.tjeubaoit.pokeradar.util.BitmapUtils;
import io.tjeubaoit.pokeradar.util.Logger;
import io.tjeubaoit.pokeradar.util.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        PokemonMapFilterAdapter.OnStateChangeListener {

    private static final int DEFAULT_ZOOM = 14;
    private static final double DEFAULT_LAT = 21.0333333;
    private static final double DEFAULT_LON = 105.85;

    private static final float DEFAULT_SCALE = 0.3f;
    private static final int SHOW_MARKER_DELAY = 3;

    private static final Logger LOGGER = Logger.getLogger(MainActivity.class);

    private MapView mapView;
    private Map<String, Marker> markers = new HashMap<>();
    private GoogleMap map;
    private LatLngBounds bounds;
    private boolean flagLoading = false;
    private Set<Integer> disabledPokemonIds = new HashSet<>();

    private final Handler handler = new Handler();
    private final PokeRadarService service = ServiceGenerator.create(PokeRadarService.class);

    private final Runnable clearOutBoundsMarkersRunnable = new Runnable() {
        @Override
        public void run() {
            final List<String> forRemoved = new ArrayList<>();
            for (String key : markers.keySet()) {
                if (!isInBounds(markers.get(key).getPosition())) {
                    forRemoved.add(key);
                    markers.get(key).remove();
                }
            }
            for (String key : forRemoved) {
                markers.remove(key);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeTask();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                updateMapAfterSettingsChanged();
            }
        });
        toggle.syncState();

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // init pokemon filter list view
        ListView listView = (ListView) findViewById(R.id.list_pokemon_filter);
        PokemonMapFilterAdapter adapter = new PokemonMapFilterAdapter(this);
        adapter.setOnPokemonStateChangedListener(this);
        listView.setAdapter(adapter);

        for (int i = 1; i <= Resources.SIZE; i++) {
            PokemonMapFilterAdapter.Model model = new PokemonMapFilterAdapter.Model();
            model.enabled = true;
            model.pokemonId = i;
            adapter.add(model);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        LatLng hanoi = new LatLng(DEFAULT_LAT, DEFAULT_LON);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            LOGGER.warn("Not have location permissions");
            return;
        }

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnCameraMoveListener(this);
        map.setOnCameraIdleListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, DEFAULT_ZOOM));

        map.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MainActivity.this,
                        "Show direction for: " + marker.getPosition().latitude + ","
                                + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
            }
        });
    };

    @Override
    public void onCameraIdle() {
        if (!flagLoading) {
            // remove all in-queue callbacks
            handler.removeCallbacksAndMessages(null);

            // register new callback that executed after 0.5 second
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    executeTask();
                }
            }, 500);
        }
        bounds = map.getProjection().getVisibleRegion().latLngBounds;
        LOGGER.debug("Camera idle");
    }

    @Override
    public void onCameraMove() {
        // get current view bounds of map
        bounds = map.getProjection().getVisibleRegion().latLngBounds;
    }

    @Override
    public void onStateChange(int pokemonId, boolean enabled) {
        if (enabled) {
            disabledPokemonIds.remove(pokemonId);
        } else {
            disabledPokemonIds.add(pokemonId);
        }
    }

    private void updateMapAfterSettingsChanged() {
        for (Map.Entry<String, Marker> entry : markers.entrySet()) {
            try {
                int pokemonId = Integer.parseInt(entry.getKey().split("-")[1]);
                entry.getValue().setVisible(!disabledPokemonIds.contains(pokemonId));
            } catch (Exception e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }
    }

    private void executeTask() {
        if (bounds == null) {
            LOGGER.info("Current bounds is null, return");
            return;
        }
        flagLoading = true;

        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;
        LOGGER.info("Request pokemons in bounds: " + bounds);

        Call<PokeRadarResponse> call = service.trackPokemons(
                sw.latitude, ne.latitude, sw.longitude, ne.longitude,
                new TreeMap<String, String>());
        call.enqueue(new Callback<PokeRadarResponse>() {
            @Override
            public void onResponse(Call<PokeRadarResponse> call, Response<PokeRadarResponse> response) {
                if (!response.isSuccessful()) {
                    handleLoadDataFailed("Response code: " + response.code()
                            + ", message: " + response.message());
                    return;
                }
                if (!response.body().success) {
                    String errorMessage = "";
                    for (String error : response.body().errors) {
                        errorMessage += error + "\n";
                    }
                    handleLoadDataFailed(errorMessage);
                    return;
                }

                List<Pokemon> pokemons = response.body().data;
                LOGGER.info("Get pokemons success, " + pokemons.size() + " results");

                showPokemons(pokemons);
                flagLoading = false;
            }

            @Override
            public void onFailure(Call<PokeRadarResponse> call, Throwable t) {
                handleLoadDataFailed("Load data failed", t);
            }
        });
    }

    private void showPokemons(List<Pokemon> pokemons) {
        if (pokemons == null || pokemons.isEmpty()) {
            LOGGER.info("No pokemon to show");
            return;
        }
        for (int i = 0; i < pokemons.size(); i++) {
            final Pokemon pokemon = pokemons.get(i);
            if (!pokemon.isTrustedPokemon()) {
                LOGGER.info("Is not trusted pokemon, trainer name: " + pokemon.trainerName);
                continue;
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addNewMarker(pokemon, !disabledPokemonIds.contains(pokemon.pokemonId));
                }
            }, SHOW_MARKER_DELAY * i);
            handler.postDelayed(clearOutBoundsMarkersRunnable, pokemons.size() * SHOW_MARKER_DELAY);
        }
    }

    private void handleLoadDataFailed(String message) {
        LOGGER.error(message);
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleLoadDataFailed(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void addNewMarker(Pokemon pokemon, boolean visible) {
        if (pokemon.latitude == 0 && pokemon.longitude == 0) {
            LOGGER.info("Position invalid lat: 0, lon: 0");
            return;
        }
        if (map == null) {
            LOGGER.info("Map is null, return");
            return;
        }
        if (!isInBounds(pokemon.latitude, pokemon.longitude)) {
            LOGGER.info(String.format(Locale.US, "Current pokemon (%f, %f) not in bounds, cancel add",
                    pokemon.latitude, pokemon.longitude));
            return;
        }

        String key = pokemon.id;
        if (!markers.containsKey(key)) {
            int resId = Resources.getPokemonDrawable(pokemon.pokemonId);
            Bitmap bitmap = BitmapUtils.getScaledBitmap(this, resId, DEFAULT_SCALE);
            MarkerOptions markerOptions = new MarkerOptions()
                    .visible(visible)
                    .title(StringUtils.createMarkerTitleForPokemon(pokemon))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .snippet(StringUtils.createMarkerSnippetForPokemon(pokemon))
                    .position(new LatLng(pokemon.latitude, pokemon.longitude))
                    .draggable(false);
            markers.put(key, map.addMarker(markerOptions));
        }
    }

    private boolean isInBounds(LatLng ll) {
        return isInBounds(ll.latitude, ll.longitude);
    }

    private boolean isInBounds(double lat, double lon) {
        return bounds != null && bounds.contains(new LatLng(lat, lon));
    }
}
