import javax.swing.*;
import java.awt.event.*;

public class MainGUI extends JFrame {
    JButton CustomerBtn, RoomBtn, BookingBtn, BillingsBtn;

    public MainGUI() {
        setTitle("HOTEL MANAGMENT SYSTEM");
        setSize(500, 300);
        setLayout(null);

        CustomerBtn = new JButton("Customer Details:");
        CustomerBtn.setBounds(60, 60, 190, 60);
        add(CustomerBtn);

        RoomBtn = new JButton("Room Managements:");
        RoomBtn.setBounds(260, 60, 190, 50);
        add(RoomBtn);

        BookingBtn = new JButton("Room Bookings:");
        BookingBtn.setBounds(60, 130, 190, 50);
        add(BookingBtn);

        BillingsBtn = new JButton("Billing");
        BillingsBtn.setBounds(260, 130, 190, 50);
        add(BillingsBtn);

        CustomerBtn.addActionListener(e -> new CustomerGUI());
        RoomBtn.addActionListener(e -> new RoomGUI());
        BookingBtn.addActionListener(e -> new BookingGUI());
        BillingsBtn.addActionListener(e -> new BillingGUI());

         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setVisible(true);

    }

    public static void main(String[] args) {
       new MainGUI();

    }

}