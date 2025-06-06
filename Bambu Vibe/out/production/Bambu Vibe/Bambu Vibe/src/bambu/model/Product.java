package bambu.model;

public class Product {

    private int id;
    private String tag;
    private String name;
    private String company;
    private double cp; // Cost Price
    private double sp; // Selling Price
    private double profit;
    private boolean status; // true for available, false for sold

    public Product(int id, String tag, String name, String company, double cp, double sp, boolean status) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.company = company;
        this.status = status;
        this.cp = cp;
        this.sp = sp;
        this.profit = sp - cp;
    }

    public Product(String tag, String name, String company, double cp, double sp, boolean status) {
        this(0, tag, name, company, cp, sp, status); // ID will be auto-generated by DB
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public double getCp() { return cp; }
    public void setCp(double cp) { this.cp = cp; }

    public double getSp() { return sp; }
    public void setSp(double sp) { this.sp = sp; }

    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }

    public boolean getStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    @Override
    public String toString() {
        return "Product [id=" + id + ", tag=" + tag + ", name=" + name + ", company=" + company + ", cp=" + cp
                + ", sp=" + sp + ", profit=" + profit + ", status=" + (status ? "Available" : "Sold") + "]";
    }
}