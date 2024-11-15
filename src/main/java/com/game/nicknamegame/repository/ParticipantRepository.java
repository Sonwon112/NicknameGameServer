package com.game.nicknamegame.repository;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.game.nicknamegame.model.Participant;

@Repository
public class ParticipantRepository {
	
	private Set<Participant> participantSet = new HashSet<Participant>();
	
	public void AppendParticipant() {
		
	}
	
	public boolean ContainParticipant() {
		
		return false;
	}
	
	
}
