package app.recharge;

import app.utils.DBConnect;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RechargeDao {

    //TODO controllare se l'utente è loggato per non dare errore db
    public static Recharge addRechargeRequest(Recharge newRecharge){
        final String sql = "INSERT INTO recharges(username, percentage, notification) VALUES (?, ?, ?)";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, newRecharge.getUsername());
            st.setInt(2, newRecharge.getPercentage());
            st.setInt(3, newRecharge.getNotification());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static int getNumberOfRechargeRequests() {
        final String sql = "SELECT completed FROM recharges WHERE completed = ?";

        int queue = 0;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, "no");

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                queue+=1;
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queue;
    }
}
