package ru.Den_Abr.ChatGuard.Common.Utils;

import com.google.common.base.Objects;

public class MessagePair {
	private String key;
	private String value;
	private boolean old;

	public MessagePair(String key, String value, boolean old) {
		this.key = key;
		this.value = value;
		this.old = old;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean isOld() {
		return old;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).addValue(key).addValue(value).toString();
	}
}
