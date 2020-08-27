package com.example.routeplanner.features.container.driveListFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.Address;
import com.example.routeplanner.data.pojos.api.Drive;

import java.util.List;

public class DriveListAdapter extends RecyclerView.Adapter<DriveListAdapter.CustomViewHolder>{

    private final String debugTag = "debugTag";

    private List<Drive> driveList;
    private AdapterCallback callback;
    private Context context;

    public DriveListAdapter(AdapterCallback callback, List<Drive> driveList) {
        this.driveList = driveList;
        this.callback = callback;
    }

    interface AdapterCallback{
        void itemClick(Address address);
        void goButtonClick(Drive drive);
        void completeDrive(Drive drive);
    }

    void addContext(Context context){
        this.context = context;
    }

    void addTouchHelper(RecyclerView recyclerView){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_drive, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        Drive drive = driveList.get(position);

        String city = drive.getDestinationAddress().getPostCode() + " " + drive.getDestinationAddress().getCity();
        String distance = "Distance: "+drive.getDriveDistanceHumanReadable();
        String duration = "Duration: "+drive.getDriveDurationHumanReadable();
        String arrivalTime = drive.getDeliveryTimeHumanReadable();
        String timeDifference = drive.getTimeDiffString();

        holder.positionTextView.setText(String.valueOf(driveList.indexOf(drive)+1));
        holder.streetTextView.setText(drive.getDestinationAddress().getStreet());
        holder.cityTextView.setText(city);
        holder.distanceTextView.setText(distance);
        holder.durationTextView.setText(duration);
        holder.estimatedArrivalTime.setText(arrivalTime);
        holder.timeDiff.setText(timeDifference);

        if(drive.isDestinationIsABusiness()){
            holder.addressType.setImageResource(R.drawable.business_ic);
        }else{
            holder.addressType.setImageResource(R.drawable.home_ic);
        }

        if(drive.getDone() == 1){
            holder.itemView.setAlpha(0.5f);

            if(timeDifference.contains("+")){
                holder.timeDiff.setTextColor(Color.RED);
            }else{
                holder.timeDiff.setTextColor(Color.GREEN);
            }

            holder.timeDiff.setVisibility(View.VISIBLE);
        }else{
            holder.itemView.setAlpha(1f);
            holder.timeDiff.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return driveList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView positionTextView;
        private TextView streetTextView;
        private TextView cityTextView;
        private TextView distanceTextView;
        private TextView durationTextView;
        private TextView estimatedArrivalTime;
        private TextView timeDiff;
        private ImageView addressType;

        private ViewGroup itemWrapper;
        private ConstraintLayout goIvWrapper;
        private ImageView goIv;

        CustomViewHolder(View itemView) {
            super(itemView);
            this.positionTextView = itemView.findViewById(R.id.positionTextView);
            this.streetTextView = itemView.findViewById(R.id.streetTextView);
            this.cityTextView = itemView.findViewById(R.id.cityTextView);
            this.distanceTextView = itemView.findViewById(R.id.distanceTextView);
            this.durationTextView = itemView.findViewById(R.id.durationTextView);
            this.estimatedArrivalTime = itemView.findViewById(R.id.estimatedArrivalTimeTextView);
            this.timeDiff = itemView.findViewById(R.id.timeDiff_tv);
            this.addressType = itemView.findViewById(R.id.addressTypeImageView);
            this.goIv = itemView.findViewById(R.id.go_iv);
            this.itemWrapper = itemView.findViewById(R.id.item_wrapper);
            this.goIvWrapper = itemView.findViewById(R.id.goIvWrapper);

            this.itemWrapper.setOnClickListener(this);
            this.goIvWrapper.setOnClickListener(this);
        }

        public void onClick(View v) {

            if(v == this.itemWrapper){
                callback.itemClick(driveList.get(this.getAdapterPosition()).getDestinationAddress());
            }
            else if(v == this.goIvWrapper){
                callback.goButtonClick(driveList.get(this.getAdapterPosition()));
            }
        }
    }

    private ItemTouchHelper.Callback createHelperCallback(){

        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            //not used, as the first parameter above is 0
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                callback.completeDrive(driveList.get(position));
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;

                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    Paint backgroundPaint = new Paint();
                    Paint textPaint = new Paint();
                    Bitmap icon;

                    if (dX > 0) {

                        backgroundPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.green, null));

                        textPaint.setColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
                        textPaint.setTextSize(50f);

                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, backgroundPaint);

                        c.drawText("✔   Done",
                                (float) itemView.getLeft() + width,
                                (float) itemView.getTop() + (height*(float)0.6),
                                textPaint);

//                        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_delete_white_24dp);
//                        RectF iconDest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
//                        c.drawBitmap(icon, null, iconDest, p);

//                        Log.d("debug", "Top: "+itemView.getTop());
//                        Log.d("debug", "Bottom: "+itemView.getBottom());
//                        Log.d("debug", "Left: "+itemView.getLeft());
//                        Log.d("debug", "Right: "+itemView.getRight());
//                        Log.d("debug", "Height: "+height);
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
    }
}