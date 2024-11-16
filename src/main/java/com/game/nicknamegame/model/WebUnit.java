package com.game.nicknamegame.model;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.game.nicknamegame.service.WebUnitService;

import lombok.extern.slf4j.Slf4j;


public class WebUnit {
	
	private WebDriver driver;
	private String url;
	private CrawlNicknameTask crawlThread;
	
	
	public WebUnit(String url, WebUnitService service, String sessionId) {
		this.url = url;
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		openChrome();
		crawlThread = new CrawlNicknameTask(driver, service, sessionId);
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
		crawlThread.start();
	}
	
	public void stopCrawl() {
		crawlThread.interrupt();
	}
}

@Slf4j
class CrawlNicknameTask extends Thread{
	
	private WebDriver driver;
	private WebUnitService service;
	private String sessionId;
	
	private static final String WRAPPER = "wrapper live_chatting_message_wrapper__xpYre"; // 채팅 wrapper
	private static final String CHAT_TEXT = "live_chatting_message_text__DyleH"; // 채팅 내용
	private static final String NICKNAME = "name_text__yQG50"; // 닉네임
	
	public CrawlNicknameTask(WebDriver driver, WebUnitService service, String sessionId) {
		this.driver = driver;
		this.service = service;
		this.sessionId = sessionId;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true) {				
				List<WebElement> elements = driver.findElements(By.className(WRAPPER));
				for(WebElement e : elements) {
					WebElement chatText = e.findElement(By.className(CHAT_TEXT));
					if(!chatText.getText().equals("!참여")) continue;
					String nickname = e.findElement(By.className(NICKNAME)).getText();
					service.AppendParticipant(nickname, sessionId);
					Thread.sleep(100);
				}
			}
		}catch(InterruptedException e) {
			log.debug("stopThread");
		}
	}	
}
