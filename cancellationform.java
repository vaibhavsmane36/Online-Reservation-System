package online.reservation.system;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 *
 * @author Ananda
 */
public class cancellationform extends JFrame {
    private final JLabel pnrLabel;
    private JTextField pnrTextField;
    private final JButton submitButton;

    private Connection connection;

    public cancellationform() {
        setTitle("Cancellation Form");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new FlowLayout());

        pnrLabel = new JLabel("Enter PNR number:");
        pnrTextField = new JTextField(15);
        submitButton = new JButton("OK");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pnr = pnrTextField.getText();
                TicketInfo ticketInfo = fetchTicketInfoFromDatabase(pnr);
                if (ticketInfo != null) {
                    String message = "Name: " + ticketInfo.getName() +
                            "\nTrain Name: " + ticketInfo.getTrainName() +
                            "\nTrain Number: " + ticketInfo.getTrainNumber() +
                            "\nDate: " + ticketInfo.getDate() +
                            "\nClass Type: " + ticketInfo.getClassType() +
                            "\nFrom: " + ticketInfo.getFrom() +
                            "\nTo: " + ticketInfo.getTo();
                    int choice = JOptionPane.showConfirmDialog(cancellationform.this, message, "Ticket Information", JOptionPane.OK_CANCEL_OPTION);
                    if (choice == JOptionPane.OK_OPTION) {
                        performCancellation(pnr);
                    }
                } else {
                    JOptionPane.showMessageDialog(cancellationform.this, "Invalid PNR number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(pnrLabel);
        add(pnrTextField);
        add(submitButton);

        setVisible(true);

        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            String databaseUrl = "jdbc:mysql://localhost:3306/reservation";
            String username = "root";
            String password = "";

            connection = DriverManager.getConnection(databaseUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(cancellationform.this, "Failed to connect to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private TicketInfo fetchTicketInfoFromDatabase(String pnr) {
        String query = "SELECT name, train_name, train_number, date, class_type, from_place, to_place FROM reservations WHERE pnr_number = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, pnr);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String trainName = resultSet.getString("train_name");
                String trainNumber = resultSet.getString("train_number");
                String date = resultSet.getString("date");
                String classType = resultSet.getString("class_type");
                String from = resultSet.getString("from_place");
                String to = resultSet.getString("to_place");

                return new TicketInfo(name, trainName, trainNumber, date, classType, from, to);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(cancellationform.this, "Failed to fetch ticket");
            }
        return null;
    }

    private void performCancellation(String pnr) {        
        try {
            String delete_query = "DELETE FROM reservations WHERE pnr_number = ?";
            PreparedStatement statement = connection.prepareStatement(delete_query);
            statement.setString(1, pnr);
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected>0) {
                JOptionPane.showMessageDialog(this, "Ticket Cancellation Successful!");
            }
            else {
            JOptionPane.showMessageDialog(this, "Ticket cannot be cancelled!");
        }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new cancellationform();
            }
        });
    }
}

