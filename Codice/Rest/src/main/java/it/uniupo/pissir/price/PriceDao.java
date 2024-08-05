package it.uniupo.pissir.price;

import it.uniupo.pissir.car.Car;
import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class PriceDao {
    public static List<Price> getAllPrices() {
        final String sql = "SELECT service, price FROM prices";

        List<Price> prices = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                Price price = new Price(rs.getString("service"), rs.getFloat("price"));
                prices.add(price);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prices;
    }

    public static void changePrice(String service, Float price) {
        final String sql = "UPDATE prices SET price = ? WHERE service = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setFloat(1, price);
            st.setString(2, service);

            st.executeUpdate();
            
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Price getPrice(String service) {
        Price price = null;
        final String sql = "SELECT * FROM prices WHERE service = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, service);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                price = new Price(rs.getString("service"), rs.getFloat("price"));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return price;
    }
}
