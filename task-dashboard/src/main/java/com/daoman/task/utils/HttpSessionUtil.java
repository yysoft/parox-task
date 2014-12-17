package com.daoman.task.utils;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpSessionUtil {
	public static Serializable getAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return (Serializable) session.getAttribute(name);
		} else {
			return null;
		}
	}

	public static void setAttribute(HttpServletRequest request,
			String name, Object value) {
		HttpSession session = request.getSession();
		session.setAttribute(name, value);
	}
	
	public static boolean exists(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute(name) != null) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSessionId(HttpServletRequest request) {
		return request.getSession().getId();
	}

	public static void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

}
