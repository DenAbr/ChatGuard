package ru.Den_Abr.ChatGuard;

import java.util.ArrayList;
import java.util.List;

import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class ViolationInfo {

	private String originalMessage;
	private String clearMessage;
	private CGPlayer player;
	private List<ViolationType> violations = new ArrayList<>();

	public String getOriginalMessage() {
		return originalMessage;
	}

	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = originalMessage;
	}

	public String getClearMessage() {
		return clearMessage;
	}

	public void setClearMessage(String clearMessage) {
		this.clearMessage = clearMessage;
	}

	public CGPlayer getPlayer() {
		return player;
	}

	public void setPlayer(CGPlayer player) {
		this.player = player;
	}

	public List<ViolationType> getViolations() {
		return violations;
	}
}
