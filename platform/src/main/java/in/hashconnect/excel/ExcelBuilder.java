package in.hashconnect.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import static in.hashconnect.util.StringUtil.*;

public class ExcelBuilder {

	private SXSSFWorkbook wb = null;
	private SXSSFSheet sheet = null;
	private Row row = null;

	// styles
	CellStyle dateCellStyle = null;

	public ExcelBuilder(Integer excelRandomAccessSize) {
		wb = new SXSSFWorkbook(excelRandomAccessSize);
		wb.setZip64Mode(Zip64Mode.Always);

		dateCellStyle = wb.createCellStyle();
		dateCellStyle.setDataFormat(wb.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));
	}

	public ExcelBuilder createSheet() {
		sheet = wb.createSheet();
		return this;
	}

	public ExcelBuilder setColumnWidth(int col, int width) {
		sheet.setColumnWidth(col, width);
		return this;
	}

	public ExcelBuilder createSheet(String name) {
		sheet = wb.createSheet(name);
		return this;
	}

	public ExcelBuilder createRow(int rowIndex) {
		row = sheet.createRow(rowIndex);
		return this;
	}

	public ExcelBuilder addBoldHeaders(String... headerRowData) {
		int cellIndex = 0;
		for (String cellData : headerRowData) {
			Cell cell = row.createCell(cellIndex++, CellType.STRING);
			cell.setCellStyle(getBoldStyle());
			cell.setCellValue(cellData);
		}
		return this;
	}

	public ExcelBuilder addBoldHeaders(List<Column> columns) {
		int cellIndex = 0;
		for (Column c : columns) {
			Cell cell = row.createCell(cellIndex++);
			setCellValue(c.getValue(), cell);

			String format = c.getFormat();
			if (in.hashconnect.util.StringUtil.isValid(format)) {
				CellStyle style = wb.createCellStyle();
				style.setDataFormat(wb.createDataFormat().getFormat(format));
				// make it bold
				Font font = wb.createFont();
				font.setBold(true);
				style.setFont(font);

				cell.setCellStyle(style);
			} else {
				cell.setCellStyle(getBoldStyle());
			}
		}
		return this;
	}

	private CellStyle getBoldStyle() {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setBold(true);
		style.setFont(font);
		return style;
	}

	public void addRowDataWithTypes(Object... rowData) {
		addRowDataWithTypesWithIdx(0, rowData);
	}

	public void addRowDataWithTypesWithIdx(int startColumn, Object... rowData) {
		int cellIndex = startColumn;
		for (Object value : rowData) {
			Cell cell = row.createCell(cellIndex++);

			setCellValue(value, cell);
		}
	}

	public void addRowData(List<Column> columns) {
		int cellIndex = 0;
		for (Column cellData : columns) {
			Cell cell = row.createCell(cellIndex++);
			String format = cellData.getFormat();
			if (isValid(format)) {
				CellStyle style = wb.createCellStyle();
				style.setDataFormat(wb.createDataFormat().getFormat(format));
				cell.setCellStyle(style);
			}

			Object value = cellData.getValue();

			setCellValue(value, cell);
		}
	}

	@SuppressWarnings("deprecation")
	private void setCellValue(Object value, Cell cell) {
		if (value == null) {
			cell.setCellValue("");
			return;
		}

		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Float) {
			cell.setCellValue((Float) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof BigInteger) {
			cell.setCellValue(((BigInteger) value).intValue());
		} else if (value instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) value).doubleValue());
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
			cell.setCellStyle(dateCellStyle);
		} else {
			cell.setCellValue(String.valueOf(value));
		}
	}

	public void autoSizeColumn(int columnCount) {
		for (int i = 0; i < columnCount; i++) {
			sheet.trackColumnForAutoSizing(i);
			sheet.autoSizeColumn(i);
		}
	}

	public void writeToOutputStream(OutputStream os) {
		try {
			wb.write(os);
		} catch (IOException e) {
			throw new RuntimeException("failed to write", e);
		} finally {
			wb.dispose();
		}
	}

	public static void exptyExcel(OutputStream out, String sheetName, String content) {
		ExcelBuilder builder = new ExcelBuilder(1).createSheet(sheetName);
		builder.createRow(0).addRowDataWithTypes(content);
		builder.writeToOutputStream(out);
	}
}
