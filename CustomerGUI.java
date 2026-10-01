import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class CustomerGUI extends JFrame {
    JTextField idField, nameField, aadharField, mobileField, emailField, ageField;
    JComboBox<String> genderBox;
    JButton addBtn, updateBtn, deleteBtn;
    JTable table;
    DefaultTableModel model;

    public CustomerGUI() {
        setTitle("Customer Management");
        setSize(900, 500);
        setLayout(null);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setBounds(20, 20, 80, 25);
        add(idLabel);
        idField = new JTextField();
        idField.setBounds(120, 20, 150, 25);
        add(idField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 60, 80, 25);
        add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(120, 60, 150, 25);
        add(nameField);

        JLabel aadharLabel = new JLabel("Aadhar No:");
        aadharLabel.setBounds(20, 100, 80, 25);
        add(aadharLabel);
        aadharField = new JTextField();
        aadharField.setBounds(120, 100, 150, 25);
        add(aadharField);

        JLabel mobileLabel = new JLabel("Mobile:");
        mobileLabel.setBounds(20, 140, 80, 25);
        add(mobileLabel);
        mobileField = new JTextField();
        mobileField.setBounds(120, 140, 150, 25);
        add(mobileField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 180, 80, 25);
        add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(120, 180, 150, 25);
        add(emailField);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(20, 220, 80, 25);
        add(genderLabel);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Others"});
        genderBox.setBounds(120, 220, 150, 25);
        add(genderBox);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(20, 260, 80, 25);
        add(ageLabel);
        ageField = new JTextField();
        ageField.setBounds(120, 260, 150, 25);
        add(ageField);

        
        addBtn = new JButton("Add");
        addBtn.setBounds(20, 300, 80, 30);
        add(addBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(110, 300, 80, 30);
        add(updateBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(200, 300, 80, 30);
        add(deleteBtn);

       
        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Aadhar No");
        model.addColumn("Mobile");
        model.addColumn("Email");
        model.addColumn("Gender");
        model.addColumn("Age");

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(320, 20, 550, 400);
        add(sp);

        loadTable();


        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void loadTable() {
        try {
            model.setRowCount(0);
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getLong("aadhar"),
                        rs.getLong("mobile"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getInt("age")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCustomer() {
        try {
            if (idField.getText().trim().isEmpty() ||
                nameField.getText().trim().isEmpty() ||
                aadharField.getText().trim().isEmpty() ||
                mobileField.getText().trim().isEmpty() ||
                ageField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "All fields are compulsory!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idField.getText().trim());
            long aadhar = Long.parseLong(aadharField.getText().trim());
            long mobile = Long.parseLong(mobileField.getText().trim());
            int age = Integer.parseInt(ageField.getText().trim());
            String gender = (String) genderBox.getSelectedItem();

            
            if (String.valueOf(aadhar).length() != 12) {
                JOptionPane.showMessageDialog(this, "Aadhar must be 12 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (String.valueOf(mobile).length() != 10) {
                JOptionPane.showMessageDialog(this, "Mobile must be 10 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO customers(id, name, aadhar, mobile, email, gender, age) VALUES(?,?,?,?,?,?,?)");
            ps.setInt(1, id);
            ps.setString(2, nameField.getText());
            ps.setLong(3, aadhar);
            ps.setLong(4, mobile);
            ps.setString(5, emailField.getText());
            ps.setString(6, gender);
            ps.setInt(7, age);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Customer Added!");
            clearFields();
            loadTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Numeric fields are invalid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCustomer() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            long aadhar = Long.parseLong(aadharField.getText().trim());
            long mobile = Long.parseLong(mobileField.getText().trim());
            int age = Integer.parseInt(ageField.getText().trim());
            String gender = (String) genderBox.getSelectedItem();

            
            if (String.valueOf(aadhar).length() != 12) {
                JOptionPane.showMessageDialog(this, "Aadhar must be 12 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (String.valueOf(mobile).length() != 10) {
                JOptionPane.showMessageDialog(this, "Mobile must be 10 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE customers SET name=?, aadhar=?, mobile=?, email=?, gender=?, age=? WHERE id=?"
            );
            ps.setString(1, nameField.getText());
            ps.setLong(2, aadhar);
            ps.setLong(3, mobile);
            ps.setString(4, emailField.getText());
            ps.setString(5, gender);
            ps.setInt(6, age);
            ps.setInt(7, id);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                JOptionPane.showMessageDialog(this, "❌ ID not found for update!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "✅ Customer Updated!");
            }
            clearFields();
            loadTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "integer fields are invalid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE id=?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                JOptionPane.showMessageDialog(this, "❌ ID not found for delete!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "✅ Customer Deleted!");
            }
            clearFields();
            loadTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        aadharField.setText("");
        mobileField.setText("");
        emailField.setText("");
        ageField.setText("");
        genderBox.setSelectedIndex(0);
    }
}
