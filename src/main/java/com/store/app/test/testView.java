package com.store.app.test;
import com.store.app.petstore.Views.ModelView;
//import org.testng.annotations.Test;

public class testView {
//    @org.testng.annotations.Test

    public void showPage(){
        ModelView.getInstance().getViewFactory().showWindow("order");
    }
}
