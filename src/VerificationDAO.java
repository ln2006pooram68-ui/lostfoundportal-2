
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VerificationDAO {

    public boolean saveVote(int itemId, int userId, String vote) {

        if (!vote.equals("GENUINE") && !vote.equals("SUSPICIOUS")) {
            System.out.println("Invalid vote!");
            return false;
        }

        String sql = "INSERT INTO verification_votes "
                + "(item_id, user_id, vote) VALUES (?, ?, ?)";

        try (Connection con =
                DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, vote);

            pstmt.executeUpdate();

            System.out.println("Vote saved successfully!");
            return true;

        } catch (SQLException e) {
            System.out.println(
                    "Could not save vote. The user may have already voted.");
            return false;
        }
    }
    
    public double getCredibilityScore(int itemId) {

        String sql = "SELECT "
                + "COUNT(CASE WHEN vote = 'GENUINE' "
                + "THEN 1 END) AS genuine_count, "
                + "COUNT(*) AS total_count "
                + "FROM verification_votes "
                + "WHERE item_id = ?";

        try (Connection con =
                DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {

                    int genuineVotes = rs.getInt("genuine_count");
                    int totalVotes = rs.getInt("total_count");

                    if (totalVotes == 0) {
                        return 0.0;
                    }

                    return (genuineVotes * 100.0) / totalVotes;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error calculating credibility score:");
            e.printStackTrace();
        }

        return 0.0;
    }
    
public int getTotalVotes(int itemId) {

    String sql = "SELECT COUNT(*) AS total_count "
            + "FROM verification_votes WHERE item_id = ?";

    try (Connection con =
                DBConnection.getInstance().getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setInt(1, itemId);

        try (java.sql.ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_count");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error counting votes:");
        e.printStackTrace();
    }

    return 0;
}

    public int getVoteCount(int itemId) {
        return getTotalVotes(itemId);
    }

    public boolean hasUserVoted(int itemId, int userId) {

        String sql = "SELECT COUNT(*) FROM verification_votes "
                + "WHERE item_id = ? AND user_id = ?";

        try (Connection con =
                     DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            pstmt.setInt(2, userId);

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking whether user voted:");
            e.printStackTrace();
        }

        return false;
    }


}
