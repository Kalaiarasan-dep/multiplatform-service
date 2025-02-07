package in.hashconnect.vo;

import java.util.List;

public record FileUploadValidateResponse(List<ReferenceIdData> noRefIdRecords,
		List<ReferenceIdData> failedRecords, Integer readyToPrcoess) {}
