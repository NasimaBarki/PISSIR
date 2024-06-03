package app.parkingSpot;

import app.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ParkingSpotDao {

    public static List<ParkingSpot> getAllParkingSpots() {
        final String sql = "SELECT id, available, bookable FROM parkingSpot";

        List<ParkingSpot> parkingSpots = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ParkingSpot p = new ParkingSpot(rs.getInt("id"), rs.getInt("available"), rs.getInt("bookable"));
                parkingSpots.add(p);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parkingSpots;
    }
}
