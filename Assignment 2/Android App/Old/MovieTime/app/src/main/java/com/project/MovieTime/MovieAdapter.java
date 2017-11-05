package com.project.MovieTime;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> { //Class that will use the recylcer view for the Cards.


    //Variables that are used for adding to the database and lists used for the Recycler view.
    private Context adaptContext;
    private List<Movie> movieList;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference myRef;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year;
        public ImageView thumbnail, overflow;                       //Creating a view holder that will be used for the card view.

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            year = (TextView) view.findViewById(R.id.Year);             //Getting all the Controls from the View.
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public MovieAdapter(Context adaptContext, List<Movie> movieList) {
        this.adaptContext = adaptContext;           //Giving the MovieAdapter the Movielist
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
        holder.title.setText(movie.getOriginal_title());        //Adding the details of the object in the list to a Card View. And Getting the image from the path.
        holder.year.setText(movie.getRelease_date());

        //Putting the Image into the Thumbnail portion of the card holder.
        Glide.with(adaptContext).load("http://image.tmdb.org/t/p/w300"+ movie.getPoster_path()).into(holder.thumbnail);


        //Setting an onclick listener for the Menu button, that will take in the position of the object pressed for Firebase stuff.
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, int position) {   //Method that will show the menu.
        // inflate menu
        PopupMenu popup = new PopupMenu(adaptContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_movie, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position)); //Setting onclicl
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        int position;

        public MyMenuItemClickListener(int position) {      //Constructor that takes the position of the object in the recycler view, relative to the list of movies.

            this.position = position;

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_set_reminder:      //When use pressed Set Reminder, it will add the Moview object to the Firebase db.
                    Toast.makeText(adaptContext, "Set Reminder", Toast.LENGTH_SHORT).show();

                    myRef =  database.getReference("Reminders/"+mAuth.getCurrentUser().getUid()); //Getting the reference wit the users ID

                    String PushKey = myRef.push().getKey();         //Getting unique push key

                    Movie movie = movieList.get(position);  //Getting the specific movie object

                    //movie.setVote_average((Float) movie.getVote_average());

                    myRef.child(PushKey).setValue(movie);       //Pusing the new moviw to the reminders in the Database.

                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }       //Getting the size of the movieList.
}
