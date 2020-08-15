package com.example.gasaberdeen;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GasStationAdapter extends RecyclerView.Adapter<GasStationAdapter.GasStationsVH> {

    private static final String TAG = "GasStationsAdapter";
    List<GasStations> GasList;

    public GasStationAdapter(List<GasStations> GasList) {
        this.GasList = GasList;
    }

    @NonNull
    @Override
    public GasStationsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gasstation_row, parent, false);
        return new GasStationsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GasStationsVH holder, int position) {

        GasStations movie = GasList.get(position);
        holder.titleTextView.setText(movie.getTitle());
        holder.yearTextView.setText(movie.getYear());
        holder.ratingTextView.setText(movie.getRating());
        holder.plotTextView.setText(movie.getPlot());

        boolean isExpanded = GasList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return GasList.size();
    }

    class GasStationsVH extends RecyclerView.ViewHolder {

        private static final String TAG = "GasStationsVH";

        ConstraintLayout expandableLayout;
        TextView titleTextView, yearTextView, ratingTextView, plotTextView;

        public GasStationsVH(@NonNull final View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            plotTextView = itemView.findViewById(R.id.plotTextView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);


            titleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    GasStations movie = GasList.get(getAdapterPosition());
                    movie.setExpanded(!movie.isExpanded());
                    notifyItemChanged(getAdapterPosition());

                }
            });
        }
    }
}