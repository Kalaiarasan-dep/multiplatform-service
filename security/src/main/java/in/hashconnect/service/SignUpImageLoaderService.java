package in.hashconnect.service;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import in.hashconnect.storage.vo.FileContent;

import static org.apache.commons.collections4.MapUtils.getInteger;
import static org.apache.commons.collections4.MapUtils.getString;

@Service
public class SignUpImageLoaderService extends AbstractImageLoader implements ImageLoaderService {

	private static final String INVOICE_REQUEST_BUCKET = "invoice_request_bucket";
	@Override
	public FileContent getImage(Map<String, Object> params) {
		Integer id = getInteger(params, "id");
		String type = getString(params, "type");

		Map<String, Object> invReq = genericDao.getPartnerInfiById(id);
		if (MapUtils.isEmpty(invReq))
			throw new RuntimeException("invalid id");

		FileContent fc = new FileContent();
		if(type.equalsIgnoreCase("gst"))
		fc.setName(getString(invReq, "gst_url"));
		if(type.equalsIgnoreCase("pan"))
			fc.setName(getString(invReq, "pancard_url"));
		if(type.equalsIgnoreCase("cheque"))
			fc.setName(getString(invReq, "cancelled_cheque_url"));
		fc.setBucket(settingsUtil.getValue(INVOICE_REQUEST_BUCKET));

		storageService.get(fc);

		if (fc.getData() == null)
			throw new RuntimeException("no image");

		return fc;
	}


}
