package io.tjeubaoit.pokeradar.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import io.tjeubaoit.pokeradar.R;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class PokemonMapFilterAdapter extends ArrayAdapter<PokemonMapFilterAdapter.Model> {

    private OnStateChangeListener listener;

    public PokemonMapFilterAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final Holder holder;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            v = inflater.inflate(R.layout.item_pokemon_filter, parent, false);

            holder = new Holder();
            holder.ivPokemon = (ImageView) v.findViewById(R.id.img_pokemon);
            holder.tvPokemonName = (TextView) v.findViewById(R.id.text_pokemon_name);
            holder.cbEnabled = (CheckBox) v.findViewById(R.id.cb_pokemon_enabled);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        final Model model = getItem(position);
        holder.ivPokemon.setImageResource(Resources.getPokemonDrawable(model.pokemonId));
        holder.tvPokemonName.setText(Resources.getPokemonName(model.pokemonId));
        holder.cbEnabled.setChecked(model.enabled);
        holder.cbEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (listener != null) {
                    listener.onStateChange(model.pokemonId, checked);
                }
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cbEnabled.performClick();
            }
        });

        return v;
    }

    public void setOnPokemonStateChangedListener(OnStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnStateChangeListener {
        void onStateChange(int pokemonId, boolean enabled);
    }

    static class Model {
        public int pokemonId;
        public boolean enabled;
    }

    static class Holder {
        public ImageView ivPokemon;
        public TextView tvPokemonName;
        public CheckBox cbEnabled;
    }
}
