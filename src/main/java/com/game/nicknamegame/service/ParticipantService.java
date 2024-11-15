package com.game.nicknamegame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.nicknamegame.repository.ParticipantRepository;

@Service
public class ParticipantService {
	
	@Autowired
	ParticipantRepository repo;
	
	public void AppendParticipant() {
		
	}
	
	public boolean ContainParticipant() {
		return repo.ContainParticipant();
	}
}
