import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class RoomGUI extends JFrame {
    JTextField roomField, priceField, statusField;
    JComboBox<String> typeBox, acBox;
    JButton addBtn, updateBtn, deleteBtn;
    JTable table;
    DefaultTableModel model;

    public RoomGUI() {
        setTitle("Room Management");
        setSize(800, 400);
        setLayout(null);

        JLabel roomLabel = new JLabel("Room No:");
        roomLabel.setBounds(20, 20, 80, 25);
        add(roomLabel);
        roomField = new JTextField();
        roomField.setBounds(100, 20, 150, 25);
        add(roomField);


        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(20, 60, 80, 25);
        add(typeLabel);
        String[] roomTypes = {"Single", "Double", "4 Seater"};
        typeBox = new JComboBox<>(roomTypes);
        typeBox.setBounds(100, 60, 150, 25);
        add(typeBox);


        JLabel acLabel = new JLabel("AC/Non-AC:");
        acLabel.setBounds(20, 100, 80, 25);
        add(acLabel);
        String[] acOptions = {"Non-AC", "AC"};
        acBox = new JComboBox<>(acOptions);
        acBox.setBounds(100, 100, 150, 25);
        add(acBox);

        
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 140, 80, 25);
        add(priceLabel);
        priceField = new JTextField();
        priceField.setBounds(100, 140, 150, 25);
        priceField.setEditable(false);
        add(priceField);

        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(20, 180, 80, 25);
        add(statusLabel);
        statusField = new JTextField("Available");
        statusField.setBounds(100, 180, 150, 25);
        add(statusField);

       
        addBtn = new JButton("Add");
        addBtn.setBounds(20, 220, 80, 30);
        add(addBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(110, 220, 80, 30);
        add(updateBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(200, 220, 80, 30);
        add(deleteBtn);

       
        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Room No");
        model.addColumn("Type");
        model.addColumn("Price");
        model.addColumn("Status");

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(300, 20, 470, 300);
        add(sp);

        
        loadTable();

    
        ActionListener priceUpdater = e -> updatePrice();
        typeBox.addActionListener(priceUpdater);
        acBox.addActionListener(priceUpdater);

        
        addBtn.addActionListener(e -> addRoom());
        updateBtn.addActionListener(e -> updateRoom());
        deleteBtn.addActionListener(e -> deleteRoom());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void updatePrice() {
        String type = (String) typeBox.getSelectedItem();
        String ac = (String) acBox.getSelectedItem();

        int price = 0;
        if (type.equals("Single")) price = ac.equals("AC") ? 800 : 500;
        else if (type.equals("Double")) price = ac.equals("AC") ? 1500 : 1000;
        else if (type.equals("4 Seater")) price = ac.equals("AC") ? 3000 : 2000;

        priceField.setText(String.valueOf(price));
    }

    private void loadTable() {
        try {
            model.setRowCount(0);
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM rooms");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("room_number"),
                    rs.getString("type"),
                    rs.getDouble("price"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addRoom() {
        if (roomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room No is compulsory!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO rooms(room_number, type, price, status) VALUES(?,?,?,?)"
            );
            ps.setString(1, roomField.getText());
            ps.setString(2, typeBox.getSelectedItem() + " " + acBox.getSelectedItem());
            ps.setDouble(3, Double.parseDouble(priceField.getText()));
            ps.setString(4, statusField.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room Added Successfully!");
            clearFields();
            loadTable();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Room No already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRoom() {
        if (roomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room No is compulsory!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE rooms SET type=?, price=?, status=? WHERE room_number=?"
            );
            ps.setString(1, typeBox.getSelectedItem() + " " + acBox.getSelectedItem());
            ps.setDouble(2, Double.parseDouble(priceField.getText()));
            ps.setString(3, statusField.getText());
            ps.setString(4, roomField.getText());
            int rows = ps.executeUpdate();
            if (rows > 0)
                JOptionPane.showMessageDialog(this, "Room Updated!");
            else
                JOptionPane.showMessageDialog(this, "Room No not found!", "Error", JOptionPane.ERROR_MESSAGE);
            clearFields();
            loadTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteRoom() {
        if (roomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room No is compulsory!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM rooms WHERE room_number=?"
            );
            ps.setString(1, roomField.getText());
            int rows = ps.executeUpdate();
            if (rows > 0)
                JOptionPane.showMessageDialog(this, "Room Deleted!");
            else
                JOptionPane.showMessageDialog(this, "Room No not found!", "Error", JOptionPane.ERROR_MESSAGE);
            clearFields();
            loadTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        roomField.setText("");
        priceField.setText("");
        statusField.setText("Available");
        typeBox.setSelectedIndex(0);
        acBox.setSelectedIndex(0);
    }
}
