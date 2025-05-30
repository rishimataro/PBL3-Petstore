package com.store.app.petstore.Views;

public class ModelView {
    private static ModelView model;
    private final ViewFactory viewFactory;
    private final AdminFactory adminFactory;

    public ModelView() {
        viewFactory = new ViewFactory();
        adminFactory = new AdminFactory();
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

    public AdminFactory getAdminFactory() {
        return adminFactory;
    }
}
