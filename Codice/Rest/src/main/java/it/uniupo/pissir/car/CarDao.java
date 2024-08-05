package it.uniupo.pissir.car;

import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;

import java.sql.*;

public class CarDao {

    public static Car getCar(String username) {
        Car car = null;
        final String sql = "SELECT * FROM cars WHERE username = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                car = new Car(rs.getString("licensePlate"), rs.getString("username"), rs.getString("model"), rs.getInt("capacity"));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return car;
    }

    public static Car getCarByLicensePlate(String licensePlate) {
        Car car = null;
        final String sql = "SELECT * FROM cars WHERE licensePlate = ? COLLATE NOCASE";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, licensePlate);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                car = new Car(rs.getString("licensePlate"), rs.getString("username"), rs.getString("model"), rs.getInt("capacity"));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return car;
    }

    public static Car addCar(Car newCar) {
        final String sql = "INSERT INTO cars(licensePlate, username, model, capacity) VALUES (UPPER(?), ?, UPPER(?), ?)";
        ResultSet rs;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, newCar.getLicensePlate());
            st.setString(2, newCar.getUsername());
            st.setString(3, newCar.getModel());
            st.setInt(4, newCar.getCapacity());

            st.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newCar;
    }

    public static Car modifyCar(Car newCar) {
        final String sql = "UPDATE cars SET licensePlate = ?, model = ?, capacity = ? WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, newCar.getLicensePlate());
            st.setString(2, newCar.getModel());
            st.setInt(3, newCar.getCapacity());
            st.setString(4, newCar.getUsername());

            st.executeUpdate();

            st.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newCar;
    }
}
