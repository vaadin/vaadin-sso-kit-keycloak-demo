package org.bwajtr.ssokitdemo.views;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Public view")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class PublicView extends VerticalLayout {

    public PublicView() {
        add(new H3("This page is accessible to anyone"));
        add(new Span("The 'Private view' is accessible only to logged in user"));
    }

}
