package bambu.dao;

import bambu.model.Product;
import bambu.util.BambuConstant;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductDAO {

    private Connection myConn;
    private DecimalFormat df = new DecimalFormat("#.00");

    public ProductDAO() throws SQLException {
        myConn = DriverManager.getConnection(
                BambuConstant.DB_URL,
                BambuConstant.DB_USER,
                BambuConstant.DB_PASSWORD
        );
    }

    public void addProduct(Product product) throws SQLException {
        PreparedStatement myStmt = null;
        try {
            myStmt = myConn.prepareStatement(BambuConstant.INSERT_PRODUCT_QUERY);
            myStmt.setString(1, product.getTag());
            myStmt.setString(2, product.getName());
            myStmt.setString(3, product.getCompany());
            myStmt.setDouble(4, product.getCp());
            myStmt.setDouble(5, product.getSp());
            myStmt.setDouble(6, product.getProfit());
            myStmt.setBoolean(7, product.getStatus());
            myStmt.executeUpdate();
        } finally {
            close(myStmt, null);
        }
    }

    public void UpdateProduct(Product product) throws SQLException {
        PreparedStatement myStmt = null;
        try {
            myStmt = myConn.prepareStatement(BambuConstant.UPDATE_PRODUCT_QUERY);
            myStmt.setString(1, product.getName());
            myStmt.setString(2, product.getCompany());
            myStmt.setDouble(3, product.getCp());
            myStmt.setDouble(4, product.getSp());
            myStmt.setDouble(5, product.getProfit());
            myStmt.setBoolean(6, product.getStatus());
            myStmt.setInt(7, product.getId());
            myStmt.executeUpdate();
        } finally {
            close(myStmt, null);
        }
    }

    public void deleteItem(int productId) throws SQLException {
        PreparedStatement myStmt = null;
        try {
            myStmt = myConn.prepareStatement(BambuConstant.DELETE_PRODUCT_QUERY);
            myStmt.setInt(1, productId);
            myStmt.executeUpdate();
        } finally {
            close(myStmt, null);
        }
    }

    public ArrayList<Product> getAllProduct() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        Statement myStmt = null;
        ResultSet myRst = null;
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery(BambuConstant.GET_ALL_PRODUCTS_QUERY);
            while (myRst.next()) {
                Product product = convertRowToProduct(myRst);
                products.add(product);
            }
        } finally {
            close(myStmt, myRst);
        }
        return products;
    }

    public ArrayList<Product> searchProduct(String searchTerm) throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        PreparedStatement myStmt = null;
        ResultSet myRst = null;
        try {
            myStmt = myConn.prepareStatement(BambuConstant.SEARCH_PRODUCTS_QUERY);
            String searchPattern = "%" + searchTerm + "%";
            myStmt.setString(1, searchPattern);
            myStmt.setString(2, searchPattern);
            myRst = myStmt.executeQuery();
            while (myRst.next()) {
                Product product = convertRowToProduct(myRst);
                products.add(product);
            }
        } finally {
            close(myStmt, myRst);
        }
        return products;
    }

    public void soldItem(int id) throws SQLException {
        PreparedStatement myStmt = null;
        try {
            myStmt = myConn.prepareStatement(BambuConstant.MARK_SOLD_QUERY);
            myStmt.setInt(1, id);
            myStmt.executeUpdate();
        } finally {
            close(myStmt, null);
        }
    }

    private Product convertRowToProduct(ResultSet myRst) throws SQLException {
        int id = myRst.getInt("id");
        String tag = myRst.getString("tag");
        String name = myRst.getString("name");
        String company = myRst.getString("company");
        double cp = myRst.getDouble("cost_price");
        double sp = myRst.getDouble("selling_price");
        boolean status = myRst.getBoolean("status");
        return new Product(id, tag, name, company, cp, sp, status);
    }

    private void close(Statement myStmt, ResultSet myRst) throws SQLException {
        if (myRst != null) myRst.close();
        if (myStmt != null) myStmt.close();
    }

    public String getTotalItems() throws SQLException {
        Statement myStmt = null;
        ResultSet myRst = null;
        String totalItems = "0";
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery("SELECT COUNT(*) FROM products");
            if (myRst.next()) {
                totalItems = String.valueOf(myRst.getInt(1));
            }
        } finally {
            close(myStmt, myRst);
        }
        return totalItems;
    }

    public String getAvaiItems() throws SQLException {
        Statement myStmt = null;
        ResultSet myRst = null;
        String availableItems = "0";
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery("SELECT COUNT(*) FROM products WHERE status = TRUE");
            if (myRst.next()) {
                availableItems = String.valueOf(myRst.getInt(1));
            }
        } finally {
            close(myStmt, myRst);
        }
        return availableItems;
    }

    public String getSoldItems() throws SQLException {
        Statement myStmt = null;
        ResultSet myRst = null;
        String soldItems = "0";
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery("SELECT COUNT(*) FROM products WHERE status = FALSE");
            if (myRst.next()) {
                soldItems = String.valueOf(myRst.getInt(1));
            }
        } finally {
            close(myStmt, myRst);
        }
        return soldItems;
    }

    public String getTotalInvestment() throws SQLException {
        Statement myStmt = null;
        ResultSet myRst = null;
        String totalInvestment = "0.00";
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery("SELECT SUM(cost_price) FROM products WHERE status = TRUE");
            if (myRst.next()) {
                totalInvestment = df.format(myRst.getDouble(1));
            }
        } finally {
            close(myStmt, myRst);
        }
        return totalInvestment;
    }

    public String getTotalSell() throws SQLException {
        Statement myStmt = null;
        ResultSet myRst = null;
        String totalSell = "0.00";
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery("SELECT SUM(selling_price) FROM products WHERE status = FALSE");
            if (myRst.next()) {
                totalSell = df.format(myRst.getDouble(1));
            }
        } finally {
            close(myStmt, myRst);
        }
        return totalSell;
    }

    public String getProfit() throws SQLException {
        Statement myStmt = null;
        ResultSet myRst = null;
        String profit = "0.00";
        try {
            myStmt = myConn.createStatement();
            myRst = myStmt.executeQuery("SELECT SUM(profit) FROM products WHERE status = FALSE");
            if (myRst.next()) {
                profit = df.format(myRst.getDouble(1));
            }
        } finally {
            close(myStmt, myRst);
        }
        return profit;
    }
}
