package com.store.app.petstore.Views;

public class ModelView {
    private static ModelView model;
    private final ViewFactory viewFactory;

    public ModelView() {
        viewFactory = new ViewFactory();
    }

    public static synchronized ModelView getInstance() {
        if(model == null) {
            model = new ModelView();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }
}
