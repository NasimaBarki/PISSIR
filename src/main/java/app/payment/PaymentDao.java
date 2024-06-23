package app.payment;

import app.parkingSpot.ParkingSpot;
import app.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class PaymentDao {
    public static int addPayment(Payment newPayment) {
        final String sql = "INSERT INTO payments(username, date, time, type, price, userType) VALUES (?, ?, ?, ?, ?, ?)";

        int error = 0;

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, newPayment.getUsername());
            st.setString(2, newPayment.getDate().toString());
            st.setString(3, newPayment.getTime().toString());
            st.setString(4, newPayment.getType());
            st.setFloat(5, newPayment.getPrice());
            st.setString(6, newPayment.getUserType());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;
    }

    public static List<Payment> getAll() {
        final String sql = "SELECT id, username, date, time, type, price, userType FROM payments";

        List<Payment> payments = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Payment p = new Payment(rs.getInt("id"), rs.getString("username"), rs.getDate("date").toLocalDate(), rs.getTime("time").toLocalTime(), rs.getString("type"), rs.getFloat("price"), rs.getString("userType"));
                payments.add(p);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }
}
