import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpPage extends JFrame 
{
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JPasswordField passwordField;
    private JButton registerBtn;
    private JButton goToSignInBtn;
    private AuthenticationManager authManager;

    public SignUpPage() 
    {
        authManager = new AuthenticationManager();

        setTitle("Supermarket Sign Up");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 128, 0));

        JLabel titleLabel = new JLabel("Supermarket Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(0, 128, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        contentPanel.setPreferredSize(new Dimension(500, 500));
        contentPanel.setMinimumSize(new Dimension(500, 300));

        nameField = createStyledTextField("Full Name");
        emailField = createStyledTextField("Email");
        phoneField = createStyledTextField("Phone");
        addressField = createStyledTextField("Address");

        passwordField = new JPasswordField("Password");
        setupPasswordPlaceholderBehavior(passwordField, "Password");

        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setMaximumSize(new Dimension(500, 50));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        registerBtn = new JButton("Register");
        registerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        registerBtn.setBackground(new Color(34, 139, 34));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        goToSignInBtn = new JButton("Back to Sign In");
        goToSignInBtn.setFont(new Font("Arial", Font.BOLD, 16));
        goToSignInBtn.setBackground(Color.WHITE);
        goToSignInBtn.setForeground(new Color(34, 139, 34));
        goToSignInBtn.setFocusPainted(false);
        goToSignInBtn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        goToSignInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(emailField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(phoneField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(addressField);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(registerBtn);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(goToSignInBtn);

        centerWrapper.add(contentPanel, gbc);
        add(centerWrapper, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> {
            String name = getCleanedText(nameField, "Full Name");
            String email = getCleanedText(emailField, "Email");
            String phone = getCleanedText(phoneField, "Phone");
            String address = getCleanedText(addressField, "Address");
            String password = new String(passwordField.getPassword());


            if (password.equals("Password")||name.equals("Full Name")||email.equals("Email")||address.equals("Address"))
            {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;

            } 
            if(authManager.emailExists(email)||authManager.adminemailExists(email))
            {
                JOptionPane.showMessageDialog(this, "This email already used.");
                return;
            }
            if(authManager.phoneExists(phone))
            {
                JOptionPane.showMessageDialog(this, "This phone number already used.");
                return;
            }
            if(!isValidEmail(email))
            {
                JOptionPane.showMessageDialog(this, "Please enter a valid email.");
                return;
            }
            if(!isValidEgyptianPhone(phone))
            {
                JOptionPane.showMessageDialog(this, "Please enter a valid phone number.");
                return;
            }
            if(!isValidPassword(password))
            {
                JOptionPane.showMessageDialog(this, "Please enter more powerful password");
                return;
            }
            Customer customer = new Customer(name, email, phone, address, password);
            boolean success = authManager.registerCustomer(customer);
            if(!success)
            {
                JOptionPane.showMessageDialog(this,"Registration failed.");
                return;

            }
            else
            {
                new CustomerDashboard(customer);
                dispose();
            }

            
        });

        goToSignInBtn.addActionListener(e ->{new SignInPage();
                dispose();} );

        setVisible(true);
        SwingUtilities.invokeLater(() -> titleLabel.requestFocusInWindow());
    }

    private JTextField createStyledTextField(String placeholder) 
    {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 18));
        field.setForeground(Color.LIGHT_GRAY);
        field.setMaximumSize(new Dimension(500, 50));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) 
            {
                if (field.getText().equals(placeholder)) 
                {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) 
            {
                if (field.getText().isEmpty()) 
                {
                    field.setText(placeholder);
                    field.setForeground(Color.LIGHT_GRAY);
                }
            }
        });

        return field;
    }

    private void setupPasswordPlaceholderBehavior(JPasswordField field, String placeholder) 
    {
        field.setForeground(Color.LIGHT_GRAY);
        field.setText(placeholder);
        field.setEchoChar((char) 0);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) 
            {
                if (new String(field.getPassword()).equals(placeholder)) 
                {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('\u2022');
                }
            }

            public void focusLost(FocusEvent e) 
            {
                if (field.getPassword().length == 0) 
                {
                    field.setText(placeholder);
                    field.setForeground(Color.LIGHT_GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }

    private String getCleanedText(JTextField field, String placeholder) 
    {
        String text = field.getText();
        return text.equals(placeholder) ? "" : text.trim();
    }
    private boolean isValidEmail(String email) 
    {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    public boolean isValidEgyptianPhone(String phone) {
        return phone.matches("^(010|011|012|015)\\d{8}$");
    }
    public boolean isValidPassword(String password)
    {
        return password.length()>=7;
    }

}
