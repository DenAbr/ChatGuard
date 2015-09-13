package ru.Den_Abr.ChatGuard.Commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SubCommand {
	String name;
	String vals;
	int max;
	int min;
	String desc;
	String perm;
	Method method;

	public SubCommand(Annotation ann, Method meth) {
		Cmd cmdAnn = (Cmd) ann;
		name = cmdAnn.name();
		desc = cmdAnn.desc();
		perm = cmdAnn.perm();
		vals = cmdAnn.args();
		max = cmdAnn.max();
		min = cmdAnn.min();
		method = meth;
	}

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
		if (perm.isEmpty())
			return true;
		return cs.hasPermission(perm);
	}

	public void execute(CommandSender cs, String[] args) {
		try {
			method.invoke(CommandManager.handler, cs, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printHelp(CommandSender arg0, String l) {
		arg0.sendMessage(ChatColor.GOLD + "/" + l + " " + name + " " + vals + " - " + ChatColor.GRAY + desc);
	}

	public boolean isArgsValid(String[] arg3) {
		return min <= arg3.length && max >= arg3.length;
	}
}