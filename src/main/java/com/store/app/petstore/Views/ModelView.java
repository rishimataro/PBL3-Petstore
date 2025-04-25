package com.store.app.petstore.Views;

public class ModelView {
    private static ModelView model;
    private final StaffView viewFactory;

    public ModelView() {
        viewFactory = new StaffView();
    }

    public static synchronized ModelView getInstance() {
        if(model == null) {
            model = new ModelView();
        }
        return model;
    }

    public StaffView getViewFactory() {
        return viewFactory;
    }
}
