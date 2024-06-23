package app.price;

import app.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class PriceDao {

    public static Price getServiceAndPrice(String service) {
        Price price = null;

        final String sql = "SELECT service, price FROM prices WHERE service = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, service);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                price = new Price(rs.getString("service"), rs.getFloat("price"));
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return price;
    }

    public static int changePrice(float price, String service) {
        int error = 0;

        final String sql = "UPDATE prices SET price = ? WHERE service = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setFloat(1, price);
            st.setString(2, service);

            error = st.executeUpdate();
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return error;
    }
}
