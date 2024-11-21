package com.game.nicknamegame.repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepository {
	
	
	private Map<String,Set<String>> participantMap = new HashMap<>();
	/**
	 * 참여 리스트에 추가 실패시 false 성공시 true
	 * @param nickname 추가하고자하는 닉네임
	 * @return
	 */
	public boolean AppendParticipant(String sessionId, String nickname) {
		if(participantMap.containsKey(sessionId)) {
			Set<String> participantSet = participantMap.get(sessionId);
			if(!participantSet.contains(nickname)) {
				participantSet.add(nickname);
				return true;
			}
		}else {
			Set<String> tmp = new HashSet<>();
			tmp.add(nickname);
			participantMap.put(sessionId, tmp);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 참여 마감 후 맵을 열 경우 참여자 Set을 비우는 용도
	 */
	public void clearParticipant(String sessionId) {
		if(participantMap.isEmpty())return;
		Set<String> tmp = participantMap.get(sessionId);
		if(tmp.isEmpty())return;
		tmp.clear();;
	}
	
	
	
}
