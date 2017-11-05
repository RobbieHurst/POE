package com.project.MovieTime;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> movieList;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference myRef;
                                                                        //Adapter that is similar to the MovieAdapter, except it deletes from the firbaseDB
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            year = (TextView) view.findViewById(R.id.Year);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public ReminderAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Movie movie = movieList.get(position);
        holder.title.setText(movie.getOriginal_title());
        holder.year.setText(movie.getRelease_date());

        Glide.with(mContext).load("http://image.tmdb.org/t/p/w300"+ movie.getPoster_path()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_reminders, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int position;

        public MyMenuItemClickListener(int position) {

            this.position = position;

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:        //Checking to see if the user has pressed Watch
                    Toast.makeText(mContext, "Watched", Toast.LENGTH_SHORT).show();

                    myRef =  database.getReference("Reminders/"+mAuth.getCurrentUser().getUid());

                    Movie movie = movieList.get(position);

                    String queryId = movie.getId();

                    Query query = myRef.orderByChild("id").equalTo(queryId);    //Query that will look for a Record that has the same Movie ID.

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot movieSnapShot: dataSnapshot.getChildren()) {
                                movieSnapShot.getRef().removeValue(); //Removing from the Snapshot
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    movieList.remove(position);         //Removing the movie from the list.
                    notifyItemRemoved(position);        //Telling the adapter that the moview has been released.

                    return true;

                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
