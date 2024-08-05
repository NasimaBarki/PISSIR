package it.uniupo.pissir.payment;

import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PaymentDao {
    public static List<Payment> getAllPayments(String start, String service, String userType) {

        String sql = "SELECT id, username, date, time, type, price, userType FROM payments";
        ArrayList<String> filters = new ArrayList<>();
        if ((start != null && !start.equals("")) || (service != null && !service.equals("nothing")) || (userType != null && !userType.equals("nothing"))){
            sql += " WHERE ";

            if (start != null && !start.equals("")) {
                filters.add("date = \"" + start + "\"");
            }
            if (service != null && !service.equals("nothing")) {
                filters.add("type = \"" + service + "\"");
            }
            if (userType != null && !userType.equals("nothing")) {
                filters.add("userType = \"" + userType + "\"");
            }
        }

        for (int i = 0; i < filters.size(); i++) {
            if (i != 0) {
                sql += " AND ";
            }
            sql += filters.get(i);
        }

        List<Payment> payments = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Payment p = new Payment(rs.getInt("id"), rs.getString("username"), rs.getString("date"), rs.getString("time"), rs.getString("type"), rs.getFloat("price"), rs.getString("userType"));
                payments.add(p);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }

    public static Payment addPayment(Payment newPayment) {
        final String sql = "INSERT INTO payments(username, date, time, type, price, userType) VALUES (?, ?, ?, ?, ?, ?)";

        int error = 0;

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, newPayment.getUsername());
            st.setString(2, newPayment.getDate());
            st.setString(3, newPayment.getTime());
            st.setString(4, newPayment.getType());
            st.setFloat(5, newPayment.getPrice());
            st.setString(6, newPayment.getUserType());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newPayment;
    }
}
