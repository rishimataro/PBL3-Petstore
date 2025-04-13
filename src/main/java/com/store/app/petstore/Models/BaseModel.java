package com.store.app.petstore.Models;

import com.store.app.petstore.Views.ViewFactory;

public class Model {
    private static Model model;
    private ViewFactory viewFactory;

    public Model() {
        viewFactory = new ViewFactory();
    }

    public static synchronized Model getInstance() {
        if(model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
