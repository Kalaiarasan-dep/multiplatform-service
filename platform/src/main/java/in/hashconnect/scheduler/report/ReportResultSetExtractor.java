package in.hashconnect.scheduler.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ClassUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import in.hashconnect.excel.Column;
import in.hashconnect.excel.ExcelBuilder;
import in.hashconnect.util.AESUtil;

public class ReportResultSetExtractor implements ResultSetExtractor<Integer> {

	private ExcelBuilder builder;
	private ReportMetaData reportMetaData;
	private AESUtil aesUtil;

	public ReportResultSetExtractor(ExcelBuilder builder, ReportMetaData reportMetaData, AESUtil aesUtil) {
		this.builder = builder;
		this.reportMetaData = reportMetaData;
		this.aesUtil = aesUtil;
	}

	@Override
	public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
		rs.setFetchSize(200);
		List<Column> columns = readColumns(rs);

		Integer rowCounter = reportMetaData.getRowCounter();

		int read = 0;
		while (rs.next()) {
			List<Object> rowValues = new ArrayList<>();
			try {
				for (Column column : columns) {
					readValues(rs, column, rowValues);
				}

				builder.createRow(rowCounter++).addRowDataWithTypes(rowValues.toArray());
			} catch (Exception e) {
				throw new RuntimeException("failed to process row " + rowValues, e);
			}

			read++;
		}

		reportMetaData.setRowCounter(rowCounter);

		return read;
	}

	private List<Column> readColumns(ResultSet rs) throws SQLException {
		List<Column> columns = reportMetaData.getColumns();
		if (columns != null)
			return columns;

		columns = new ArrayList<>();

		int colCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			ResultSetMetaData meta = rs.getMetaData();
			columns.add(new Column(meta.getColumnLabel(i), null, meta.getColumnClassName(i)));
		}

		builder.createRow(0).addBoldHeaders(
				columns.stream().map(c -> c.getName()).collect(Collectors.toList()).toArray(new String[] {}));

		reportMetaData.setColumns(columns);

		return columns;
	}

	private void readValues(ResultSet rs, Column column, List<Object> rowValues) throws Exception {
		String col = column.getName();
		Class<?> clas = ClassUtils.getClass(column.getClassName());

		if (clas == Integer.class) {
			rowValues.add(rs.getInt(col));
		} else if (clas == Boolean.class) {
			rowValues.add(rs.getBoolean(col));
		} else if (clas == Long.class) {
			rowValues.add(rs.getLong(col));
		} else if (clas == Float.class) {
			rowValues.add(rs.getFloat(col));
		} else if (clas == Double.class) {
			rowValues.add(rs.getDouble(col));
		} else if (clas == BigInteger.class) {
			rowValues.add(rs.getLong(col));
		} else if (clas == BigDecimal.class) {
			rowValues.add(rs.getBigDecimal(col));
		} else if (clas == Date.class) {
			Date dateColumnValue = rs.getDate(col);
			rowValues.add(dateColumnValue == null ? null : new java.util.Date(dateColumnValue.getTime()));
		} else if (clas == java.sql.Timestamp.class) {
			Timestamp timeStampColumnValue = rs.getTimestamp(col);
			rowValues.add(timeStampColumnValue == null ? null : new java.util.Date(timeStampColumnValue.getTime()));
		} else if (clas == java.sql.Time.class) {
			Time timeColumnValue = rs.getTime(col);
			rowValues.add(timeColumnValue == null ? null : new java.util.Date(timeColumnValue.getTime()));
		} else {
			String val = rs.getString(col);
			if (col.startsWith("decrypt"))
				val = aesUtil.decrypt(val);
			if (col.startsWith("encrypt"))
				val = aesUtil.encrypt(val);
			rowValues.add(val);
		}
	}

}
