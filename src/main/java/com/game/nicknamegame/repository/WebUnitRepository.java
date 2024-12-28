package com.game.nicknamegame.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
		WebUnit unit = new WebUnit(url, service, session.getId(), cookie);
		webUnitMap.put(session.getId(), unit);
		sessionMap.put(session.getId(), session);
	}

	/**
	 * 참여를 허용하는 함수
	 * 
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
	 * 
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
	 * 전달 받은 세션 id를 가진 세션에게 메시지를 전송하는 메소드
	 * @param sessionId 세션 ID
	 * @param type 메시지 타입
	 * @param msg 메시지
	 * @return 전송에 성공하였는지 판단
	 */
	public boolean sendMsg(String sessionId, String type, String msg) {
		boolean[] result = {true};
		
		if (!sessionMap.containsKey(sessionId)) {
			log.error("세션이 존재하지 않습니다.");
			return false;
		}
		
		// 빠르게 들어오는 트래픽 처리를 위해서 Thread 사용
		Thread thread = new Thread(() -> {
			try {
				DTO dto = new DTO("0niyaNicknameGame", type, msg);
				String dataToJson = mapper.writeValueAsString(dto);
				Session s = sessionMap.get(sessionId);
				s.getBasicRemote().sendText(dataToJson);
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage());
			} 
		});
		
		// 스레드에서 전송 실패시 false를 반환하기 위한 Exception Handler
		thread.setUncaughtExceptionHandler((t, e) -> {
			log.info(t.getName()+" : "+e.getMessage());
			result[0] = false;
		});

		thread.start();
		return result[0];
	}

	/**
	 * 세션 종료 메소드
	 * 
	 * @param sessionId 종료할 세션 id
	 */
	public void closeSession(String sessionId) {
		try {
			if (sessionMap.isEmpty())
				return;
			if (sessionMap.get(sessionId).isOpen()) {
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
