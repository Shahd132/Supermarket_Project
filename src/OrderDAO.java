import java.sql.*;
import java.time.LocalDateTime;

public class OrderDAO {

    public void placeOrder(int customerId, Cart cart) throws SQLException {
        Connection conn = DatabaseConnector.getConnection();
        try {
            conn.setAutoCommit(false);

         
            String orderSql = """
                INSERT INTO [ORDER] (CID, ORDERDATE, PAYMENTMETHOD, ORDERSTATUS, BUILDING_NO, STREETNAME, AREANAME)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customerId);
            orderStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            orderStmt.setString(3, "Cash");
            orderStmt.setString(4, "Pending");
            orderStmt.setInt(5, 10);
            orderStmt.setString(6, "Main Street");
            orderStmt.setString(7, "Downtown");

            orderStmt.executeUpdate();

           
            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

           
            String itemSql = "INSERT INTO ORDERITEM (ORDERID, PRODUCTID, QUANTITY_ORDERED) VALUES (?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);

            for (CartItem item : cart.getItems()) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getProduct().getId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.addBatch();
            }

            itemStmt.executeBatch();

            
            String updateStockSql = "UPDATE PRODUCT SET QUANTITY = QUANTITY - ? WHERE PRODUCTID = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateStockSql);

            for (CartItem item : cart.getItems()) {
                updateStmt.setInt(1, item.getQuantity());
                updateStmt.setInt(2, item.getProduct().getId());
                updateStmt.addBatch();
            }

            updateStmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
