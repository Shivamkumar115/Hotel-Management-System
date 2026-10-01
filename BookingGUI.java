import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingGUI extends JFrame {
    JComboBox<String> customerCombo, roomCombo;
    JTextField checkinField, checkoutField, bookingIdField, dayField;
    JTextField roomTypeField, roomPriceField; 
    JButton bookBtn, unbookBtn;
    JTable table;
    DefaultTableModel model;

    private ActionListener roomComboListener = e -> loadRoomDetails();

    public BookingGUI() {
        setTitle("Room Booking");
        setSize(900, 550);
        setLayout(null);

        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setBounds(20, 20, 100, 25);
        add(customerLabel);

        customerCombo = new JComboBox<>();
        customerCombo.setBounds(120, 20, 150, 25);
        add(customerCombo);

        
        JLabel roomLabel = new JLabel("Room No:");
        roomLabel.setBounds(20, 60, 100, 25);
        add(roomLabel);

        roomCombo = new JComboBox<>();
        roomCombo.setBounds(120, 60, 150, 25);
        roomCombo.addActionListener(roomComboListener);
        add(roomCombo);

        
        JLabel roomTypeLabel = new JLabel("Room Type:");
        roomTypeLabel.setBounds(20, 100, 100, 25);
        add(roomTypeLabel);

        roomTypeField = new JTextField();
        roomTypeField.setBounds(120, 100, 150, 25);
        roomTypeField.setEditable(false);
        add(roomTypeField);

        
        JLabel roomPriceLabelLbl = new JLabel("Room Price:");
        roomPriceLabelLbl.setBounds(20, 140, 100, 25);
        add(roomPriceLabelLbl);

        roomPriceField = new JTextField();
        roomPriceField.setBounds(120, 140, 150, 25);
        roomPriceField.setEditable(false);
        add(roomPriceField);

        
        JLabel checkinLabel = new JLabel("Check-in (yyyy-MM-dd):");
        checkinLabel.setBounds(20, 180, 160, 25);
        add(checkinLabel);

        checkinField = new JTextField();
        checkinField.setBounds(180, 180, 100, 25);
        add(checkinField);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        checkinField.setText(sdf.format(new Date()));

        
        JLabel dayLabel = new JLabel("Days:");
        dayLabel.setBounds(20, 220, 100, 25);
        add(dayLabel);

        dayField = new JTextField();
        dayField.setBounds(120, 220, 150, 25);
        add(dayField);

        
        JLabel checkoutLabel = new JLabel("Check-out (yyyy-MM-dd):");
        checkoutLabel.setBounds(20, 260, 160, 25);
        add(checkoutLabel);

        checkoutField = new JTextField();
        checkoutField.setBounds(180, 260, 100, 25);
        add(checkoutField);

        
        dayField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    String checkinStr = checkinField.getText().trim();
                    String daysStr = dayField.getText().trim();
                    if (!checkinStr.isEmpty() && !daysStr.isEmpty()) {
                        Date checkin = sdf.parse(checkinStr);
                        int days = Integer.parseInt(daysStr);
                        long millis = checkin.getTime() + (long) days * 24 * 60 * 60 * 1000;
                        checkoutField.setText(sdf.format(new Date(millis)));
                    }
                } catch (Exception ex) { }
            }
        });

        
        JLabel bookingIdLabel = new JLabel("Booking ID:");
        bookingIdLabel.setBounds(20, 300, 100, 25);
        add(bookingIdLabel);

        bookingIdField = new JTextField();
        bookingIdField.setBounds(120, 300, 150, 25);
        add(bookingIdField);

        
        bookBtn = new JButton("Book Room");
        bookBtn.setBounds(20, 340, 150, 30);
        add(bookBtn);

        unbookBtn = new JButton("Unbook Room");
        unbookBtn.setBounds(180, 340, 150, 30);
        add(unbookBtn);

        
        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Booking ID");
        model.addColumn("Customer");
        model.addColumn("Room No");
        model.addColumn("Room Type");
        model.addColumn("Room Price");
        model.addColumn("Check-in");
        model.addColumn("Check-out");
        model.addColumn("Days");

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(360, 20, 500, 450);
        add(sp);

        
        loadCustomers();
        loadRooms();
        loadBookings();

        
        bookBtn.addActionListener(e -> bookRoom());
        unbookBtn.addActionListener(e -> unbookRoom());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadCustomers() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name FROM customers")) {

            customerCombo.removeAllItems();
            while (rs.next()) {
                customerCombo.addItem(rs.getInt("id") + "-" + rs.getString("name"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadRooms() {
        roomCombo.removeActionListener(roomComboListener);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT room_number FROM rooms WHERE status='Available'")) {

            roomCombo.removeAllItems();
            while (rs.next()) {
                roomCombo.addItem(rs.getString("room_number"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            roomCombo.addActionListener(roomComboListener);
        }
    }

    private void loadRoomDetails() {
        try {
            String roomNo = (String) roomCombo.getSelectedItem();
            if (roomNo == null) return;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT type, price FROM rooms WHERE room_number=?")) {
                ps.setString(1, roomNo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roomTypeField.setText(rs.getString("type"));
                        roomPriceField.setText(String.valueOf(rs.getDouble("price")));
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadBookings() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT b.id, c.name, b.room_number, r.type, r.price, b.checkin, b.checkout, b.days " +
                     "FROM bookings b " +
                     "JOIN customers c ON b.customer_id=c.id " +
                     "JOIN rooms r ON b.room_number=r.room_number")) {

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getDate("checkin"),
                        rs.getDate("checkout"),
                        rs.getInt("days")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void bookRoom() {
        try {
            String bookingIdStr = bookingIdField.getText().trim();
            if (bookingIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Booking ID is required!");
                return;
            }

            String customerStr = (String) customerCombo.getSelectedItem();
            String roomNumber = (String) roomCombo.getSelectedItem();
            if (customerStr == null || roomNumber == null) {
                JOptionPane.showMessageDialog(this, "Select customer and room!");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            int customerId = Integer.parseInt(customerStr.split("-")[0]);
            String customerName = customerStr.split("-")[1];

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date checkinDate = new java.sql.Date(sdf.parse(checkinField.getText().trim()).getTime());
            java.sql.Date checkoutDate = new java.sql.Date(sdf.parse(checkoutField.getText().trim()).getTime());
            int days = Integer.parseInt(dayField.getText().trim());

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement psCheck = conn.prepareStatement("SELECT id FROM bookings WHERE id=?")) {
                psCheck.setInt(1, bookingId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        JOptionPane.showMessageDialog(this, "Booking ID already exists!");
                        return;
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO bookings(id, customer_id, customer_name, room_number, checkin, checkout, days) VALUES(?,?,?,?,?,?,?)")) {

                    ps.setInt(1, bookingId);
                    ps.setInt(2, customerId);
                    ps.setString(3, customerName);
                    ps.setString(4, roomNumber);
                    ps.setDate(5, checkinDate);
                    ps.setDate(6, checkoutDate);
                    ps.setInt(7, days);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps2 = conn.prepareStatement("UPDATE rooms SET status='Booked' WHERE room_number=?")) {
                    ps2.setString(1, roomNumber);
                    ps2.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Room Booked for " + customerName + "!");
            loadBookings();
            loadRooms();

            bookingIdField.setText("");
            dayField.setText("");
            roomTypeField.setText("");
            roomPriceField.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error booking room: " + e.getMessage());
        }
    }

    private void unbookRoom() {
        try {
            String bookingIdStr = bookingIdField.getText().trim();
            if (bookingIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Booking ID to unbook!");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement psRoom = conn.prepareStatement("SELECT room_number FROM bookings WHERE id=?")) {

                psRoom.setInt(1, bookingId);
                String roomNumber;

                try (ResultSet rs = psRoom.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Booking ID not found!");
                        return;
                    }
                    roomNumber = rs.getString("room_number");
                }

                try (PreparedStatement psDel = conn.prepareStatement("DELETE FROM bookings WHERE id=?")) {
                    psDel.setInt(1, bookingId);
                    psDel.executeUpdate();
                }

                try (PreparedStatement psUpdate = conn.prepareStatement("UPDATE rooms SET status='Available' WHERE room_number=?")) {
                    psUpdate.setString(1, roomNumber);
                    psUpdate.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Booking Cancelled & Room Available!");
            loadBookings();
            loadRooms();

            bookingIdField.setText("");
            dayField.setText("");
            roomTypeField.setText("");
            roomPriceField.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error unbooking room: " + e.getMessage());
        }
    }
}
