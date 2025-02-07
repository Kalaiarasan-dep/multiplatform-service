package in.hashconnect.notification.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.springframework.jdbc.core.PreparedStatementCreator;

public class NotificationPSCreator implements PreparedStatementCreator {
	private String query, template, type, to, from, cc, params, subject;

	public NotificationPSCreator(String query, String template, String type, String to, String from, String cc,
			String params, String subject) {
		this.query = query;
		this.template = template;
		this.type = type;
		this.to = to;
		this.from = from;
		this.cc = cc;
		this.params = params;
		this.subject = subject;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		int idx = 1;
		ps.setString(idx++, template);
		ps.setString(idx++, type);
		ps.setString(idx++, to);
		ps.setString(idx++, from);
		ps.setString(idx++, cc);
		ps.setString(idx++, params);
		ps.setString(idx++, subject);
//		if (clientId != null)
//			ps.setLong(idx++, clientId);
//		else
			ps.setNull(idx++, Types.NULL);
		return ps;
	}

}
