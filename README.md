# Vaadin SSO Kit Keycloak demo

This project showcases a minimal setup of [Keycloak](https://www.keycloak.org/), so you can use it together with Vaadin SSO Kit as an SSO identity manager in the Vaadin
application. Please take into account that the tutorial was created in January 2023, and the involved technologies may have changed since then. Especially the
screenshots do not have to be 100% accurate anymore.
 
* SSO Kit documentation: https://vaadin.com/docs/latest/tools/sso
* Keycloak documentation: https://www.keycloak.org/documentation

The demo consists of two views:

* Public view, which is accessible without login and which is mapped to http://localhost:8080/  
  [<img height="100px" src="tutorial/public.png?raw=true"/>](tutorial/public.png?raw=true)
* Private view, which is protected by @PermitAll and requires you to log in. This view is accessible at http://localhost:8080/private  
  [<img height="100px" src="tutorial/private.png?raw=true"/>](tutorial/private.png?raw=true)

You should be redirected to the configured Oauth Provider Login page when:
* you either attempt to enter the Private view, 
* or when you want to explicitly log in using the button in the lower-left corner of the screen

You should be able to log out using the user dropdown button in the lower-left corner of the screen.
You should also be able to use the backchannel logout functionality. See the "Testing backchannel logout" chapter below.

## Keycloak setup

To run the demo, you have to run and configure Keycloak first. In this tutorial, we will do the following:

- run Keycloak locally using Docker
- create a new realm in it
- create at least one user in this new realm
- create an OIDC client configuration so our application can use its details for login
                                                             
Note that the documentation is based on this Keycloak "Getting started" documentation:
https://github.com/keycloak/keycloak-quickstarts/blob/latest/docs/getting-started.md

Let's jump in:
1. Run Keycloak using `docker run --name keycloak-ssokit -p 8280:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:20.0.3 start-dev`
2. Go to http://localhost:8280 to open Keycloak UI  
   [<img height="100px" src="tutorial/keycloak.png?raw=true"/>](tutorial/keycloak.png?raw=true) 
3. Click on Administration Console and login to it using admin/admin
4. Create a new realm and name it `ssokitrealm`  
   [<img height="100px" src="tutorial/realmcombo.png?raw=true"/>](tutorial/realmcombo.png?raw=true)
   [<img height="100px" src="tutorial/newrealm.png?raw=true"/>](tutorial/newrealm.png?raw=true)
5. Go to Users and create a new User. We will use this user to test the login functionality in the demo  
   [<img height="100px" src="tutorial/newuserstart.png?raw=true"/>](tutorial/newuserstart.png?raw=true)
   [<img height="100px" src="tutorial/newuser.png?raw=true"/>](tutorial/newuser.png?raw=true)
6. Go to user Credentials to create a new password for the user     
   [<img height="100px" src="tutorial/credentialstab.png?raw=true"/>](tutorial/credentialstab.png?raw=true)
   [<img height="100px" src="tutorial/setpassword.png?raw=true"/>](tutorial/setpassword.png?raw=true)
7. Once you set the password, you should see in the credentials:  
   [<img height="100px" src="tutorial/passwordcreated.png?raw=true"/>](tutorial/passwordcreated.png?raw=true)
8. Go to "Clients" section in the left menu and create a new client:  
   [<img height="100px" src="tutorial/newclientstart.png?raw=true"/>](tutorial/newclientstart.png?raw=true)
9. Use the following values in the client creation wizard:
   * Client type: OpenID Connect
   * Client ID: sso-kit-sample
   * Name: sso-kit-sample
   * Client authentication: On
   * Authorization: On
   * Authentication flow: Check only "Standard flow" (option "Service account roles" is checked and disabled)  
   [<img height="100px" src="tutorial/generalsettings.png?raw=true"/>](tutorial/generalsettings.png?raw=true)
   [<img height="100px" src="tutorial/capabilityconfig.png?raw=true"/>](tutorial/capabilityconfig.png?raw=true)
10. Once you create the client, you are redirected to the Client Details  
   [<img height="100px" src="tutorial/clientdetails.png?raw=true"/>](tutorial/clientdetails.png?raw=true)
11. Scroll down and set the following values:
    * Root URL: http://localhost:8080
    * Home URL: /
    * Valid redirect URIs: http://localhost:8080/login/oauth2/code/keycloak
    * Valid post logout redirect URIs: http://localhost:8080
    * Web origins: +    
      [<img height="100px" src="tutorial/accesssettings.png?raw=true"/>](tutorial/accesssettings.png?raw=true)
12. Scroll even lower on the same page to set the Logout settings:
    * Front channel logout: Off (if you leave it enabled, the backchannel logout won't work)
    * Backchannel logout URL: http://192.168.2.158:8080/logout/back-channel/keycloak  (replace `192.168.2.158` with the IP of your computer, do not use `localhost` here)
    * Backchannel logout session required: Off (backchannel logout won't work when left on On)  
    [<img height="100px" src="tutorial/backchannel.png?raw=true"/>](tutorial/backchannel.png?raw=true)
13. Do not forget to Save changes to your client
14. Scroll all the way up and go to client Credentials, copy the Client secret there and paste it to application.properties  
    [<img height="100px" src="tutorial/ccredentialstab.png?raw=true"/>](tutorial/ccredentialstab.png?raw=true)
    [<img height="100px" src="tutorial/csecret.png?raw=true"/>](tutorial/csecret.png?raw=true)
15. That's it! Your instance of Keycloak is ready to be used

## Vaadin application setup

You must modify the application.properties and only change the client-secret value:    

```properties
spring.security.oauth2.client.registration.keycloak.client-secret=[client secret can be found in Keycloak client details]
```

Keep the other configuration settings as they are:
```properties
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8280/realms/ssokitrealm
spring.security.oauth2.client.registration.keycloak.client-id=sso-kit-sample
spring.security.oauth2.client.registration.keycloak.scope=profile,openid,email,roles
vaadin.sso.login-route=/oauth2/authorization/keycloak
vaadin.sso.back-channel-logout=true
```

## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different
IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Testing backchannel logout

1. Open the app at http://localhost:8080 and login using the "Sign in" button in the lower-left corner:  
   [<img height="100px" src="tutorial/signin.png?raw=true"/>](tutorial/signin.png?raw=true)
2. After login, you now have an authenticated session, and you are allowed to enter the "Private view"
3. Go to Keycloak admin, select `ssokitrealm`, go to Clients and click on the `sso-kit-sample` client
4. Scroll the tab bar to the right and select the Sessions tab:    
   [<img height="100px" src="tutorial/csessions.png?raw=true"/>](tutorial/csessions.png?raw=true)
5. There should be a session for `user` user listed, click on the three dots drop-down menu like in the picture above
6. Click on "Sign out"
7. This should cause the KeyCloak server to directly call the running demo server and perform backchannel logout of the given user
8. Go back to the demo and click, for example, on the "Public view" to open it
9. You might have noticed that the page has been reloaded -> that is because the backchannel logout expired the user session, and a new session was created with the click
10. You are now not authenticated, and when you try to go to Private view, you are asked to log in

