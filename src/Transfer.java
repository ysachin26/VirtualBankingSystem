import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class Transfer extends JFrame {
    Transfer(String username) {
        Font f = new Font("Futura", Font.BOLD, 30);
        Font f2 = new Font("Calibri", Font.PLAIN, 18);

        JLabel title = new JLabel("Transfer Funds", JLabel.CENTER);
        JLabel l1 = new JLabel("Receiver:");
        JTextField t1 = new JTextField(10);

        JLabel l2 = new JLabel("Amount:");
        JTextField t2 = new JTextField(10);

        JButton b1 = new JButton("Transfer");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        l1.setFont(f2);
        t1.setFont(f2);
        l2.setFont(f2);
        t2.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        int labelX = 200, fieldX = 400, yStart = 80, width = 150, height = 30, gap = 40;

        title.setBounds(250, 20, 300, 40);

        l1.setBounds(labelX, yStart, width, height);
        t1.setBounds(fieldX, yStart, width, height);

        l2.setBounds(labelX, yStart + gap, width, height);
        t2.setBounds(fieldX, yStart + gap, width, height);

        b1.setBounds(250, yStart + 2 * gap, 120, 40);
        b2.setBounds(400, yStart + 2 * gap, 120, 40);

        c.add(title);
        c.add(l1);
        c.add(t1);
        c.add(l2);
        c.add(t2);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(a -> {
            new Home(username); // Replace this with your Home class constructor logic.
            dispose();
        });

        b1.addActionListener(a -> {
            String receiver = t1.getText().trim();
            String amountStr = t2.getText().trim();

            if (receiver.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(null, "Amount must be greater than zero.");
                    return;
                }

                String url = "jdbc:mysql://localhost:3306/batch2";
                try (Connection con = DriverManager.getConnection(url, "root", "Netizun@26")) {
                    // Check if receiver exists
                    String checkReceiver = "SELECT balance FROM users WHERE username = ?";
                    try (PreparedStatement pst2 = con.prepareStatement(checkReceiver)) {
                        pst2.setString(1, receiver);
                        ResultSet rs2 = pst2.executeQuery();

                        if (!rs2.next()) {
                            JOptionPane.showMessageDialog(null, "Receiver does not exist.");
                            return;
                        }

                        double receiverBalance = rs2.getDouble("balance");

                        // Check sender's balance and withdrawal limit
                        String checkSender = "SELECT balance, wlimit FROM users WHERE username = ?";
                        try (PreparedStatement pst = con.prepareStatement(checkSender)) {
                            pst.setString(1, username);
                            ResultSet rs = pst.executeQuery();

                            if (!rs.next()) {
                                JOptionPane.showMessageDialog(null, "Sender does not exist. Please check your account.");
                                return;
                            }

                            double senderBalance = rs.getDouble("balance");
                            double wlimit = rs.getDouble("wlimit");

                            if (amount > senderBalance) {
                                JOptionPane.showMessageDialog(null, "Insufficient balance.");
                                return;
                            }

                            if (amount > wlimit) {
                                JOptionPane.showMessageDialog(null, "Withdrawal limit exceeded.");
                                return;
                            }

                            // Perform the transfer
                            con.setAutoCommit(false); // Enable transaction

                            try {
                                // Update sender's balance
                                String updateSender = "UPDATE users SET balance = balance - ? WHERE username = ?";
                                try (PreparedStatement pst3 = con.prepareStatement(updateSender)) {
                                    pst3.setDouble(1, amount);
                                    pst3.setString(2, username);
                                    pst3.executeUpdate();
                                }
                                    updatePassbook(username,"Transfered to "+ receiver , - amount, senderBalance-amount);

                                // Update receiver's balance
                                String updateReceiver = "UPDATE users SET balance = balance + ? WHERE username = ?";
                                try (PreparedStatement pst4 = con.prepareStatement(updateReceiver)) {
                                    pst4.setDouble(1, amount);
                                    pst4.setString(2, receiver);
                                    pst4.executeUpdate();
                                }
                                updatePassbook(username,"Transfered from "+ username , amount, receiverBalance+amount);
                                con.commit(); // Commit the transaction
                                JOptionPane.showMessageDialog(null, "Transaction successful!");
                                t1.setText("");
                                t2.setText("");
                            } catch (Exception e) {
                                con.rollback(); // Rollback in case of an error
                                throw e;
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid amount.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        });

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Transfer Funds");
    }

    void updatePassbook(String username,String desc,double amount, double total)
    {
        String url = "jdbc:mysql://localhost:3306/batch2";
        try(Connection con = DriverManager.getConnection(url,"root","Netizun@26"))
        {

            String sql = "insert into transactions(username,description,amount,balance) values(?,?,?,?)";
            try(PreparedStatement pst = con.prepareStatement(sql))
            {
                pst.setString(1,username);
                pst.setString(2,desc);
                pst.setDouble(3,amount);
                pst.setDouble(4,total);

                pst.executeUpdate();
            }


        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Transfer("saloni"); // Replace "sachin" with an actual username for testing.
    }
}
