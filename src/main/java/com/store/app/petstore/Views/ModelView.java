package com.store.app.petstore.Views;

public class ModelView {
    private static ModelView model;

    private final StaffFactory viewFactory;
    private final AdminFactory adminFactory;
    private final UtilsFactory utilsFactory;

    public ModelView() {
        viewFactory = new StaffFactory();
        adminFactory = new AdminFactory();
        utilsFactory = new UtilsFactory();
    }

    public static synchronized ModelView getInstance() {
        if(model == null) {
            model = new ModelView();
        }
        return model;
    }

    public StaffFactory getViewFactory() {
        return viewFactory;
    }

    public AdminFactory getAdminFactory() {
        return adminFactory;
    }

    public UtilsFactory getUtilsFactory() {
        return utilsFactory;
    }
}
