package com.game.nicknamegame.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.nicknamegame.customenum.MessageType;
import com.game.nicknamegame.model.DTO;
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
	private ObjectMapper mapper = new ObjectMapper();

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

		DTO data = null;
		try {
			
			data = mapper.readValue(msg, DTO.class);
		} catch (Exception e) {
			// e.printStackTrace();
			log.warn("잘못된 형식 입니다. session : {}, error msg : {}", session.getId(), e.getMessage());
		}

		if (data == null)
			return;
		if (data.getToken() == null || data.getToken().equals("")) {
			log.warn("부적절한 접근입니다.");
			return;
		}
		if (!data.getToken().equals("0niyaNicknameGame")) {
			log.warn("잘못된 토큰입니다.");
			return;
		}
		MessageType type = MessageType.valueOf(data.getType().toString());

		switch (type) {
			case CONNECT:
				service.connectingObserver(session, data.getMsg().toString());
				service.sendMsg(session, "CONNECT", "success");
				break;
			case PERMIT:
				if(data.getMsg().equals("permit")) {
					service.permitPart(session);
				}else if(data.getMsg().equals("stop")) {
					service.stopPart(session);
				}
				break;
			case END:
				
				break;
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
		// 세션 종료시 WebUnit 삭제
	}

}
