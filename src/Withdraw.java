import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Withdraw extends JFrame
{
    Withdraw(String username)
    {
        Font f = new Font("Futura", Font.BOLD, 40);
        Font f2 = new Font("Calibri", Font.PLAIN, 22);

        JLabel title = new JLabel("Withdraw Money", JLabel.CENTER);
        JLabel label = new JLabel("Enter Amount:");
        JTextField t1 = new JTextField(10);
        JButton b1 = new JButton("Withdraw");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        label.setFont(f2);
        t1.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        title.setBounds(200, 30, 400, 50);
        label.setBounds(250, 120, 300, 30);
        t1.setBounds(250, 160, 300, 30);
        b1.setBounds(300, 220, 200, 40);
        b2.setBounds(300, 280, 200, 40);

        c.add(title);
        c.add(label);
        c.add(t1);
        c.add(b1);
        c.add(b2);

          b2.addActionListener(a->
          {
              new Home(username);
              dispose();
          });

          b1.addActionListener(

                  a->{
                      //part 1
                      double balance = 0.0;
                      double wlimit = 0.0;

                      String url = "jdbc:mysql://localhost:3306/batch2";
                      try(Connection con = DriverManager.getConnection(url,"root","Netizun@26"))
                      {
                          String sql = "select balance,wlimit from users where username = ?";
                          try(PreparedStatement pst = con.prepareStatement(sql))
                          {
                              pst.setString(1,username);

                              ResultSet rs =  pst.executeQuery();
                              if(rs.next())
                              {
                                  balance = rs.getDouble("balance");
                                  wlimit = rs.getDouble("wlimit");
                              }

                          }
                      }
                      catch(Exception e)
                      {
                          JOptionPane.showMessageDialog(null,e.getMessage());
                      }

                      //part 2

                      String s1 = t1.getText();
                      if(s1.isEmpty())
                      {
                          JOptionPane.showMessageDialog(null,"Cannot be empty");
                      }
                      else
                      {
                          double wamount = Double.parseDouble(s1);
                          if(wamount>balance)
                          {
                              JOptionPane.showMessageDialog(null,"Insufficient Balance");
                          } else if (wamount>wlimit) {
                                JOptionPane.showMessageDialog(null,"Limit Exceeded");
                          }
                          else {
                              double total = balance-wamount;

                              //part 3

                              try(Connection con = DriverManager.getConnection(url,"root","Netizun@26"))
                              {
                                  String sql = "update users set balance =? where username=?";
                                  try(PreparedStatement pst = con.prepareStatement(sql))
                                  {
                                      pst.setDouble(1,total);
                                      pst.setString(2,username);
                                      pst.executeUpdate();

                                      JOptionPane.showMessageDialog(null,"Successfully Withdrawn from " +username+ " accounts");
                                      t1.setText(" ");

                                      updatePassbook(username,"Withdrawn",-wamount,balance-wamount);
                                  }
                              }
                              catch(Exception e)
                              {
                                  JOptionPane.showMessageDialog(null,e.getMessage());
                              }
                          }
                      }

                  }
          );

        setVisible(true);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Withdraw Money");
    }

    void updatePassbook(String username,String desc,double wamount, double total)
    {
        String url = "jdbc:mysql://localhost:3306/batch2";
        try(Connection con = DriverManager.getConnection(url,"root","Netizun@26"))
        {

            String sql = "insert into transactions(username,description,amount,balance) values(?,?,?,?)";
            try(PreparedStatement pst = con.prepareStatement(sql))
            {
                pst.setString(1,username);
                pst.setString(2,desc);
                pst.setDouble(3,wamount);
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
        new Withdraw("Saloni");
    }
}
