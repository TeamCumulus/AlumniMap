package com.cumulus.collection;

//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class SeedUser extends User {

    private String email;
    private String password;
    private HtmlPage hmPage;
    private boolean isLogin;

    private final WebClient webClient = new WebClient();

    public SeedUser(String email, String password) {
        super();
        this.email = email;
        this.password = password;
        this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //	this.login();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void login() {
        try {
            HtmlForm formLogin = ((HtmlPage)webClient.getPage("http://www.facebook.com")).getForms().get(0);
            HtmlSubmitInput button = formLogin.getInputByValue("Log In");
            HtmlTextInput email = formLogin.getInputByName("email");
            HtmlPasswordInput pass = formLogin.getInputByName("pass");
            email.setValueAttribute(this.email);
            pass.setValueAttribute(this.password);
            hmPage = button.click();
            HtmlAnchor profile = (HtmlAnchor) hmPage.getHtmlElementByAccessKey('2');
            this.setName(profile.asText());
            this.setHomepage(profile.getHrefAttribute());
            this.isLogin = true;
            System.out.println(this+" successfully logged in");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public HtmlPage getHmPage() {
        return hmPage;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void logout() {
        try {
            HtmlForm logoutForm = hmPage.getForms().get(1);
            System.out.println(logoutForm.asText());
            HtmlSubmitInput button = logoutForm.getInputByValue("Log Out");
            button.click();
            this.isLogin = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


//	public static void main(String[] args) throws Exception{
//		SeedUser hanFB = new SeedUser();
//		hanFB.login();
//		HtmlPage friendsPage = hanFB.webClient.getPage("http://www.facebook.com/yahui.han.7/friends?ft_ref=mni");
//		ArrayList<DomNode> list = new ArrayList<DomNode>();
//		list = (ArrayList<DomNode>)friendsPage.getByXPath("//div[@class='fsl fwb fcb']");
//		for (DomNode d : list){
//			//System.out.println(d.asXml());
//			HtmlAnchor friendAnchor = (HtmlAnchor) d.getByXPath("./a").get(0);
//			System.out.println(friendAnchor.asText() + " " +friendAnchor.getHrefAttribute());
//		}
//		hanFB.logout();	
//	}
}
