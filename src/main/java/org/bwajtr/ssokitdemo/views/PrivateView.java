package org.bwajtr.ssokitdemo.views;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import jakarta.annotation.security.PermitAll;
import java.util.Optional;

/**
 * This view is not accessible to public. You have to log in using the configured OAUTH provider.
 * Please see application.properties for SSO Kit configuration.
 */
@PageTitle("Private view")
@Route(value = "private", layout = MainLayout.class)
@PermitAll
public class PrivateView extends VerticalLayout {

    public PrivateView(AuthenticationContext authenticationContext) {
        add(new H3("This page is accessible to only logged in users"));
        add(new Span("It also showcases how to obtain the data about the logged in user from the AuthenticationContext:"));

        final Optional<OidcUser> authenticatedUser = authenticationContext.getAuthenticatedUser(OidcUser.class);
        if (authenticatedUser.isPresent()) {
            OidcUser user = authenticatedUser.get();
            add(new Span("User name: " + user.getFullName()));
            add(new Span("User email: " + user.getEmail()));
        } else {
            add(new Span("No user found in AuthenticationContext!"));
        }
    }
}
