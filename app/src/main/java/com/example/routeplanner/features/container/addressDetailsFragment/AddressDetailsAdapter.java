package com.example.routeplanner.features.container.addressDetailsFragment;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.routeplanner.R;
import com.example.routeplanner.data.pojos.CommentInformation;
import com.example.routeplanner.data.pojos.database.Notes;

public class AddressDetailsAdapter extends RecyclerView.Adapter <AddressDetailsAdapter.CustomViewHolder>{

    private Notes notes;
    private CommentListFunctions commentListFunctions;
    private CommentInformation commentInformation;

    AddressDetailsAdapter(Notes notes, CommentListFunctions commentListFunctions) {
        this.commentListFunctions = commentListFunctions;
        this.commentInformation = new CommentInformation();

        if(notes == null){
            this.notes = new Notes();
            this.notes.setNotesCount(0);
        }else{
            this.notes = notes;
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

        if(position > 0){
            holder.infoWrapper.setBackgroundResource(R.drawable.list_item_bg);
        }

        holder.employedName.setText(notes.getAuthors().get(position));
        holder.date.setText(notes.getDates().get(position));
        holder.comment.setText(notes.getNotes().get(position));
    }

    @Override
    public int getItemCount() {
        return notes.getNotesCount();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ViewGroup itemWrapper;
        private ConstraintLayout infoWrapper;
        private TextView employedName;
        private TextView date;
        private TextView comment;

        CustomViewHolder(View itemView) {
            super(itemView);
            this.employedName = itemView.findViewById(R.id.username_tv);
            this.infoWrapper = itemView.findViewById(R.id.infoWrapper);
            this.date = itemView.findViewById(R.id.date_tv);
            this.comment = itemView.findViewById(R.id.comment_tv);
            this.itemWrapper = itemView.findViewById(R.id.item_wrapper);
            this.itemWrapper.setOnClickListener(this);
        }

        public void onClick(View v) {
            commentInformation.setEmployeeId(notes.getAuthors().get(this.getAdapterPosition()));
            commentInformation.setDate(notes.getDates().get(this.getAdapterPosition()));
            commentInformation.setComment(notes.getNotes().get(this.getAdapterPosition()));
            commentListFunctions.onListItemClick(commentInformation);
        }
    }

}
