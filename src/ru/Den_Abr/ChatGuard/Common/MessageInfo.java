package ru.Den_Abr.ChatGuard.Common;

import java.util.ArrayList;
import java.util.List;

import ru.Den_Abr.ChatGuard.Common.Player.CGPlayer;

public class MessageInfo {

	private String originalMessage;
	private String clearMessage;
	private CGPlayer player;
	private List<Violation> violations = new ArrayList<>();
	private boolean cancel = false;

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

	public List<Violation> getViolations() {
		return violations;
	}

	public void cancel(boolean arg) {
		cancel = arg;
	}

	public boolean isCancelled() {
		return cancel;
	}
}
