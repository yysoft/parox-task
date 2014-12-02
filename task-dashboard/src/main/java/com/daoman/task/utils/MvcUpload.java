/**
 * Copyright 2011 ASTO.
 * All right reserved.
 * Created on 2011-3-17
 */
package com.daoman.task.utils;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.google.common.base.Strings;

/**
 * @author mays (mays@zz91.com)
 * 
 *         created on 2011-3-17
 */
public class MvcUpload {

	public static String localUpload(HttpServletRequest request, String path,
			String filename) {
		MultipartRequest multipartRequest = (MultipartRequest) request;
		
		MultipartFile file = multipartRequest.getFile("uploadfile");

		do {

			String name = file.getOriginalFilename();

			if (Strings.isNullOrEmpty(name)) {
				break;
			}

			if (!Strings.isNullOrEmpty(filename)) {
				name = filename + "."
						+ name.substring(name.lastIndexOf("."), name.length());
			}

			if (!path.endsWith("/")) {
				path = path + "/";
			}

			File upfile = new File(path + name);

			try {
				file.transferTo(upfile);
				return path + name;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} while (false);

		return null;
	}
}
