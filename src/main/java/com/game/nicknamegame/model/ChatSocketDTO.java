package com.game.nicknamegame.model;

import lombok.Getter;
import lombok.Setter;


public class ChatSocketDTO {
	
	public static FirstSend fs = new FirstSend();
	public static SecondSend ss = new SecondSend();
	public static RepeatSend rs = new RepeatSend();
	
	public static void setCid(String cid) {
		fs.setCid(cid);
		ss.setCid(cid);
	}
	
	public static void setAccTkn(String acctkn) {
		fs.setAccTkn(acctkn);
	}
	
	
	@Getter @Setter
	private static class FirstSend{
		private String ver = "3";
		private int cmd = 100;
		private String svcid = "game";
		private String cid;
		private firstBdy bdy = new firstBdy();
		private int tid = 1;
		
		public void setAccTkn(String acctkn) {
			bdy.setAccTkn(acctkn);
		}
	}
	
	@Getter @Setter
	private static class firstBdy{
			    private String uid = "b55ee839d92e120d65ed89eed5b510c6";
			    private int devType = 2001;
			    private String accTkn;
			    private String auth = "SEND";
			    private String libVer = "4.9.3";
			    private String osVer = "Windows/10";
			    private String devName = "Google Chrome/131.0.0.0";
			    private String locale = "ko";
			    private String timezone ="Asia/Seoul";
	}
	
	@Getter @Setter
	private static class SecondSend{
		private String ver = "3";
		private int cmd = 5001;
		private String svcid = "game";
		private String cid;
		private String sid = "cacg3!hCJIQ8Uq2dPHbTvdu_1_19kFv3sTk_G1fKMwW1mCMlu8UKRYpJHvY!4vkiWzViohat2Ni1XAkGY0fIsQ--";
		private SecondBdy bdy = new SecondBdy();
		private int tid = 2;
	}
	
	@Getter @Setter
	private static class SecondBdy{
		private int recentMessageCount = 50;
	}
	
	@Getter @Setter
	private static class RepeatSend{
		private String ver = "3";
		private int cmd = 0;
	}
}
