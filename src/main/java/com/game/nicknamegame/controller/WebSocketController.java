package com.game.nicknamegame.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.catalina.core.ApplicationContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.game.nicknamegame.customenum.MessageType;
import com.game.nicknamegame.service.WebUnitService;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@ServerEndpoint(value = "/connect")
public class WebSocketController {
	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<>());

	private static WebUnitService service;
	private JSONParser parser = new JSONParser();

	@Autowired
	public void setWebUnitService(WebUnitService service) {
		WebSocketController.service = service;
	}
	
	/**
	 * 클라이언트 측에서 메시지를 전송했을 때 수신하는 핸들러 메소드
	 * @param msg 클라이언트 측에서 송신한 메시지
	 * @param session 메시지를 송신한 클라이언트의 Session 
	 * @throws Exception
	 */
	@OnMessage
	public void OnMesage(String msg, Session session) throws Exception {

		JSONObject data = null;
		try {
			Object obj = parser.parse(msg);
			data = (JSONObject) obj;
		} catch (Exception e) {
			// e.printStackTrace();
			log.warn("잘못된 형식 입니다. session : {}, error msg : {}", session.getId(), e.getMessage());
		}

		if (data == null)
			return;
		if (!data.containsKey("token")) {
			log.warn("부적절한 접근입니다.");
			return;
		}
		if (!data.get("token").equals("0niyaNicknameGame")) {
			log.warn("잘못된 토큰입니다.");
			return;
		}
		MessageType type = MessageType.valueOf(data.get("type").toString());

		switch (type) {
			case CONNECT:
				service.connectingObserver(session, data.get("msg").toString());
				sendMsg(session, "CONNECT", "success");
				break;
			case PERMIT:
				break;
			case END:
				break;
		}
	}
	
	/**
	 * 전달 받은 세션에게 메시지를 전송하는 메소드
	 * @param s 메시지를 전송할세션
	 * @param type 메시지 타입
	 * @param msg 전송할 메시지
	 */
	public void sendMsg(Session s,String type, String msg) {
		try {
			String data = 
					"{"
					+ "\"token\":\"0niyaNicknameGame\","
					+ "\"type\":\""+type+"\","
					+ "\"msg\":\""+msg+"\"}";
			s.getBasicRemote().sendText(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 클라이언트 접속시 실행되는 핸들러 메소드
	 * @param s 접속하는 Session
	 * 
	 */
	@OnOpen
	public void OnOpen(Session s) {
		log.info("openSession : {}", s.toString());
		if (!clients.contains(s)) {
			clients.add(s);
		} else {
			log.info("이미 연결된 세션입니다.");
		}
	}
	
	/**
	 * 클라이언트의 연결이 끊겼을 때 실행되는 핸들러 메소드
	 * @param s 연결이 끊긴 Session
	 */
	@OnClose
	public void onClose(Session s) {

	}

}
