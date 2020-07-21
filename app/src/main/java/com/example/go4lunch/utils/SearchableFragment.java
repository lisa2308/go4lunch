package com.example.go4lunch.utils;

import com.google.android.libraries.places.api.model.Place;

//définir le comportement de la recherche par rapport au fragment dans lequel je suis

public interface SearchableFragment {

    void performSearch(Place place);
}
