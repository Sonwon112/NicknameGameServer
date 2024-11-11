package com.game.nicknamegame.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.game.nicknamegame.model.WebUnit;

import jakarta.websocket.Session;

@Repository
public class WebUnitRepository {
	
	Map<String, WebUnit> webUnitMap = new HashMap<>();
	
	/**
	 * WebUnit을 생성하고 Map 키로는 Seesion Id, 값으로는 WebUnit을 저장
	 * @param url 채팅창 Url
	 * @param session 접속한 클라이언트의 Session
	 */
	public void CreateWebUnit(String url, Session session) {
		WebUnit unit = new WebUnit(url);
		webUnitMap.put(session.getId(), unit);
	}
}
