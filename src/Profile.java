import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Profile extends JFrame {

    String naya = ""; // Initialize naya as empty string to hold updated username

    Profile(String username) {
        Font f = new Font("Futura", Font.BOLD, 35);
        Font f2 = new Font("Calibri", Font.PLAIN, 20);

        JLabel title = new JLabel("Profile Settings", JLabel.CENTER);
        title.setFont(f);

        JLabel l1 = new JLabel("Select Field to Update:");
        JComboBox<String> box = new JComboBox<>(new String[]{"Username", "Password", "Phone", "Email","gender"});

        JLabel l2 = new JLabel("Enter New Value:");
        JTextField t1 = new JTextField(15);

        JButton b1 = new JButton("Update");
        JButton b2 = new JButton("Back");

        l1.setFont(f2);
        box.setFont(f2);
        l2.setFont(f2);
        t1.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        title.setBounds(250, 20, 300, 40);
        l1.setBounds(200, 100, 200, 30);
        box.setBounds(400, 100, 200, 30);
        l2.setBounds(200, 160, 200, 30);
        t1.setBounds(400, 160, 200, 30);
        b1.setBounds(250, 220, 120, 40);
        b2.setBounds(400, 220, 120, 40);

        c.add(title);
        c.add(l1);
        c.add(box);
        c.add(l2);
        c.add(t1);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(a -> {
           new Home(username);
                   dispose();
        });

        b1.addActionListener(a -> {
            String s1 = box.getSelectedItem().toString().toLowerCase(); // Selected field
            String s2 = t1.getText().trim(); // New value entered

            // Validation for empty input
            if (s2.isEmpty()) {
                JOptionPane.showMessageDialog(null, "New value cannot be empty!");
                return;
            }

            String url = "jdbc:mysql://localhost:3306/batch2"; // Corrected URL format

            try (Connection con = DriverManager.getConnection(url, "root", "Netizun@26")) {
                // Use placeholders in the SQL statement
                String sql = "UPDATE users SET " + s1 + " = ? WHERE username = ?";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, s2);
                    pst.setString(2, username);
                    pst.executeUpdate();


                    if ("username".equals(s1)) { // If username is updated, update naya
                        naya = s2;
                    }


                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                return;
            }
            if(s1.equals("username"))
            {
                dispose();
                new Profile(s2);
                try (Connection con = DriverManager.getConnection(url, "root", "Netizun@26")) {
                    // Use placeholders in the SQL statement
                    String sql = "UPDATE transactions SET  username = ? WHERE username = ?";
                    try (PreparedStatement pst = con.prepareStatement(sql)) {
                        pst.setString(1, s2);
                        pst.setString(2, username);
                        pst.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Profile Updated Successfully");

                        if ("username".equals(s1)) { // If username is updated, update naya
                            naya = s2;
                        }

                        t1.setText(""); // Clear text field after updating
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        });

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Profile Settings");
    }

    public static void main(String[] args) {
        new Profile("saloni"); // Testing with a sample username
    }
}
