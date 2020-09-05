package com.example.routeplanner.features.container;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.routeplanner.R;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

public class PredictionsAdapter extends RecyclerView.Adapter<PredictionsAdapter.CustomViewHolder>{

    private final String debugTag = "debugTag";

    private List<AutocompletePrediction> predictions;
    private AdapterCallback callback;

    PredictionsAdapter(AdapterCallback callback, List<AutocompletePrediction> predictions) {
        this.callback = callback;
        this.predictions = predictions;
    }

    interface AdapterCallback{
        void predictionClick(String address);
        void predictionSelected(String address);
    }
    @Override
    public PredictionsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_prediction, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PredictionsAdapter.CustomViewHolder holder, int position) {
        AutocompletePrediction currentAddress = predictions.get(position);

        if(position > 0){
            holder.wrapper.setBackgroundResource(R.drawable.list_item_bg);
        }

        holder.addressPrimaryText.setText(currentAddress.getPrimaryText(null).toString());
        holder.addressSecondaryText.setText(currentAddress.getSecondaryText(null).toString());
    }

    @Override
    public int getItemCount() {

        if(predictions.size() > 4){
            return predictions.size()-1;
        }else{
            return predictions.size();
        }
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ConstraintLayout wrapper;
        private ConstraintLayout addPredictionBtnWrapper;
        private TextView addressPrimaryText;
        private TextView addressSecondaryText;
        private ImageView addPredictionBtn;

        CustomViewHolder(View itemView) {
            super(itemView);
            this.wrapper = itemView.findViewById(R.id.prediction_wrapper);
            this.addPredictionBtnWrapper = itemView.findViewById(R.id.addPredictionBtnWrapper);
            this.addressPrimaryText = itemView.findViewById(R.id.addressPrimaryText);
            this.addressSecondaryText = itemView.findViewById(R.id.addressSecondaryText);
            this.addPredictionBtn = itemView.findViewById(R.id.addPredictionBtn);
            wrapper.setOnClickListener(this);
            addPredictionBtnWrapper.setOnClickListener(this);
            //addPredictionBtn.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v == addPredictionBtnWrapper) {
                callback.predictionClick(predictions.get(getAdapterPosition()).getPrimaryText(null).toString());
            }else if(v == wrapper){
                callback.predictionSelected(predictions.get(getAdapterPosition()).getFullText(null).toString());
            }
        }
    }


}
