package org.bwajtr.ssokitdemo.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.sso.starter.SingleSignOnProperties;
import org.bwajtr.ssokitdemo.components.appnav.AppNav;
import org.bwajtr.ssokitdemo.components.appnav.AppNavItem;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private final AuthenticationContext authenticationContext;
    private final SingleSignOnProperties ssoProperties;

    public MainLayout(AuthenticationContext authenticationContext,
                      SingleSignOnProperties ssoProperties) {
        this.authenticationContext = authenticationContext;
        this.ssoProperties = ssoProperties;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("SSO Kit Sample");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        nav.addItem(new AppNavItem("Public view", PublicView.class, "la la-globe"));
        nav.addItem(new AppNavItem("Private view", PrivateView.class, "la la-cat"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        createUserAuthComponent(layout);
        return layout;
    }

    private void createUserAuthComponent(Footer layout) {
        Optional<OidcUser> maybeUser = authenticationContext.getAuthenticatedUser(OidcUser.class);
        if (maybeUser.isPresent()) {
            OidcUser user = maybeUser.get();

            Avatar avatar = new Avatar(user.getFullName());
            avatar.setThemeName("xsmall");
            avatar.setColorIndex(2);
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getFullName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> authenticationContext.logout());

            layout.add(userMenu);
        } else {
            var loginLink = new Button("Sign in");
            loginLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            loginLink.addClickListener(event -> event.getSource().getUI().ifPresent(ui -> ui.getPage().open(ssoProperties.getLoginRoute(), "_self")));
            layout.add(loginLink);
        }
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
