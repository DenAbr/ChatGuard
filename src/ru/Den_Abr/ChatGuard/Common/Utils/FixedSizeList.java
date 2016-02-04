package ru.Den_Abr.ChatGuard.Common.Utils;

import java.util.ArrayList;

public class FixedSizeList<E> extends ArrayList<E> {
	private int maxSize;
	private static final long serialVersionUID = 2808893166605718195L;

	public FixedSizeList(int size) {
		super();
		if (size <= 0) {
			throw new IllegalArgumentException("Size must be higher than 0");
		}
		maxSize = size;
	}

	public FixedSizeList(int size, E firstE) {
		this(size);
		add(firstE);
	}

	@Override
	public boolean add(E arg0) {
		if (super.size() >= maxSize)
			super.remove(0);
		return super.add(arg0);
	}

	public int getFixedSize() {
		return maxSize;
	}

	public void setFixedSize(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size must be higher than 0");
		}
		maxSize = size;
	}
}
