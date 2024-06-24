package app.car;

import app.utils.DBConnect;
import org.apache.commons.lang3.BooleanUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarDao {
    public static Car getCar(String username) {
        Car car = null;
        final String sql = "SELECT username, licensePlate, model, capacity FROM cars WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                car = new Car(rs.getString("username"), rs.getString("licensePlate"), rs.getString("model"), rs.getInt("capacity"));
            }

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return car;
    }

    public static int addCar(Car newCar) {
        int error = 0;
        final String sql = "INSERT INTO cars(username, licensePlate, model, capacity) VALUES (?, ?, ?, ?)";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st = conn.prepareStatement(sql);

            st.setString(1, newCar.getUsername());
            st.setString(2, newCar.getLicensePlate());
            st.setString(3, newCar.getModel());
            st.setInt(4, newCar.getCapacity());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;
    }

    public static int changeCar(Car car) {
        int error = 0;
        final String sql = "UPDATE cars SET licensePlate = ?, model = ?, capacity = ? WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, car.getLicensePlate());
            st.setString(2, car.getModel());
            st.setInt(3, car.getCapacity());
            st.setString(4, car.getUsername());

            error = st.executeUpdate();
            st.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;
    }
}
