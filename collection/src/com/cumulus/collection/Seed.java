package com.cumulus.collection;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class Seed {
    private String name;
    private String homepage;
    private String email;
    private String password;

    private HtmlPage pageMain;

    public Seed(String email, String password) {
        super();
        this.email = email;
        this.password = password;
//        this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

    public void login(WebClient client) {
        try {
            HtmlForm formLogin = ((HtmlPage)client.getPage("http://www.facebook.com")).getForms().get(0);
            formLogin.getInputByName("email").setValueAttribute(email);
            formLogin.getInputByName("pass").setValueAttribute(password);
            pageMain = formLogin.getInputByValue("Log In").click();
            HtmlAnchor profile = (HtmlAnchor) pageMain.getHtmlElementByAccessKey('2');
            name = profile.asText();
            homepage = profile.getHrefAttribute();
            System.out.println(name + " successfully logged in");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout() {
        try {
//            System.out.println(formLogout.asText());
            // TODO index 1 size 1????
            pageMain.getFormByName("logout_form").getInputByValue("Log Out").click();  // log out form
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public String getHomepage() {
        return homepage;
    }

}
