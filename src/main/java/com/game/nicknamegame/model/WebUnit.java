package com.game.nicknamegame.model;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebUnit {
	
	private WebDriver driver;
	private String url;
	
	public WebUnit(String url) {
		this.url = url;
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		openChrome();
	}
	
	public void openChrome() {
		try {
			driver.get(url);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void startCrawl() {
		
	}
	
	public void stopCrawl() {
		
	}
}

class CrawlNicknameTask extends Thread{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			
		}
	}	
}
