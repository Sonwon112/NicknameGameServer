package com.game.nicknamegame.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.nicknamegame.customenum.MessageType;
import com.game.nicknamegame.model.DTO;
import com.game.nicknamegame.model.WebUnit;
import com.game.nicknamegame.service.WebUnitService;

import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WebUnitRepository {
	
	
	private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private Map<String, WebUnit> webUnitMap = new HashMap<>();
	private Map<String, Session> sessionMap = new HashMap<>();

	private ObjectMapper mapper = new ObjectMapper();
	
	@Value("${cookie}")
	private String cookie;

	/**
	 * WebUnit을 생성하고 Map 키로는 Seesion Id, 값으로는 WebUnit을 저장
	 * 
	 * @param url     채팅창 Url
	 * @param session 접속한 클라이언트의 Session
	 */
	public void CreateWebUnit(String url, Session session, WebUnitService service) {
		WebUnit unit = new WebUnit(url, service, session.getId(),cookie);
		webUnitMap.put(session.getId(), unit);
		sessionMap.put(session.getId(), session);
	}

	/**
	 * 참여를 허용하는 함수
	 * @param session 참여를 허용할 세션
	 */
	public void permitPart(Session session) {
		if (!webUnitMap.containsKey(session.getId())) {
			log.warn("없는 세션입니다.");
			return;
		}
		WebUnit tmp = webUnitMap.get(session.getId());
		tmp.startCheck();
	}

	/**
	 * 참여를 차단하는 함수
	 * @param session 참여를 차단할 세션
	 */
	public void stopPart(Session session) {
		if (!webUnitMap.containsKey(session.getId())) {
			log.warn("없는 세션입니다.");
			return;
		}
		WebUnit tmp = webUnitMap.get(session.getId());
		tmp.stopCheck();
	}

	/**
	 * 전달 받은 세션에게 메시지를 전송하는 메소드
	 * 
	 * @param s    메시지를 전송할세션
	 * @param type 메시지 타입
	 * @param msg  전송할 메시지
	 */
	public void sendMsg(Session s, String type, String msg) {
		try {
			DTO dto = new DTO("0niyaNicknameGame", type, msg);
			String dataToJson = mapper.writeValueAsString(dto);
			s.getBasicRemote().sendText(dataToJson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 전달 받은 세션에게 메시지를 전송하는 메소드
	 * 
	 * @param sessionId 메시지를 전송할세션의 아이디
	 * @param type      메시지 타입
	 * @param msg       전송할 메시지
	 */
	public void sendMsg(String sessionId, String type, String msg) {
		if (!sessionMap.containsKey(sessionId)) {
			log.error("세션이 존재하지 않습니다.");
			return;
		}
		
		Runnable sendRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					DTO dto = new DTO("0niyaNicknameGame", type, msg);
					String dataToJson = mapper.writeValueAsString(dto);
					Session s = sessionMap.get(sessionId);
					s.getBasicRemote().sendText(dataToJson);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		pool.submit(sendRunnable);
	}
	
	/**
	 * 세션 종료 메소드
	 * @param sessionId 종료할 세션 id
	 */
	public void closeSession(String sessionId) {
		try {
			if(sessionMap.isEmpty())return;
			if(sessionMap.get(sessionId).isOpen()) {
				sendMsg(sessionMap.get(sessionId), MessageType.END.toString(), "connect Fail");
				sessionMap.get(sessionId).close();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webUnitMap.remove(sessionId);
		sessionMap.remove(sessionId);
	}
}
