package it.uniupo.pissir.recharge;

import it.uniupo.pissir.utils.DBConnect;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class RechargeDao {
    //Ottieni tutte le ricariche non completate
    public static List<Recharge> getAllRecharges() {
        final String sql = "SELECT id, username, percentage, notification, completed FROM recharges";

        List<Recharge> recharges = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                Recharge recharge = new Recharge(rs.getInt("id"), rs.getString("username"), rs.getInt("percentage"), rs.getInt("notification"), rs.getInt("completed"));
                recharges.add(recharge);
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recharges;
    }

    public static int getAllNotCompletedRecharges() {
        final String sql = "SELECT id, username, percentage, notification, completed FROM recharges WHERE completed = 0";

        int queue = 0;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                queue++;
            }
            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queue;
    }

    //Ottieni la richiesta di ricarica di un certo utente
    public static Recharge getRecharge(String username) {
        Recharge recharge = null;
        final String sql = "SELECT id, username, percentage, notification, completed FROM recharges WHERE username = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                recharge = new Recharge(rs.getInt("id"), username, rs.getInt("percentage"), rs.getInt("notification"), rs.getInt("completed"));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recharge;
    }

    //Aggiungi una nuova richiesta di ricarica al DB
    public static Recharge addRecharge(Recharge newRecharge) {
        final String sql = "INSERT INTO recharges(username, percentage, notification, completed) VALUES (?, ?, ?, ?)";
        ResultSet rs;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, newRecharge.getUsername());
            st.setInt(2, newRecharge.getPercentage());
            st.setInt(3, newRecharge.getNotification());
            st.setInt(4, newRecharge.getCompleted());

            st.executeUpdate();
            rs = st.getGeneratedKeys();
            newRecharge.setId(rs.getInt(1));

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newRecharge;
    }

    public static int deleteRecharges(String username) {
        final String sql = "DELETE FROM recharges WHERE username = ?";
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

    public static void changeRecharge(Recharge modifiedRecharge) {
        final String sql = "UPDATE recharges SET (id, username, percentage, notification, completed) = (?, ?, ?, ?, ?) WHERE username = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1, modifiedRecharge.getId());
            st.setString(2, modifiedRecharge.getUsername());
            st.setInt(3, modifiedRecharge.getPercentage());
            st.setInt(4, modifiedRecharge.getNotification());
            st.setInt(5, modifiedRecharge.getCompleted());

            st.setString(6, modifiedRecharge.getUsername());

            st.executeUpdate();

            st.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Recharge getNotCompletedRecharge(String username) {
        Recharge recharge = null;
        final String sql = "SELECT id, username, percentage, notification, completed FROM recharges WHERE username = ? AND completed = ?";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, username);
            st.setInt(2, 1);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                System.out.println("Ricarica");
                recharge = new Recharge(rs.getInt("id"), username, rs.getInt("percentage"), rs.getInt("notification"), rs.getInt("completed"));
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recharge;
    }
}
