package ru.Den_Abr.ChatGuard;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import ru.Den_Abr.ChatGuard.Player.CGPlayer;

public class MessageInfo {

    private String originalMessage;
    private String clearMessage;
    private CGPlayer player;
    private List<Violation> violations = new ArrayList<>();
    private boolean cancel = false;

    public MessageInfo(String originalMessage, CGPlayer player) {
        setOriginalMessage(originalMessage);
        setPlayer(player);
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        checkNotNull(originalMessage, "Original message cannot be null");
        this.originalMessage = originalMessage;
        if (this.clearMessage == null) {
            this.clearMessage = originalMessage;
        }
    }

    public String getClearMessage() {
        return clearMessage;
    }

    public void setClearMessage(String clearMessage) {
        checkNotNull(clearMessage, "Clear message cannot be null");
        this.clearMessage = clearMessage;
    }

    public CGPlayer getPlayer() {
        return player;
    }

    public void setPlayer(CGPlayer player) {
        checkNotNull(player, "Player cannot be null");
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
