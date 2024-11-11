package com.game.nicknamegame.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.game.nicknamegame.model.WebUnit;

import jakarta.websocket.Session;

@Repository
public class WebUnitRepository {
	
	Map<String, WebUnit> webUnitMap = new HashMap<>();
	
	public void CreateWebUnit(String url, Session session) {
		WebUnit unit = new WebUnit(url);
		webUnitMap.put(session.getId(), unit);
	}
}
