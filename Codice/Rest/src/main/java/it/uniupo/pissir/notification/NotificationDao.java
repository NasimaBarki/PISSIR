package it.uniupo.pissir.notification;

import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class NotificationDao {
    public static Notification addNotification(Notification newNotification) {
        final String sql = "INSERT INTO notifications(username, message, date) VALUES (?, ?, ?)";
        ResultSet rs;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, newNotification.getUsername());
            st.setString(2, newNotification.getMessage());
            st.setString(3, newNotification.getDate());

            st.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newNotification;
    }

    public static List<Notification> getNotifications(String username) {
        List<Notification> allNotifications = new LinkedList<>();

        final String sql = "SELECT * FROM notifications WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while(rs.next()){
                Notification notification = new Notification(rs.getString("username"), rs.getString("message"), rs.getString("date"));
                allNotifications.add(notification);
            }

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return allNotifications;
    }
}
