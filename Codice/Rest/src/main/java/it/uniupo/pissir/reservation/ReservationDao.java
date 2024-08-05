package it.uniupo.pissir.reservation;

import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ReservationDao {
    public static List<Reservation> getAllReservations() {
        final String sql = "SELECT id, username, arrival, reservationEnd, parkingSpotNumber, late FROM reservations";

        List<Reservation> reservations = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation(rs.getInt("id"), rs.getString("username"), rs.getString("arrival"), rs.getString("reservationEnd"), rs.getInt("parkingSpotNumber"), rs.getInt("late"));
                reservations.add(r);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public static Reservation getReservation(String username) {
        Reservation reservation = null;
        final String sql = "SELECT id, username, arrival, reservationEnd, parkingSpotNumber, late FROM reservations WHERE username = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                reservation = new Reservation(rs.getInt("id"), rs.getString("username"), rs.getString("arrival"), rs.getString("reservationEnd"), rs.getInt("parkingSpotNumber"), rs.getInt("late"));
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservation;
    }

    public static Reservation addReservation(Reservation newReservation) {
        String sql = "SELECT username from reservations WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, newReservation.getUsername());

            st.executeQuery();

            sql = "INSERT INTO reservations(username, arrival, reservationEnd, parkingSpotNumber, late) VALUES (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql);

            st.setString(1, newReservation.getUsername());
            st.setString(2, newReservation.getArrival());
            st.setString(3, newReservation.getReservationEnd());
            st.setInt(4, newReservation.getParkingSpotNumber());
            st.setInt(5, newReservation.getLate());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return newReservation;
    }

    public static int deleteReservations(String username) {
        final String sql = "DELETE FROM reservations WHERE username = ?";
        int exitval=0;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            exitval = st.executeUpdate();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exitval;
    }
}
