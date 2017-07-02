package com.ktung.mockrest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

	private static final String GET_REQ  = "GET Request";
	private static final String POST_REQ = "POST Request";

	private int maxFileSize = 10_000 * 1024; // 10 mo
	private int maxMemSize  = 5 * 1024; // 5 ko

	@Override
	public void init() throws ServletException {
		LOGGER.info("Init");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println(GET_REQ);
		LOGGER.info(GET_REQ);

		printParameters(req, out);
		out.close();

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info(POST_REQ);

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

			try (PrintWriter out = resp.getWriter()) {
				List<FileItem> fileItems = upload.parseRequest(req);
				Iterator<FileItem> it = fileItems.iterator();

				while (it.hasNext()) {
					FileItem item = it.next();

					String paramLog = (!item.isFormField())
						? String.format("file { fieldName = %s, filename = %s, content = %s }"
							, item.getFieldName(), item.getName(), item.getString())
						: String.format("param { fieldName = %s, value = %s }"
							, item.getFieldName(), item.getString());

					out.println(paramLog);
					LOGGER.info(paramLog);
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				LOGGER.error("Parsing file items : ", e);
			}
		} else {
			PrintWriter out = resp.getWriter();
			printParameters(req, out);
			out.close();
		}
	}

	private void printParameters(HttpServletRequest req, PrintWriter writer) {
		Map<String, String[]> params = req.getParameterMap();

		params.forEach((k, v) -> {
			StringBuilder paramLog = new StringBuilder("param { ");

			paramLog.append(String.format("%s = ", k));
			for (String val : v) {
				paramLog.append(String.format("%s)", val));
			}

			paramLog.append(" }");
			writer.println(paramLog.toString());
			LOGGER.info(paramLog.toString());
		});
	}

}
