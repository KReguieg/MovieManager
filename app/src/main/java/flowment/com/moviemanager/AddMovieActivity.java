package flowment.com.moviemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class AddMovieActivity extends AppCompatActivity {

    public Button submitMovieButton;
    public EditText movieTitle;
    public EditText movieDirector;
    public EditText movieActors;
    public EditText movieWatchedOn;
    public EditText movieCity;
    MovieSQLiteHelper movieSQLiteHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        movieSQLiteHelper = new MovieSQLiteHelper(AddMovieActivity.this, "DBMovie", null, 1);

        submitMovieButton = (Button) findViewById(R.id.submitWatchedMovieButton);
        movieTitle = (EditText) findViewById(R.id.movieTitleEditText);
        movieDirector = (EditText) findViewById(R.id.movieDirectorEditText);
        movieActors = (EditText) findViewById(R.id.movieActorsEditText);
        movieWatchedOn = (EditText) findViewById(R.id.movieWatchedOnEditText);
        movieCity = (EditText) findViewById(R.id.movieCityEditText);

        submitMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = movieTitle.getText().toString();
                String director = movieDirector.getText().toString();
                String actors = movieActors.getText().toString();
                String watchedOn = movieWatchedOn.getText().toString();
                String city = movieCity.getText().toString();

                db = movieSQLiteHelper.getWritableDatabase();

                if (db != null) {
                    // preparing content values
                    ContentValues newRecord = new ContentValues();
                    newRecord.put("title", title);
                    newRecord.put("director", director);
                    newRecord.put("actors", actors);
                    newRecord.put("date", watchedOn);
                    newRecord.put("city", city);
                    // Insert records
                    db.insert("movie", null, newRecord);
                    db.close();
                }
                makeText(AddMovieActivity.this, "The Movie \"" + title + "\" has been added.", LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), MovieListActivity.class);
                intent.putExtra("activity", "second");
                startActivity(intent);
            }
        });
    }
}
