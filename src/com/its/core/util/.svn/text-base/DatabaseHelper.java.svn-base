package com.its.core.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHelper {

	public static void close(ResultSet resultSet, PreparedStatement preStatement) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (Exception ex) {
			}
		}
		if (preStatement != null) {
			try {
				preStatement.close();
			} catch (Exception ex) {
			}
		}
	}
}
