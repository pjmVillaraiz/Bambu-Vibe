package bambu.util;

public class BambuConstant {
    // Database connection details
    public static final String DB_URL = "jdbc:mysql://localhost:3306/bambuvibedb?useSSL=false&serverTimezone=UTC";
    public static final String DB_USER = "root"; // <--- IMPORTANT: Your MySQL username here
    public static final String DB_PASSWORD = "your_mysql_password"; // <--- IMPORTANT: Your MySQL password here

    // SQL Queries
    public static final String INSERT_PRODUCT_QUERY = "INSERT INTO products (`tag`, `name`, `company`, `cost_price`, `selling_price`, `profit`, `status`) VALUES (?,?,?,?,?,?,?)";
    public static final String UPDATE_PRODUCT_QUERY = "UPDATE products SET `name`=?, `company`=?, `cost_price`=?, `selling_price`=?, `profit`=?, `status`=? WHERE `id`=?";
    public static final String GET_ALL_PRODUCTS_QUERY = "SELECT * FROM products ORDER BY name ASC";
    public static final String SEARCH_PRODUCTS_QUERY = "SELECT * FROM products WHERE name LIKE ? OR company LIKE ? ORDER BY name ASC";
    public static final String DELETE_PRODUCT_QUERY = "DELETE FROM products WHERE id=?";
    public static final String MARK_SOLD_QUERY = "UPDATE products SET status = '0' WHERE id = ?";
}