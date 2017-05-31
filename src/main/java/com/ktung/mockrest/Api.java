package com.ktung.mockrest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Api extends HttpServlet {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 9171753608000933490L;

	private static Logger LOGGER = LoggerFactory.getLogger(Api.class);

	private int maxFileSize = 10 * 1024;
	private int maxMemSize = 5 * 1024;

	@Override
	public void init() throws ServletException {
		LOGGER.info("Init");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("GET Request");

		logParameters(req);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("POST Request");

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		LOGGER.info(String.format("Mutipart request %s", isMultipart));

		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// maximum size that will be stored in memory
			factory.setSizeThreshold(maxMemSize);
			// Location to save data that is larger than maxMemSize.
			factory.setRepository(new File("/tmp"));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			// maximum file size to be uploaded.
			upload.setSizeMax(maxFileSize);

			try {
				List<FileItem> fileItems = upload.parseRequest(req);
				Iterator<FileItem> it = fileItems.iterator();

				while (it.hasNext()) {
					FileItem item = it.next();
					if (!item.isFormField()) {
						LOGGER.info(String.format("file { fieldName = %s, filename = %s }", item.getFieldName(),
						        item.getName()));
					} else {
						LOGGER.info(String.format("param { fieldName = %s, value = %s }", item.getFieldName(),
						        item.getString()));

					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				LOGGER.error("Parsing file items : ", e);
			}
		} else {
			logParameters(req);
		}
	}

	private void logParameters(HttpServletRequest req) {
		Map<String, String[]> params = req.getParameterMap();

		params.forEach((k, v) -> {
			String paramLog = "param { ";
			paramLog += String.format("%s = ", k);
			for (String val : v) {
				paramLog += String.format("%s ", val);
			}

			paramLog += " }";
			LOGGER.info(paramLog);
		});

//		String paramsLog = "parameters { ";
//		Iterator<Entry<String, String[]>> it = params.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry<String, String[]> entry = it.next();
//			String key = entry.getKey();
//			String[] values = entry.getValue();
//
//			paramsLog += String.format("%s = ", key);
//			for (String val : values) {
//				paramsLog += String.format("%s ", val);
//			}
//
//			paramsLog += ", ";
//		}
//		paramsLog += " }";
//		LOGGER.info(paramsLog);
	}

}
