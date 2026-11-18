import java.io.*;
import java.time.LocalDate;
import java.util.*;

interface Storable {
    String toRecord();
}

class Supplier implements Storable {
    private int id;
    private String name, phone, city;

    Supplier(int id, String name, String phone, String city) {
        this.id = id; this.name = name; this.phone = phone; this.city = city;
    }
    int getId() { return id; }
    String getName() { return name; }
    public String toRecord() { return id + "|" + name + "|" + phone + "|" + city; }
    static Supplier fromRecord(String r) {
        String[] p = r.split("\\|");
        return new Supplier(Integer.parseInt(p[0]), p[1], p[2], p[3]);
    }
    public String toString() {
        return String.format("%-5d %-20s %-12s %-12s", id, name, phone, city);
    }
}

class Product implements Storable {
    private int id, supplierId, stock, reorderLevel;
    private String name;
    private double price;

    Product(int id, String name, int supplierId, double price, int stock, int reorderLevel) {
        this.id = id; this.name = name; this.supplierId = supplierId;
        this.price = price; this.stock = stock; this.reorderLevel = reorderLevel;
    }
    int getId() { return id; }
    int getSupplierId() { return supplierId; }
    double getPrice() { return price; }
    int getStock() { return stock; }
    void addStock(int qty) { if (qty > 0) stock += qty; }
    boolean isLow() { return stock <= reorderLevel; }
    public String toRecord() {
        return id + "|" + name + "|" + supplierId + "|" + price + "|" + stock + "|" + reorderLevel;
    }
    static Product fromRecord(String r) {
        String[] p = r.split("\\|");
        return new Product(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]),
                Double.parseDouble(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5]));
    }
    public String toString() {
        return String.format("%-5d %-18s Sup:%-3d Rs.%-9.2f Stock:%-5d %s",
                id, name, supplierId, price, stock, isLow() ? "** LOW STOCK **" : "OK");
    }
}

class PurchaseOrder implements Storable {
    private int id, supplierId, productId, quantity;
    private double totalCost;
    private String status;

    PurchaseOrder(int id, int supplierId, int productId, int quantity, double totalCost, String status) {
        this.id = id; this.supplierId = supplierId; this.productId = productId;
        this.quantity = quantity; this.totalCost = totalCost; this.status = status;
    }
    int getId() { return id; }
    int getProductId() { return productId; }
    int getQuantity() { return quantity; }
    String getStatus() { return status; }
    void setStatus(String s) { status = s; }
    public String toRecord() {
        return id + "|" + supplierId + "|" + productId + "|" + quantity + "|" + totalCost + "|" + status;
    }
    static PurchaseOrder fromRecord(String r) {
        String[] p = r.split("\\|");
        return new PurchaseOrder(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                Integer.parseInt(p[3]), Double.parseDouble(p[4]), p[5]);
    }
    public String toString() {
        return String.format("Order %-4d Sup:%-3d Prod:%-3d Qty:%-5d Cost:Rs.%-10.2f %s",
                id, supplierId, productId, quantity, totalCost, status);
    }
}

class Delivery implements Storable {
    private int id, orderId;
    private String date;

    Delivery(int id, int orderId, String date) {
        this.id = id; this.orderId = orderId; this.date = date;
    }
    public String toRecord() { return id + "|" + orderId + "|" + date; }
    static Delivery fromRecord(String r) {
        String[] p = r.split("\\|");
        return new Delivery(Integer.parseInt(p[0]), Integer.parseInt(p[1]), p[2]);
    }
    public String toString() {
        return String.format("Delivery %-4d Order:%-4d Date:%s", id, orderId, date);
    }
}

class SupplyChainManager {
    private List<Supplier> suppliers = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<PurchaseOrder> orders = new ArrayList<>();
    private List<Delivery> deliveries = new ArrayList<>();

    private Supplier findSupplier(int id) {
        for (Supplier s : suppliers) if (s.getId() == id) return s;
        return null;
    }
    private Product findProduct(int id) {
        for (Product p : products) if (p.getId() == id) return p;
        return null;
    }
    private PurchaseOrder findOrder(int id) {
        for (PurchaseOrder o : orders) if (o.getId() == id) return o;
        return null;
    }

    void addSupplier(String name, String phone, String city) {
        int id = suppliers.size() + 1;
        suppliers.add(new Supplier(id, name, phone, city));
        System.out.println("Supplier added with ID " + id);
    }

    void addProduct(String name, int supplierId, double price, int stock, int reorder) {
        if (findSupplier(supplierId) == null) {
            System.out.println("Error: Supplier not found.");
            return;
        }
        if (price <= 0 || stock < 0 || reorder < 0) {
            System.out.println("Error: Invalid price or quantity.");
            return;
        }
        int id = products.size() + 1;
        products.add(new Product(id, name, supplierId, price, stock, reorder));
        System.out.println("Product added with ID " + id);
    }

    void createOrder(int supplierId, int productId, int qty) {
        Supplier s = findSupplier(supplierId);
        Product p = findProduct(productId);
        if (s == null || p == null) {
            System.out.println("Error: Supplier or product not found.");
            return;
        }
        if (p.getSupplierId() != supplierId) {
            System.out.println("Error: This product is not supplied by that supplier.");
            return;
        }
        if (qty <= 0) {
            System.out.println("Error: Quantity must be greater than zero.");
            return;
        }
        int id = orders.size() + 1;
        double total = qty * p.getPrice();
        orders.add(new PurchaseOrder(id, supplierId, productId, qty, total, "PENDING"));
        System.out.println("Order " + id + " created. Total cost: Rs." + total);
    }

    void recordDelivery(int orderId) {
        PurchaseOrder o = findOrder(orderId);
        if (o == null) {
            System.out.println("Error: Order not found.");
            return;
        }
        if (!o.getStatus().equals("PENDING")) {
            System.out.println("Error: Order already delivered.");
            return;
        }
        findProduct(o.getProductId()).addStock(o.getQuantity());
        o.setStatus("DELIVERED");
        deliveries.add(new Delivery(deliveries.size() + 1, orderId, LocalDate.now().toString()));
        System.out.println("Delivery recorded. Stock updated.");
    }

    void showSuppliers() {
        System.out.println("\n--- Suppliers ---");
        if (suppliers.isEmpty()) System.out.println("No records.");
        for (Supplier s : suppliers) System.out.println(s);
    }
    void showStock() {
        System.out.println("\n--- Stock Report ---");
        if (products.isEmpty()) System.out.println("No records.");
        for (Product p : products) System.out.println(p);
    }
    void showOrders() {
        System.out.println("\n--- Purchase Orders ---");
        if (orders.isEmpty()) System.out.println("No records.");
        for (PurchaseOrder o : orders) System.out.println(o);
    }
    void showDeliveries() {
        System.out.println("\n--- Deliveries ---");
        if (deliveries.isEmpty()) System.out.println("No records.");
        for (Delivery d : deliveries) System.out.println(d);
    }

    private void save(String file, List<? extends Storable> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Storable s : list) pw.println(s.toRecord());
        } catch (IOException e) {
            System.out.println("Could not save " + file);
        }
    }

    void saveAll() {
        save("suppliers.txt", suppliers);
        save("products.txt", products);
        save("orders.txt", orders);
        save("deliveries.txt", deliveries);
    }

    private List<String> readLines(String file) {
        List<String> lines = new ArrayList<>();
        File f = new File(file);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null)
                if (!line.trim().isEmpty()) lines.add(line);
        } catch (IOException e) {
            System.out.println("Could not read " + file);
        }
        return lines;
    }

    void loadAll() {
        for (String l : readLines("suppliers.txt")) suppliers.add(Supplier.fromRecord(l));
        for (String l : readLines("products.txt")) products.add(Product.fromRecord(l));
        for (String l : readLines("orders.txt")) orders.add(PurchaseOrder.fromRecord(l));
        for (String l : readLines("deliveries.txt")) deliveries.add(Delivery.fromRecord(l));
    }
}

public class SupplyChain {
    static Scanner sc = new Scanner(System.in);

    static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
    static double readDouble(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    static String readText(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim().replace("|", "/");
    }

    public static void main(String[] args) {
        SupplyChainManager m = new SupplyChainManager();
        m.loadAll();
        int choice;
        do {
            System.out.println("\n====== Supply Chain Management System ======");
            System.out.println("1. Add Supplier");
            System.out.println("2. Add Product");
            System.out.println("3. Create Purchase Order");
            System.out.println("4. Record Delivery");
            System.out.println("5. View Stock Report");
            System.out.println("6. View Suppliers");
            System.out.println("7. View Purchase Orders");
            System.out.println("8. View Deliveries");
            System.out.println("9. Save and Exit");
            choice = readInt("Enter Choice: ");

            switch (choice) {
                case 1:
                    m.addSupplier(readText("Name: "), readText("Phone: "), readText("City: "));
                    break;
                case 2:
                    String pn = readText("Product Name: ");
                    int sid = readInt("Supplier ID: ");
                    double pr = readDouble("Unit Price: ");
                    int st = readInt("Opening Stock: ");
                    int rl = readInt("Reorder Level: ");
                    m.addProduct(pn, sid, pr, st, rl);
                    break;
                case 3:
                    int s2 = readInt("Supplier ID: ");
                    int p2 = readInt("Product ID: ");
                    int q2 = readInt("Quantity: ");
                    m.createOrder(s2, p2, q2);
                    break;
                case 4:
                    m.recordDelivery(readInt("Order ID: "));
                    break;
                case 5: m.showStock(); break;
                case 6: m.showSuppliers(); break;
                case 7: m.showOrders(); break;
                case 8: m.showDeliveries(); break;
                case 9:
                    m.saveAll();
                    System.out.println("Data saved. System closed.");
                    break;
                default:
                    System.out.println("Invalid Choice!");
            }
        } while (choice != 9);
    }
}
