package com.example.routeplanner.features.container.addressListFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.Address;

import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.CustomViewHolder>{

    private final String debugTag = "debugTag";

    private List<Address> addressList;
    private AdapterCallback callback;
    private Context context;

    public AddressListAdapter(AdapterCallback callback, List<Address> addressList) {
        this.addressList = addressList;
        this.callback = callback;
    }

    interface AdapterCallback{
        void itemClick(Address address);
        void showAddress(Address address);
    }

    void addContext(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_address, parent, false);
        return new CustomViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Address address = addressList.get(position);

        if(addressList.indexOf(address) > 0){
            holder.address_info_wrapper.setBackgroundResource(R.drawable.list_item_bg);
        }

        if(address.isValid()){
            holder.streetTv.setText(address.getStreet());
            holder.cityTv.setText(address.getPostCode() +" "+ address.getCity());
            holder.packageCountTv.setText(String.valueOf(address.getPackageCount()) + " x");

            if(address.isBusiness()){
                holder.addressType.setImageResource(R.drawable.company_outline_128_ic);
            }else{
                holder.addressType.setImageResource(R.drawable.house_outline3_128_ic);
            }

        }else{
            holder.streetTv.setText(address.getAddress());
        }

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup itemWrapper;
        private ConstraintLayout address_info_wrapper;
        private TextView streetTv;
        private TextView cityTv;
        private TextView packageCountTv;
        private ImageView addressType;

        CustomViewHolder(View itemView) {
            super(itemView);
            itemWrapper = itemView.findViewById(R.id.item_wrapper);
            address_info_wrapper = itemView.findViewById(R.id.address_info_wrapper);
            streetTv = itemView.findViewById(R.id.street_tv);
            cityTv = itemView.findViewById(R.id.city_tv);
            packageCountTv = itemView.findViewById(R.id.packageCount_tv);
            addressType = itemView.findViewById(R.id.address_type_iv);
            itemWrapper.setOnClickListener(this);

            itemWrapper.setOnLongClickListener(view -> {
                callback.showAddress(addressList.get(getAdapterPosition()));
                return true;// returning true instead of false, works for me
            });

        }

        public void onClick(View v) {

            if (v == itemWrapper) {
                callback.itemClick(addressList.get(getAdapterPosition()));
            }
        }
    }
}
