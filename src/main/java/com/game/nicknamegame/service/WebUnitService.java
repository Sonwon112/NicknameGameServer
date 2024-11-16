package com.game.nicknamegame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.nicknamegame.customenum.MessageType;
import com.game.nicknamegame.repository.WebUnitRepository;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebUnitService {
	private static String WEB_DRIVER_ID="webdriver.chrome.driver";
	private static String WEB_DRIVER_PATH="C:/workspace/java library/chromedriver-win64/chromedriver.exe";
	private static String BASE_CHAT_URL = "https://chzzk.naver.com/live/";
	
	@Autowired
	WebUnitRepository repo;
	@Autowired
	ParticipantService partService;
	
	private Session session;
	
	/**
	 * 클라이언트에서 서버에 연결을 시도하고, 자신의 채널 id를 송신하였을 때 해당 ID에 맞게 WebUnit을 생성 
	 * @param session 접속한 클라이언트의 Session
	 * @param id 스트리머 본인의 채널 ID
	 */
	public void connectingObserver(Session session, String id) {
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		String chatUrl = BASE_CHAT_URL + id+ "/chat";
		repo.CreateWebUnit(chatUrl, session, this);
		log.info("WebUnit Create Success");
	}
	
	public void permitPart(Session session) {
		repo.permitPart(session);
	}
	
	public void AppendParticipant(String nickname, String sessionId) {
		if(partService.AppendParticipant(nickname)) {
			sendMsg(sessionId, MessageType.PERMIT.toString(), nickname);
		}
	}
	
	
	
	public void sendMsg(String sessionId,String type, String msg) {
		repo.sendMsg(sessionId,type, msg);
	}
	
	public void sendMsg(Session session,String type, String msg) {
		repo.sendMsg(session,type, msg);
	}
}
