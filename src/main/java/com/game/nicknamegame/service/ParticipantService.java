package com.game.nicknamegame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.nicknamegame.repository.ParticipantRepository;

@Service
public class ParticipantService {
	
	@Autowired
	ParticipantRepository repo;
	
	public boolean AppendParticipant(String nickname) {
		return repo.AppendParticipant(nickname);
	}
}
