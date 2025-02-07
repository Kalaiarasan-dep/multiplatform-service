package in.hashconnect.controller;

import static in.hashconnect.util.StringUtil.isValid;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.hashconnect.service.ImageLoaderServiceFactory;
import in.hashconnect.storage.vo.FileContent;
import in.hashconnect.util.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/admin/img")
public class ImageController {
	private final static Logger logger = LoggerFactory.getLogger(ImageController.class);

	@Autowired
	private ImageLoaderServiceFactory imageLoaderServiceFactory;

	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	private static final String DEFAULT_IMG_CONTENT_TYPE = "image/jpeg";
	private static final String DEFAULT_IMG = "no-image.jpg";

	@GetMapping("/{type}")
	public void invoiceRequestImage(@PathVariable("type") String type, @RequestParam Map<String, Object> params,
			HttpServletResponse response) throws IOException {
		InputStream in = null;

		String fileName = DEFAULT_IMG;

		FileContent fc = null;
		try {
			fc = imageLoaderServiceFactory.get(type).getImage(params);

			in = new ByteArrayInputStream(fc.getData());
		} catch (Exception e) {
			logger.error("failed to fetch image with params {}", JsonUtil.toString(params), e.getMessage());
			in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_IMG);
		} finally {
			if (fc != null)
				response.setHeader(CONTENT_DISPOSITION, "inline; filename=" + new File(fc.getName()).getName());
			if (fc == null)
				fc = new FileContent();

			if (!isValid(fc.getContentType()))
				fc.setContentType(DEFAULT_IMG_CONTENT_TYPE);

			response.setContentType(fc.getContentType());
			IOUtils.copyLarge(in, response.getOutputStream());
		}
	}
}
