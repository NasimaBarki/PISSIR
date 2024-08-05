package it.uniupo.pissir.parkingSpot;

import it.uniupo.pissir.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ParkingSpotDao {
    public static List<ParkingSpot> getAllParkingSpots() {
        final String sql = "SELECT id, available, premium FROM parkingSpots";

        List<ParkingSpot> parkingSpots = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ParkingSpot p = new ParkingSpot(rs.getInt("id"), rs.getInt("available"), rs.getInt("premium"));
                parkingSpots.add(p);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkingSpots;
    }

    public static List<ParkingSpot> getPremiumAvailableParkingSpot(String arrival, String reservationEnd) {
        List<ParkingSpot> parkingSpots = new LinkedList<>();

        String sql = "SELECT ps.id, ps.available, ps.premium\n" +
                "FROM parkingSpots ps\n" +
                "WHERE ps.premium = 1 AND ps.id NOT IN (\n" +
                "  SELECT rs.parkingSpotNumber\n" +
                "  FROM reservations rs\n" +
                "  WHERE (\n" +
                "    substr(rs.arrival, 1, 10) = '" + arrival.substring(0, 10) + "' AND\n" +
                "    substr(substr(rs.arrival, 12, 8), 1, 2) >= '" + arrival.substring(11, 13) + "' AND\n" +
                "    substr(rs.reservationEnd, 1, 10) = '" + reservationEnd.substring(0, 10) + "' AND\n" +
                "    substr(substr(rs.reservationEnd, 12, 8), 1, 2) <= '" + reservationEnd.substring(11, 13) + "'\n" +
                "  )\n" +
                ")\n" +
                "ORDER BY ps.id;";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ParkingSpot p = new ParkingSpot(rs.getInt("id"), rs.getInt("available"), rs.getInt("premium"));
                parkingSpots.add(p);
            }

            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkingSpots;
    }

    public static List<ParkingSpot> getPremiumAvailableNowParkingSpot() {
        List<ParkingSpot> parkingSpots = new LinkedList<>();

        String sql = "SELECT id, available, premium FROM parkingSpots WHERE premium = 1 AND available = 0";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ParkingSpot p = new ParkingSpot(rs.getInt("id"), rs.getInt("available"), rs.getInt("premium"));
                parkingSpots.add(p);
            }

            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkingSpots;
    }

    public static List<ParkingSpot> getOccupiedParkingSpots() {
        List<ParkingSpot> parkingSpots = new LinkedList<>();

        String sql = "SELECT id, available, premium FROM parkingSpots WHERE available = 1";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ParkingSpot p = new ParkingSpot(rs.getInt("id"), rs.getInt("available"), rs.getInt("premium"));
                parkingSpots.add(p);
            }

            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkingSpots;
    }

    public static void changeState(String id, String value) {
        final String sql = "UPDATE parkingSpots SET available = ? WHERE id = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(value));
            st.setInt(2, Integer.parseInt(id));

            st.executeUpdate();

            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
