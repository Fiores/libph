package com.cab404.libtabun.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * @author cab404
 */
public class Topic implements Serializable {
    private static final long serialVersionUID = 0L;

    public String title, votes;
    public Profile author;
    public Blog blog;

    public String text;
    public List<String> tags;

    public Calendar date;
    public int id;

    public boolean vote_enabled = false;
    public boolean in_favourites = false;

    public int your_vote = 0;

    public int comments = 0, comments_new = 0;

    public Topic() {
        tags = new ArrayList<>();
        blog = new Blog();
        author = new Profile();
    }
}