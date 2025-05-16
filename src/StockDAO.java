import java.sql.*;
import java.util.*;

public class StockDAO {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public List<Stock> getAllStocks() throws SQLException {
        List<Stock> stocks = new ArrayList<>();
        String query = "SELECT * FROM STOCK";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stocks.add(new Stock(
                    rs.getInt("STOCKID"),
                    rs.getInt("BRANCHID"),
                    rs.getInt("PRODUCTID"),
                    rs.getInt("QUANTITY"),
                    rs.getInt("MINREQUIRED"),
                    rs.getTimestamp("LASTUPDATED")
                ));
            }
        }
        return stocks;
    }

    public void insertStock(Stock stock) throws SQLException {
        String sql = "INSERT INTO STOCK (BRANCHID, PRODUCTID, QUANTITY, MINREQUIRED, LASTUPDATED) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, stock.getBranchId());
            ps.setInt(2, stock.getProductId());
            ps.setInt(3, stock.getQuantity());
            ps.setInt(4, stock.getMinRequired());
            ps.setTimestamp(5, new Timestamp(stock.getLastUpdated().getTime()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    stock.setStockId(rs.getInt(1));
                }
            }

            // Update total product quantity after insertion
            int totalQuantity = getTotalStockQuantityForProduct(stock.getProductId());
            updateProductQuantity(stock.getProductId(), totalQuantity);
        }
    }

    public void updateStock(Stock stock) throws SQLException {
        String sql = "UPDATE STOCK SET BRANCHID=?, PRODUCTID=?, QUANTITY=?, MINREQUIRED=?, LASTUPDATED=? WHERE STOCKID=?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stock.getBranchId());
            ps.setInt(2, stock.getProductId());
            ps.setInt(3, stock.getQuantity());
            ps.setInt(4, stock.getMinRequired());
            ps.setTimestamp(5, new Timestamp(stock.getLastUpdated().getTime()));
            ps.setInt(6, stock.getStockId());
            ps.executeUpdate();

            // Update total product quantity after update
            int totalQuantity = getTotalStockQuantityForProduct(stock.getProductId());
            updateProductQuantity(stock.getProductId(), totalQuantity);
        }
    }

    public void deleteStock(int stockId, int productId) throws SQLException {
        String sql = "DELETE FROM STOCK WHERE STOCKID = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stockId);
            ps.executeUpdate();

            // Update total product quantity after deletion
            int totalQuantity = getTotalStockQuantityForProduct(productId);
            updateProductQuantity(productId, totalQuantity);
        }
    }

    // Helper method to update the total quantity in PRODUCT table
    public void updateProductQuantity(int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE PRODUCT SET QUANTITY = ? WHERE PRODUCTID = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    // Helper method to get total quantity of all STOCK entries for a product
    public int getTotalStockQuantityForProduct(int productId) throws SQLException {
        String sql = "SELECT SUM(QUANTITY) AS TotalQuantity FROM STOCK WHERE PRODUCTID = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TotalQuantity");
                } else {
                    return 0;
                }
            }
        }
    }
    public List<Map<String, Object>> getProductsInBranch(int branchId) throws SQLException {
        List<Map<String, Object>> products = new ArrayList<>();

        String sql = "SELECT p.PRODUCTID, p.PNAME, p.DESCRIPTION, s.QUANTITY " +
                    "FROM PRODUCT p " +
                    "JOIN STOCK s ON p.PRODUCTID = s.PRODUCTID " +
                    "WHERE s.BRANCHID = ?";

        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("PRODUCTID", rs.getInt("PRODUCTID"));
                    row.put("PNAME", rs.getString("PNAME"));
                    row.put("DESCRIPTION", rs.getString("DESCRIPTION"));
                    row.put("QUANTITY", rs.getInt("QUANTITY"));
                    products.add(row);
                }
            }
        }

        return products;
    }

}


