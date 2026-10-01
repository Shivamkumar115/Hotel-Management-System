 import javax.swing.*;
 import java.awt.event.*;
 import java.sql.*;


 public class LoginGUI extends JFrame {
      JTextField UsernameField;
      JPasswordField PasswordField;
      JButton LOGINButton;

    public LoginGUI() {
         setTitle("HOTEL MANAGEMENT SYSTEM LOGIN");
         setSize(400,200);
         setLayout(null);

        JLabel UserLabel = new JLabel(" Enter Username:");
        UserLabel.setBounds(50,30,100,30);
        add(UserLabel);

         UsernameField = new JTextField();
         UsernameField.setBounds(150,30,150,30);
         add(UsernameField);

        JLabel PassLabel = new JLabel("Enter Password:"); 
        PassLabel.setBounds(50,70,100,30);
        add(PassLabel);

         PasswordField = new JPasswordField();
         PasswordField.setBounds(150, 70, 150, 30);
         add(PasswordField);

        LOGINButton = new JButton("Login");
        LOGINButton.setBounds(150, 110, 100, 30);
        add(LOGINButton);

         LOGINButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }


    private void login() {
        String Username = UsernameField.getText();
        String Password = new String(PasswordField.getPassword());

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE Username=? AND Password=?"
            );
            
             ps.setString(1, Username);
             ps.setString(2, Password);
             ResultSet rs = ps.executeQuery();

            if(rs.next()) 
            {
                JOptionPane.showMessageDialog(this, "This Login is Successful!");
                new MainGUI();
                this.dispose();
            }
            else 
            {
                JOptionPane.showMessageDialog(this,"The Invalid Credentials!");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
    public static void main(String[] args) {
        new LoginGUI();
    }
}