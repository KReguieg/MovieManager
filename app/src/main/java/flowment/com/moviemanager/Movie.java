package flowment.com.moviemanager;

import java.io.Serializable;

/**
 * Created by Khaled Reguieg <a href="mailto:Khaled.Reguieg@gmail.com">Khaled Reguieg</a> on 04.08.2015.
 * This class is a data holding class for movies.
 */
public class Movie implements Serializable {

    /**
     * This variable holds the title of a movie..
     */
    public String title;

    /**
     * This variable holds the director of a movie.
     */
    public String director;

    /**
     * This Array holds the actors of the movie.
     */
    public String actors;

    /**
     * This variable holds the date at which a movie was watched.
     */
    public String watchedOn;

    /**
     * This variable holds the city in which the movie was see.
     */
    public String city;

    /**
     * Standard Constructor for initializing a movie object.
     *
     * @param title     The title of the movie.
     * @param director  The director of the movie.
     * @param actors    The actors of the movie.
     * @param watchedOn The date the mocie was watched.
     * @param city      The city the movie was watched in.
     */
    public Movie(String title, String director, String actors, String watchedOn, String city) {
        this.title = title;
        this.director = director;
        this.actors = actors;
        this.watchedOn = watchedOn;
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getWatchedOn() {
        return watchedOn;
    }

    public void setWatchedOn(String watchedOn) {
        this.watchedOn = watchedOn;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", actors=" + actors + '\'' +
                ", watchedOn='" + watchedOn + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
