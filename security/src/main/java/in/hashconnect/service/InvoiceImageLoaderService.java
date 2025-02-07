package in.hashconnect.service;

import static org.apache.commons.collections4.MapUtils.*;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import in.hashconnect.storage.vo.FileContent;

@Service
public class InvoiceImageLoaderService extends AbstractImageLoader implements ImageLoaderService {
	private static final String INVOICE_REQUEST_BUCKET = "invoice_request_bucket";

	@Override
	public FileContent getImage(Map<String, Object> params) {
		Integer id = getInteger(params, "id");

		Map<String, Object> invReq = genericDao.getInvoiceRequestById(id);
		if (MapUtils.isEmpty(invReq))
			throw new RuntimeException("invalid id");

		FileContent fc = new FileContent();
		fc.setName(getString(invReq, "invoice_scan_uri"));
		fc.setBucket(settingsUtil.getValue(INVOICE_REQUEST_BUCKET));

		storageService.get(fc);

		if (fc.getData() == null)
			throw new RuntimeException("no image");

		return fc;
	}

}
