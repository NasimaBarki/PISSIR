package app.recharge;

import app.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RechargeDao {

    //TODO controllare se l'utente è loggato per non dare errore db
    public static int addRechargeRequest(Recharge newRecharge){
        int error = 0;
        String sql = "SELECT username from recharges WHERE username = ?";

        try{
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, newRecharge.getUsername());

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                error = 1;
                return error;
            }

            sql = "INSERT INTO recharges(username, percentage, notification) VALUES (?, ?, ?)";
            st = conn.prepareStatement(sql);

            st.setString(1, newRecharge.getUsername());
            st.setInt(2, newRecharge.getPercentage());
            st.setInt(3, newRecharge.getNotification());

            st.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return error;

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

    public static Recharge getRechargeRequest(String username) {
        Recharge recharge = null;
        final String sql = "SELECT username, percentage, notification, completed FROM recharges WHERE username = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                recharge = new Recharge(username, rs.getInt("percentage"), rs.getInt("notification"), rs.getString("completed"));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recharge;
    }
}
