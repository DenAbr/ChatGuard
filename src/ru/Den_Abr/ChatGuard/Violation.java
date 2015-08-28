package ru.Den_Abr.ChatGuard;

public enum Violation {

	SWEAR("swear"), SPAM("spam"), FLOOD("flood"), CAPS("caps"), BLACKCHAR("none");

	private String pt;

	private Violation(String pt) {
		this.pt = pt;
	}

	public String getPunishmentSection() {
		return pt;
	}
}
