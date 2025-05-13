import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO 
{

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    private Connection connect() throws SQLException 
    {
        return DriverManager.getConnection(DB_URL);
    }

    public List<Product> getAllProducts() throws SQLException 
    {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.PRODUCTID, p.PNAME, p.PRICE, p.QUANTITY, c.CATEGNAME, c.CATEGORYID
            FROM PRODUCT p
            JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("PRODUCTID");
                String name = rs.getString("PNAME");
                double price = rs.getDouble("PRICE");
                int quantity = rs.getInt("QUANTITY");
                int categoryId = rs.getInt("CATEGORYID");
                String categoryName = rs.getString("CATEGNAME");

                products.add(new Product(id, name, categoryName, price, quantity));
            }
        }

        return products;
    }

    public List<Product> searchProducts(String name) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.PRODUCTID, p.PNAME, p.PRICE, p.QUANTITY, c.CATEGNAME, c.CATEGORYID
            FROM PRODUCT p
            JOIN CATEGORY c ON p.CATEGORYID = c.CATEGORYID
            WHERE p.PNAME LIKE ?
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("PRODUCTID");
                    String pname = rs.getString("PNAME");
                    double price = rs.getDouble("PRICE");
                    int quantity = rs.getInt("QUANTITY");
                    int categoryId = rs.getInt("CATEGORYID");
                    String categoryName = rs.getString("CATEGNAME");

                    products.add(new Product(id, pname, categoryName, price, quantity));
                }
            }
        }
        return products;
    }
    private int getCategoryIdByName(String categoryName) throws SQLException 
    {
    String sql = "SELECT CATEGORYID FROM CATEGORY WHERE CATEGNAME = ?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, categoryName);
        try (ResultSet rs = stmt.executeQuery()) 
        {
            if (rs.next()) 
            {
                return rs.getInt("CATEGORYID");
            } 
            else 
            {
                throw new SQLException("Category not found: " + categoryName);
            }
        }
    }
}


    public void insertProduct(Product product) throws SQLException 
    {
    int categoryId = getCategoryIdByName(product.getCategoryName()); 

    String sql = "INSERT INTO PRODUCT (PNAME, CATEGORYID, PRICE, QUANTITY) VALUES (?, ?, ?, ?)";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, product.getName());
        stmt.setInt(2, categoryId);
        stmt.setDouble(3, product.getPrice());
        stmt.setInt(4, product.getQuantity());
        stmt.executeUpdate();
    }
}


    public void updateProduct(Product product) throws SQLException 
    {
    int categoryId = getCategoryIdByName(product.getCategoryName()); 

    String sql = "UPDATE PRODUCT SET PNAME = ?, CATEGORYID = ?, PRICE = ?, QUANTITY = ? WHERE PRODUCTID = ?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, product.getName());
        stmt.setInt(2, categoryId);
        stmt.setDouble(3, product.getPrice());
        stmt.setInt(4, product.getQuantity());
        stmt.setInt(5, product.getId());
        stmt.executeUpdate();
        }
   }


    public void deleteProduct(int productId) throws SQLException
     {
        String sql = "DELETE FROM PRODUCT WHERE PRODUCTID = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
    }
}


