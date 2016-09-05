package io.tjeubaoit.pokeradar.ui;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import io.tjeubaoit.pokeradar.R;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private View view;

    public MyInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_map_info_window, null);
        }

        TextView tvTitle = (TextView) view.findViewById(R.id.text_title);
        tvTitle.setText(Html.fromHtml(marker.getTitle()));

        TextView tvSnippet = (TextView) view.findViewById(R.id.text_snippet);
        tvSnippet.setText(Html.fromHtml(marker.getSnippet()));

        return view;
    }
}
