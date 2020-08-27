package com.example.routeplanner.features.container.addressDetailsFragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.database.AddressInformation;

public class AddressDetailsAdapter extends RecyclerView.Adapter <AddressDetailsAdapter.CustomViewHolder>{

    private AddressInformation addressInformation;
    private CommentListFunctions commentListFunctions;
    private CommentInformation commentInformation;

    AddressDetailsAdapter(AddressInformation addressInformation, CommentListFunctions commentListFunctions) {
        this.commentListFunctions = commentListFunctions;
        this.commentInformation = new CommentInformation();

        if(addressInformation == null){
            this.addressInformation = new AddressInformation();
            this.addressInformation.setCommentsCount(0);
        }else{
            this.addressInformation = addressInformation;
        }

    }

    public interface CommentListFunctions{
        void onListItemClick(CommentInformation commentInformation);
    }

    @Override
    public AddressDetailsAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

//        if(position == addressInformation.getCommentsCount()-1){
//            holder.itemWrapper.setBackgroundResource(R.drawable.last_comment_item_bg);
//        }

        holder.employedName.setText(addressInformation.getEmployeeId().get(position));
        holder.date.setText(addressInformation.getDates().get(position));
        holder.comment.setText(addressInformation.getComments().get(position));
    }

    @Override
    public int getItemCount() {
        return addressInformation.getCommentsCount();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup itemWrapper;
        private TextView employedName;
        private TextView date;
        private TextView comment;

        CustomViewHolder(View itemView) {
            super(itemView);
            this.employedName = itemView.findViewById(R.id.username_tv);
            this.date = itemView.findViewById(R.id.date_tv);
            this.comment = itemView.findViewById(R.id.comment_tv);
            this.itemWrapper = itemView.findViewById(R.id.item_wrapper);
            this.itemWrapper.setOnClickListener(this);
        }

        public void onClick(View v) {
            commentInformation.setEmployeeId(addressInformation.getEmployeeId().get(this.getAdapterPosition()));
            commentInformation.setDate(addressInformation.getDates().get(this.getAdapterPosition()));
            commentInformation.setComment(addressInformation.getComments().get(this.getAdapterPosition()));
            commentListFunctions.onListItemClick(commentInformation);
        }
    }

}
