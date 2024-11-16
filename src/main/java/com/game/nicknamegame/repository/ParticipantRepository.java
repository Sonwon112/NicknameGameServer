package com.game.nicknamegame.repository;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepository {
	
	private Set<String> participantSet = new HashSet<String>();
	
	public boolean AppendParticipant(String nickname) {
		if(!participantSet.contains(nickname)) {
			participantSet.add(nickname);
			return true;
		}
		return false;
	}
	
	
	
}
