package in.hashconnect.dao.impl;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class NotificationPSCreator implements PreparedStatementCreator {
	private String query;
	private Integer template;
	private String type;
	private String to;
	private String from;
	private String cc;
	private String params;
	private String subject;
	private Long clientId;

	public NotificationPSCreator(String query, Integer template, String to, String from, String cc,
			String params, String subject, Long clientId) {
		this.query = query;
		this.template = template;
		this.to = to;
		this.from = from;
		this.cc = cc;
		this.params = params;
		this.subject = subject;
		this.clientId = clientId;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		int idx = 1;
		ps.setInt(idx++, template);
		ps.setString(idx++, to);
		ps.setString(idx++, from);
		ps.setString(idx++, cc);
		ps.setString(idx++, params);
		ps.setString(idx++, subject);
		ps.setLong(idx, clientId);
		return ps;
	}

}
