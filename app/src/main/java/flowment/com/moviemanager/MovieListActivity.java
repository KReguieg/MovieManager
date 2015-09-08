package flowment.com.moviemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Khaled Reguieg <a href="mailto:Khaled.Reguieg@gmail.com">Khaled Reguieg</a> on 04.08.2015.
 * This class represents the list of movies a user has been adding and presents it in a nice look and feel.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * The list of all movies.
     */
    ArrayList<Movie> movieList = new ArrayList<Movie>();

    /**
     * The LinearLayout holding all the list elements and handling the vertical scrolling.
     */
    LinearLayout movieListLinearLayout;

    /**
     * The random number for choosing colors and images.
     */
    int mRndNumber;

    /**
     * The SQLiteHelper instance of my class, which handles the database.
     */
    MovieSQLiteHelper movieSQLiteHelper;

    /**
     * The SQLite database;
     */
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        movieListLinearLayout = (LinearLayout) findViewById(R.id.movieListLinearLayout);

        Intent intent = getIntent();
        String activity = intent.getStringExtra("activity");

        if (activity.equals("first")) {
            if (!movieList.isEmpty())
                System.out.println(movieList.get(0));
            fillListView();
        }
        if (activity.equals("second")) {
            fillListView();
        }
    }

    /**
     * This Method creates the layout and fills the vertical scrolling list with the layouts created.
     */
    public void fillListView() {
        // Get stuff from the database
        movieSQLiteHelper = new MovieSQLiteHelper(MovieListActivity.this, "DBMovie", null, 1);
        db = movieSQLiteHelper.getWritableDatabase();
        String[] fields = new String[]{"title", "director", "actors", "date", "city"};

        Cursor c = db.query("movie", fields, null, null, null, null, null);

        if (c.moveToFirst()) {
            do {
                Movie m = new Movie(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4));
                movieList.add(m);
            } while (c.moveToNext());
        }

        Integer[] preferredColors = new Integer[]{0xFFC41E3D, 0xFF7D1128, 0xFF4BB1A0, 0xFF055864, 0xFF19647E};
        Integer[] drawableIds = new Integer[]{
                R.drawable.darth_vader, R.drawable.death_star,
                R.drawable.storm_trooper,
                R.drawable.yoda, R.drawable.storm_trooper_old
        };

        // Creating the layout for the list programmatically for each movie object in the movieList
        for (Movie m : movieList) {
            int lastRnd = mRndNumber;
            do {
                mRndNumber = new Random().nextInt(preferredColors.length);
            }
            while (lastRnd == mRndNumber);
            RelativeLayout rl = new RelativeLayout(this);
            LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            relativeParams.setMargins(0, 0, 0, 25);
            rl.setLayoutParams(relativeParams);
            rl.requestLayout();
            rl.setBackgroundColor(preferredColors[mRndNumber]);
            rl.invalidate();
            ImageView movieImage = new ImageView(this);
            movieImage.setId(R.id.imageId);
            RelativeLayout carrierRL = new RelativeLayout(this);
            RelativeLayout.LayoutParams carrierLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            carrierLP.addRule(RelativeLayout.RIGHT_OF, movieImage.getId());
            carrierLP.setMargins(5, 5, 5, 5);
            carrierRL.setLayoutParams(carrierLP);

            movieImage.setImageResource(drawableIds[mRndNumber]);
            movieImage.setPadding(20, 20, 20, 20);

            final TextView movieTitle = new TextView(this);
            movieTitle.setId(R.id.movieTitle);
            movieTitle.setTextSize(28);
            movieTitle.setText(m.getTitle());

            TextView movieDirector = new TextView(this);
            movieDirector.setId(R.id.movieDirector);
            movieDirector.setTextSize(24);
            movieDirector.setText(m.getDirector());

            TextView movieActors = new TextView(this);
            movieActors.setId(R.id.movieActors);
            movieActors.setText(m.getActors());

            TextView movieWatchedOn = new TextView(this);
            movieWatchedOn.setId(R.id.movieWatchedOn);
            movieWatchedOn.setText(m.getWatchedOn());

            final TextView movieCity = new TextView(this);
            movieCity.setId(R.id.movieCity);
            movieCity.setText(m.getCity());

            final ImageButton deleteButton = new ImageButton(this);
            deleteButton.setId(R.id.deleteButton);
            deleteButton.setImageResource(R.mipmap.ic_delete_white_24dp);
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            RelativeLayout.LayoutParams btnLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            btnLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            btnLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            btnLP.addRule(RelativeLayout.ALIGN_PARENT_END);
            deleteButton.setLayoutParams(btnLP);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MovieListActivity.this);
                    alert.setTitle(Html.fromHtml("<font color='#000000'>" + getString(R.string.titleDialog) + "</font>"));
                    Resources res = getResources();
                    String text = String.format(res.getString(R.string.messageDialog), movieTitle.getText().toString());

                    alert.setMessage(text);
                    alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (db != null) {
                                db.delete("movie", "title = ?", new String[]{movieTitle.getText().toString()});
                            }
                            View parent = (View) deleteButton.getParent().getParent();
                            parent.setVisibility(View.GONE);
                        }
                    });
                    alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                }
            });

            TextView[] textViews = new TextView[]{movieTitle, movieDirector, movieActors, movieWatchedOn, movieCity};
            Integer[] textViewIds = new Integer[]{movieTitle.getId(), movieDirector.getId(), movieActors.getId(), movieWatchedOn.getId(), movieCity.getId()};

            rl.addView(movieImage);

            carrierRL.addView(movieTitle);
            for (int i = 0; i < textViews.length - 1; i++) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, textViewIds[i]);
                carrierRL.addView(textViews[i + 1], layoutParams);
            }
            carrierRL.addView(deleteButton);
            rl.addView(carrierRL);
            movieListLinearLayout.addView(rl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_addMovie) {
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), AddMovieActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
