package ru.chocco_crokko.musikant.models;

import java.util.ArrayList;
import java.util.List;

/*
 * ListArray<Artist> with getter
 */
public class BunchOfArtists
{
    private List<Artist> someArtists;

    public BunchOfArtists()
    {
        someArtists  = new ArrayList<>();
    }

    public List<Artist> getArtists()
    {
        return someArtists;
    }
}
