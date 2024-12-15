package com.game.nicknamegame.model;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.nicknamegame.service.WebUnitService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebUnit {
	private static String accTknURL = "https://comm-api.game.naver.com/nng_main/v1/chats/access-token?channelId={chatChannelId}&chatType=STREAMING";
	
	
	private WebSocketClient client = new StandardWebSocketClient();
	private String chatChannelId;	
	
	private final static String wssURL = "wss://kr-ss2.chat.naver.com/chat";
	private ObjectMapper mapper = new ObjectMapper();
	private WebSocketClientHandler handler;
	private HttpHeaders headers = new HttpHeaders();
	
	public WebUnit(String url, WebUnitService service, String sessionId, String cookie) {
		getChatChannelId(url, cookie);
		getAccTkn();
		handler = new WebSocketClientHandler(mapper, service, sessionId);
		try {
			WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
			client.execute(handler, headers, URI.create(wssURL));
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}		
	}
	
	/**
	 * 치지직 api로 부터 chatChannelId를 획득하기 위한 함수
	 * @param url api 주소
	 * @param cookie 로그인이 되어있어야하기 때문에 쿠키 필요
	 */
	public void getChatChannelId(String url, String cookie) {
		headers.set("Cookie", cookie);
		
		HttpEntity request = new HttpEntity(headers);
		ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET,request,String.class);
		
		try {
			JsonNode jsonNode = mapper.readValue(response.getBody(), JsonNode.class);
			if (jsonNode.has("content")) {
				chatChannelId = jsonNode.get("content").get("chatChannelId").asText();
				ChatSocketDTO.setCid(chatChannelId);
				log.info("success get chatChannelId : " + chatChannelId);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.info("액세스 실패 : "+e.getMessage());
		} 
	}
	
	/**
	 * 채팅 websocket 접속을 위해 accessToken을 획득하는 메소드
	 */
	public void getAccTkn() {
		accTknURL = accTknURL.replace("{chatChannelId}", chatChannelId);

		try {
			HttpEntity request = new HttpEntity(headers);
			ResponseEntity<String> response = new RestTemplate().exchange(accTknURL, HttpMethod.GET,request,String.class);
			JsonNode jsonNode = mapper.readValue(response.getBody(), JsonNode.class);
			if(jsonNode.has("content")) {
				String accTkn = jsonNode.get("content").get("accessToken").asText();
				ChatSocketDTO.setAccTkn(accTkn);
				log.info("success get AccessToken");
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 참가 허용
	 */
	public void startCheck() {
		handler.startCheck();
	}
	
	/**
	 * 참가 종료
	 */
	public void stopCheck() {
		handler.stopCheck();
	}
	
}


@Slf4j
@Getter @Setter
class WebSocketClientHandler extends TextWebSocketHandler{
	private ObjectMapper mapper;
	private WebUnitService service;
	private String ClientSessionId;
	
	private WebSocketSession session;
	private boolean isConnect = false;
	private boolean isPermit = false;
	private Thread repeatThread;
	
	public WebSocketClientHandler(ObjectMapper mapper, WebUnitService service, String ClientSessionId) {
		this.mapper = mapper;
		this.service = service;
		this.ClientSessionId = ClientSessionId;
	}
	

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TODO Auto-generated method stub
		log.info("connecting chat ws Server..");
		String fsData = mapper.writeValueAsString(ChatSocketDTO.fs);
		session.sendMessage(new TextMessage(fsData));
		log.info("send fsData");
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// TODO Auto-generated method stub
//		super.afterConnectionClosed(session, status);
		log.info(session.getId() + " closed : " + status.getReason());
		isConnect = false;
		service.closeSession(ClientSessionId);
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TODO Auto-generated method stub
		JsonNode jsonNode = mapper.readValue(message.getPayload(), JsonNode.class);
		//log.info(""+message.getPayload());
		if(jsonNode.has("cmd")) {
			switch (jsonNode.get("cmd").asText()) {
			
			// ------------------ 연결 성공을 알리는 cmd
			case "10100": {
				if(!jsonNode.get("retMsg").asText().equals("SUCCESS")) break;
				log.info("connect successfully chat ws Server..");
				isConnect = true;
				repeatThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(isConnect) {
							try {
								String repeatData = mapper.writeValueAsString(ChatSocketDTO.rs);
								session.sendMessage(message);
								Thread.sleep(20000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
				repeatThread.start();
				break;
			}
			// ------------------ 새로 온 채팅을 보낸 cmd
			case "93101":{
				if(!isPermit)break;
				JsonNode userJsonNode = jsonNode.get("bdy");
				if(userJsonNode.isArray()) {
					for(JsonNode element : userJsonNode) {
						JsonNode profile = mapper.readValue(element.get("profile").asText(), JsonNode.class);
						String nickname = profile.has("nickname") ?  profile.get("nickname").asText() : "NonProfile";
						String msg = element.get("msg").asText();
//						log.info(nickname + " : " + msg);
						if(msg.equals("!참여")) {
							if(service.AppendParticipant(nickname, ClientSessionId))
								log.info(nickname+" is part in game");
						}
					}
				}
				break;
			}
			case "0":{
				// 변경 없이 connection 유지를 위한 통신
				break;
			}
			}
			
		}
		
		
	}
	
	/**
	 * 참여 허용
	 */
	public void startCheck() {
		if(isConnect) {
			isPermit = true;
			log.info("if user input \"!참여\" then they can part in game now");
		}
	}
	
	/**
	 * 참여 금지
	 */
	public void stopCheck() {
		if(isConnect) {
			isPermit = false;
			log.info("user can't part in game now");
		}
	}


	
	
}
