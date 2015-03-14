package ru.Den_Abr.ChatGuard.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import ru.Den_Abr.ChatGuard.Commands.Cmd;

public class SubCommand {
	String name;
	String vals;
	int max;
	int min;
	String desc;
	String perm;
	Method method;

	public String getName() {
		return name;
	}

	public String getVals() {
		return vals;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public String getDesc() {
		return desc;
	}

	public boolean isPermitted(CommandSender cs) {
		return cs.hasPermission(perm);
	}

	public SubCommand(Annotation ann) {
		Cmd cmdAnn = (Cmd) ann;
		name = cmdAnn.name();
		desc = cmdAnn.desc();
		perm = cmdAnn.perm();
		vals = cmdAnn.vals();
		max = cmdAnn.max();
		min = cmdAnn.min();
	}

	public void execute(CommandSender cs, String[] args) {
		try {
			method.invoke(method.getDeclaringClass(), cs, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printHelp(CommandSender arg0, String l) {
		arg0.sendMessage(ChatColor.GOLD + "/" + l + " " + name + " " + vals
				+ " - " + desc);
	}

	public boolean isArgsValid(String[] arg3) {
		return min <= arg3.length && max >= arg3.length;
	}
}