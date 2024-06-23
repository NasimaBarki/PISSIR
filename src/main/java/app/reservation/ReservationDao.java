package app.reservation;

import app.parkingSpot.ParkingSpot;
import app.utils.DBConnect;
import org.apache.commons.lang3.BooleanUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ReservationDao {
    public static int addReservation(Reservation newReservation) {
        int error = 0;
        String sql = "SELECT username from reservations WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, newReservation.getUsername());

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                error = 1;
                return error;
            }

            sql = "INSERT INTO reservations(username, arrival, duration, active, parkingSpotNumber, late) VALUES (?, ?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql);

            st.setString(1, newReservation.getUsername());
            st.setString(2, newReservation.getArrival());
            st.setInt(3, newReservation.getDuration());
            st.setInt(4, BooleanUtils.toInteger(newReservation.isActive()));
            st.setInt(5, newReservation.getParkingSpotNumber());
            st.setInt(6, BooleanUtils.toInteger(newReservation.isLate()));

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;
    }

    public static List<Reservation> getAllReservations(String username) {
        final String sql = "SELECT id, username, arrival, duration, active, parkingSpotNumber, late FROM reservations WHERE username = ?";

        List<Reservation> reservations = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation(rs.getInt("id"), rs.getString("username"), rs.getString("arrival"), rs.getInt("duration"), rs.getBoolean("active"), rs.getInt("parkingSpotNumber"), rs.getBoolean("late"));
                reservations.add(r);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }
}
