package com.game.nicknamegame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.nicknamegame.repository.ParticipantRepository;

@Service
public class ParticipantService {
	
	@Autowired
	ParticipantRepository repo;
	
	/**
	 * 참여 리스트에 추가 실패시 false 성공시 true
	 * @param nickname 추가하고자하는 닉네임
	 * @return
	 */
	public boolean AppendParticipant(String nickname) {
		return repo.AppendParticipant(nickname);
	}
	
	/**
	 * 참여 마감 후 맵을 열 경우 참여자 Set을 비우는 용도
	 */
	public void clearParticipant() {
		repo.clearParticipant();
	}
}
