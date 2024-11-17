package com.game.nicknamegame.repository;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepository {
	
	private Set<String> participantSet = new HashSet<String>();
	
	/**
	 * 참여 리스트에 추가 실패시 false 성공시 true
	 * @param nickname 추가하고자하는 닉네임
	 * @return
	 */
	public boolean AppendParticipant(String nickname) {
		if(!participantSet.contains(nickname)) {
			participantSet.add(nickname);
			return true;
		}
		return false;
	}
	
	/**
	 * 참여 마감 후 맵을 열 경우 참여자 Set을 비우는 용도
	 */
	public void clearParticipant() {
		if(participantSet.isEmpty())return;
		participantSet.clear();
	}
	
	
	
}
