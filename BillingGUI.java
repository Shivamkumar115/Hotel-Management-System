import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class BillingGUI extends JFrame {
    JComboBox<String> bookingCombo;
    JButton calcBtn;
    JLabel custIdLabel, custNameLabel, roomNoLabel, roomTypeLabel, roomPriceLabel, daysLabel, totalLabel;

    public BillingGUI() {
        setTitle("Billing");
        setSize(450, 400);
        setLayout(null);

        JLabel bookingLabel = new JLabel("Select Booking:");
        bookingLabel.setBounds(20, 20, 120, 25);
        add(bookingLabel);

        bookingCombo = new JComboBox<>();
        bookingCombo.setBounds(150, 20, 250, 25);
        add(bookingCombo);

        custIdLabel = new JLabel("Customer ID: ");
        custIdLabel.setBounds(20, 60, 300, 25);
        add(custIdLabel);

        custNameLabel = new JLabel("Customer Name: ");
        custNameLabel.setBounds(20, 90, 300, 25);
        add(custNameLabel);

        roomNoLabel = new JLabel("Room No: ");
        roomNoLabel.setBounds(20, 120, 300, 25);
        add(roomNoLabel);

        roomTypeLabel = new JLabel("Room Type: ");
        roomTypeLabel.setBounds(20, 150, 300, 25);
        add(roomTypeLabel);

        roomPriceLabel = new JLabel("Room Price: ");
        roomPriceLabel.setBounds(20, 180, 300, 25);
        add(roomPriceLabel);

        daysLabel = new JLabel("No. of Days: ");
        daysLabel.setBounds(20, 210, 300, 25);
        add(daysLabel);

        calcBtn = new JButton("Calculate Bill");
        calcBtn.setBounds(20, 250, 150, 30);
        add(calcBtn);

        totalLabel = new JLabel("Total Bill: ₹0.0");
        totalLabel.setBounds(20, 290, 300, 25);
        add(totalLabel);

        loadBookings();

        bookingCombo.addActionListener(e -> loadBookingDetails());
        calcBtn.addActionListener(e -> calculateBill());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadBookings() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT b.id, c.name, r.room_number FROM bookings b " +
                         "JOIN customers c ON b.customer_id=c.id " +
                         "JOIN rooms r ON b.room_number=r.room_number";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                bookingCombo.addItem(rs.getInt("id") + " - " + rs.getString("name") + " - " + rs.getString("room_number"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBookingDetails() {
        try {
            String selected = (String) bookingCombo.getSelectedItem();
            if (selected == null) return;
            int bookingId = Integer.parseInt(selected.split("-")[0].trim());

            Connection conn = DBConnection.getConnection();
            String sql = "SELECT c.id AS cid, c.name, r.room_number, r.type, r.price, " +
                         "DATEDIFF(b.checkout, b.checkin) AS days " +
                         "FROM bookings b " +
                         "JOIN customers c ON b.customer_id=c.id " +
                         "JOIN rooms r ON b.room_number=r.room_number " +
                         "WHERE b.id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                custIdLabel.setText("Customer ID: " + rs.getInt("cid"));
                custNameLabel.setText("Customer Name: " + rs.getString("name"));
                roomNoLabel.setText("Room No: " + rs.getString("room_number"));
                roomTypeLabel.setText("Room Type: " + rs.getString("type"));
                roomPriceLabel.setText("Room Price: ₹" + rs.getDouble("price"));
                daysLabel.setText("No. of Days: " + rs.getInt("days"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateBill() {
        try {
            String selected = (String) bookingCombo.getSelectedItem();
            if (selected == null) return;
            int bookingId = Integer.parseInt(selected.split("-")[0].trim());

            Connection conn = DBConnection.getConnection();
            String sql = "SELECT r.price, DATEDIFF(b.checkout, b.checkin) AS days " +
                         "FROM bookings b JOIN rooms r ON b.room_number=r.room_number WHERE b.id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double price = rs.getDouble("price");
                int days = rs.getInt("days");
                double total = price * days;
                totalLabel.setText("Total Bill: ₹" + total);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new BillingGUI();
    }
}
