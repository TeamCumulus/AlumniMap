package crawler;

//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;

import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

public class Facebook extends Friend{
	
	private String email;
	private String password;
	private HtmlPage hmPage;
	private boolean isLogin;
	
	 private final WebClient webClient  = new WebClient();
	
	public Facebook(String email, String password) {
		super();
		this.email = email;
		this.password = password;
		this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	//	this.login();
	}
	
	public Facebook() {

	}
	
	public boolean isLogin() {
		return isLogin;
	}

	public void login(){
		try {
			HtmlPage loginPage = this.webClient.getPage("http://www.facebook.com");
			HtmlForm loginForm = loginPage.getForms().get(0);
			HtmlSubmitInput button = (HtmlSubmitInput)loginForm.getInputByValue("Log In");
			HtmlTextInput email = loginForm.getInputByName("email");
			HtmlPasswordInput pass = loginForm.getInputByName("pass");
			email.setValueAttribute(this.email);
			pass.setValueAttribute(this.password);
			hmPage = button.click();
			HtmlAnchor profile = (HtmlAnchor)hmPage.getHtmlElementByAccessKey('2');
			this.setHomePage(profile.getHrefAttribute());
			this.setName(profile.asText());
			this.isLogin = true;
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
			HtmlSubmitInput button = (HtmlSubmitInput)logoutForm.getInputByValue("Log Out");
			button.click();
			this.isLogin = false;
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	
//	public static void main(String[] args) throws Exception{
//		Facebook hanFB = new Facebook();
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
