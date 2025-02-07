package in.hashconnect.excel.reader;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.hashconnect.excel.reader.exception.ExcelReaderException;
import in.hashconnect.util.StringUtil;

public class ExcelReader {
	private final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
	private WorkBook workBook;
	private String fileName;
	private InputStream in;
	private Integer noOfHeaderColumns;

	public ExcelReader(String fileName, InputStream in) {
		workBook = new WorkBook();
		this.fileName = fileName;
		this.in = in;
	}

	public ExcelReader read() throws ExcelReaderException {
		if (fileName.endsWith("xls")) {
			processHSSFFormat(in);
		} else if (fileName.endsWith("xlsx")) {
			processXSSFFormat(in);
		}
		return this;
	}

	private String processHSSFFormat(InputStream in) throws ExcelReaderException {
		HSSFWorkbook hssfWorkBook = null;
		try {
			hssfWorkBook = new HSSFWorkbook(in);
			int sheetCount = hssfWorkBook.getNumberOfSheets();
			for (int index = 0; index < sheetCount; index++) {
				HSSFSheet hssfSheet = hssfWorkBook.getSheetAt(index);
				Sheet sheet = new Sheet();
				workBook.getSheets().add(sheet);

				Iterator<org.apache.poi.ss.usermodel.Row> rows = hssfSheet.iterator();

				boolean header = true;
				while (rows.hasNext()) {
					org.apache.poi.ss.usermodel.Row hssfRow = rows.next();
					if (header) {
						header = false;
						continue;
					}

					//Row r =
					prepareRow(hssfRow);
				//	if (r != null)
				//		sheet.getRows().add(r);
				}
			}

			return "SUCCESS";
		} catch (ExcelReaderException e) {
			throw e;
		} catch (Exception e) {
			logger.error("excel.parse failed due to " + e.getMessage(), e);
			throw new ExcelReaderException(e.getMessage());
		} finally {
			if (hssfWorkBook != null)
				try {
					hssfWorkBook.close();
				} catch (IOException e) {
				}
		}

	}

	private String processXSSFFormat(InputStream in) throws ExcelReaderException {
		XSSFWorkbook xssfWorkBook = null;
		try {
			xssfWorkBook = new XSSFWorkbook(in);
			int sheetCount = xssfWorkBook.getNumberOfSheets();
			for (int index = 0; index < sheetCount; index++) {
				XSSFSheet hssfSheet = xssfWorkBook.getSheetAt(index);
				Sheet sheet = new Sheet();
				workBook.getSheets().add(sheet);

				Iterator<org.apache.poi.ss.usermodel.Row> rows = hssfSheet.iterator();

				boolean header = true;
				while (rows.hasNext()) {
					org.apache.poi.ss.usermodel.Row hssfRow = rows.next();

					Row r = prepareRow(hssfRow);
					if (r != null) {
						sheet.getRows().add(r);

						if (header) {
							noOfHeaderColumns = r.getColumnValues().size();
						}
					}

					header = false;
				}
			}

			return "SUCCESS";
		} catch (ExcelReaderException e) {
			throw e;
		} catch (Exception e) {
			logger.error("excel.parse failed due to " + e.getMessage(), e);
			throw new ExcelReaderException(e.getMessage());
		} finally {
			if (xssfWorkBook != null)
				try {
					xssfWorkBook.close();
				} catch (IOException e) {
				}
		}
	}

	private Row prepareRow(org.apache.poi.ss.usermodel.Row hssfRow) throws ExcelReaderException {
		try {
			Row row = new Row();
			List<Object> values = new ArrayList<>();

			boolean empty = true;

			for (int i = 0; i < getColumnsToRead(hssfRow); i++) {
				Cell cell = hssfRow.getCell(i);

				if (cell == null) {
					values.add(StringUtil.EMPTY);
					continue;
				}
				switch (cell.getCellType()) {
				case FORMULA:
					switch (cell.getCachedFormulaResultType()) {
						case NUMERIC:
							values.add( cell.getNumericCellValue());
							break;
						case STRING:
							values.add(cell.getStringCellValue().trim());
							break;
						default:
							values.add(StringUtil.EMPTY);
							break;
					}
					empty = false;
					break;
				case BOOLEAN:
					values.add(cell.getBooleanCellValue());
					empty = false;
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-d");
						Date date = cell.getDateCellValue();
						values.add(simpleDateFormat.format(date));
					} else {
						cell.setCellType(CellType.STRING);
						values.add( cell.getStringCellValue().trim());
					}
					empty = false;
					break;
				case BLANK:
					values.add(StringUtil.EMPTY);
					break;
				case ERROR:
						values.add(cell.getErrorCellValue());
						break;
				default:
						values.add(cell.getStringCellValue().trim());
					    empty = false;
					break;
				}

			}

			// consider row only if all column values are matching
			if (!empty) {
				row.setColumnValues(values);
				return row;
			}

		} catch (Exception e) {
			throw new ExcelReaderException(hssfRow.getRowNum(), "failed due to " + e.getMessage());
		}

		return null;
	}

	public WorkBook getWorkBook() {
		return this.workBook;
	}

	private int getColumnsToRead(org.apache.poi.ss.usermodel.Row hssfRow) {
		return noOfHeaderColumns == null ? hssfRow.getPhysicalNumberOfCells() : noOfHeaderColumns;
	}

}
