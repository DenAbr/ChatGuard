package ru.Den_Abr.ChatGuard.Commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cmd {
	String name();

	String vals();

	int max() default 1;

	int min() default 0;

	String desc();

	String perm();
}
