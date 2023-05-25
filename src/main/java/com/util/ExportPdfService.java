package com.util;

import java.io.ByteArrayInputStream;
import java.util.Map;

public interface ExportPdfService {

    ByteArrayInputStream exportReportPdf(String templateName, Map<String, Object> data);

}
