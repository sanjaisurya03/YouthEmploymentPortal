import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class YouthEmploymentPortalApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/youth_employment";  // Update with your MySQL URL
    private static final String DB_USER = "root";  // Replace with your MySQL username
    private static final String DB_PASS = "1234";  // Replace with your MySQL password

    public static void main(String[] args) {
        if (!checkDatabaseConnection()) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
            System.exit(1);
        }

        JFrame frame = new JFrame("Youth Employment Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 1500);

        // Custom JPanel with gradient background
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                // Define a gradient from top to bottom with two colors
                Color color1 = new Color(135, 206, 250); // Light sky blue
                Color color2 = new Color(255, 182, 193); // Light pink
                GradientPaint gradientPaint = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gradientPaint);
                g2d.fillRect(0, 0, width, height);
            }
        };
        
        gradientPanel.setLayout(null); // Use absolute layout for custom positioning
        frame.setContentPane(gradientPanel); // Set the custom gradient panel as content pane

        // Title label
        JLabel titleLabel = new JLabel("Youth Employment Portal", SwingConstants.CENTER);
        titleLabel.setBounds(600, 30, 300, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 34, 59));

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(650, 150, 200, 40);
        loginButton.setBackground(new Color(34, 89, 204));
        loginButton.setForeground(Color.RED);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(e -> showLoginForm(frame));

        // Sign Up button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(650, 220, 200, 40);
        signupButton.setBackground(new Color(34, 89, 204));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("Arial", Font.BOLD, 14));
        signupButton.addActionListener(e -> showSignupForm(frame));

        gradientPanel.add(titleLabel);
        gradientPanel.add(loginButton);
        gradientPanel.add(signupButton);

        frame.setVisible(true);
    }

    private static boolean checkDatabaseConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("Database connected successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    private static void showLoginForm(JFrame frame) {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(1500, 1500);
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.getContentPane().setBackground(new Color(250, 250, 210));
        loginFrame.setLayout(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 50, 100, 30);
        JTextField emailField = new JTextField();
        emailField.setBounds(150, 50, 150, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 150, 30);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 160, 100, 30);
        loginButton.setBackground(new Color(50, 205, 50));
        loginButton.setForeground(Color.WHITE);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please fill in all fields.");
                return;
            }
            String hashedPassword = hashPassword(password);
            String role = login(email, hashedPassword);
            if (role != null) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful!");
                loginFrame.dispose();
                if (role.equals("job_seeker")) {
                    showJobSeekerDashboard(frame, email);
                } else if (role.equals("company")) {
                    showCompanyDashboard(frame, email);
                }
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials, try again!");
            }
        });

        loginFrame.add(emailLabel);
        loginFrame.add(emailField);
        loginFrame.add(passLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);
        loginFrame.setVisible(true);
    }

    private static void showSignupForm(JFrame frame) {
        JFrame signupFrame = new JFrame("Sign Up");
        signupFrame.setSize(400, 450);
        signupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        signupFrame.getContentPane().setBackground(new Color(250, 250, 210));
        signupFrame.setLayout(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 50, 100, 30);
        JTextField emailField = new JTextField();
        emailField.setBounds(150, 50, 150, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 150, 30);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 150, 100, 30);
        String[] roles = {"job_seeker", "company"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 150, 150, 30);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 200, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 200, 150, 30);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(50, 250, 100, 30);
        JTextField phoneField = new JTextField();
        phoneField.setBounds(150, 250, 150, 30);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(150, 350, 100, 30);
        signupButton.setBackground(new Color(255, 140, 0));
        signupButton.setForeground(Color.WHITE);

        signupButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            String fullName = nameField.getText();
            String phoneNumber = phoneField.getText();

            if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty()) {
                JOptionPane.showMessageDialog(signupFrame, "Please fill in all fields.");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (signup(email, hashedPassword, role, fullName, phoneNumber)) {
                JOptionPane.showMessageDialog(signupFrame, "Signup successful! Please log in.");
                signupFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(signupFrame, "Email or company name already exists.");
            }
        });

        signupFrame.add(emailLabel);
        signupFrame.add(emailField);
        signupFrame.add(passLabel);
        signupFrame.add(passwordField);
        signupFrame.add(roleLabel);
        signupFrame.add(roleComboBox);
        signupFrame.add(nameLabel);
        signupFrame.add(nameField);
        signupFrame.add(phoneLabel);
        signupFrame.add(phoneField);
        signupFrame.add(signupButton);
        signupFrame.setVisible(true);
    }

    private static String login(String email, String hashedPassword) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT role FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean signup(String email, String hashedPassword, String role, String fullName, String phoneNumber) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ? OR (role = 'company' AND full_name = ?)";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            checkStmt.setString(2, fullName);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }

            String insertQuery = "INSERT INTO users (email, password, role, full_name, phone_number) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, email);
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, role);
            insertStmt.setString(4, fullName);
            insertStmt.setString(5, phoneNumber);
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private static void showJobSeekerDashboard(JFrame frame, String userEmail) {
        JFrame dashboardFrame = new JFrame("Job Seeker Dashboard");
        dashboardFrame.setSize(600, 600);
        dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboardFrame.getContentPane().setBackground(new Color(245, 245, 220));

        JLabel welcomeLabel = new JLabel("Welcome, " + userEmail, SwingConstants.CENTER);
        welcomeLabel.setBounds(50, 20, 500, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton profileButton = new JButton("Profile");
        profileButton.setBounds(200, 100, 200, 30);
        profileButton.setBackground(new Color(72, 209, 204));
        profileButton.setForeground(Color.WHITE);
        profileButton.addActionListener(e -> showProfilePage(dashboardFrame, userEmail));

        JButton jobListingsButton = new JButton("Job Listings");
        jobListingsButton.setBounds(200, 150, 200, 30);
        jobListingsButton.setBackground(new Color(72, 209, 204));
        jobListingsButton.setForeground(Color.WHITE);
        jobListingsButton.addActionListener(e -> showJobListings(dashboardFrame, userEmail));

        JButton applicationsButton = new JButton("My Applications");
        applicationsButton.setBounds(200, 200, 200, 30);
        applicationsButton.setBackground(new Color(72, 209, 204));
        applicationsButton.setForeground(Color.WHITE);
        applicationsButton.addActionListener(e -> showApplicationsPage(dashboardFrame, userEmail));

        dashboardFrame.setLayout(null);
        dashboardFrame.add(welcomeLabel);
        dashboardFrame.add(profileButton);
        dashboardFrame.add(jobListingsButton);
        dashboardFrame.add(applicationsButton);
        dashboardFrame.setVisible(true);
    }
    
    private static void showCompanyDashboard(JFrame frame, String userEmail) {
        JFrame dashboardFrame = new JFrame("Company Dashboard");
        dashboardFrame.setSize(600, 600);
        dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboardFrame.getContentPane().setBackground(new Color(245, 245, 220));

        JLabel welcomeLabel = new JLabel("Welcome, " + userEmail, SwingConstants.CENTER);
        welcomeLabel.setBounds(50, 20, 500, 30);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton postJobButton = new JButton("Post Job");
        postJobButton.setBounds(200, 100, 200, 30);
        postJobButton.setBackground(new Color(255, 99, 71));
        postJobButton.setForeground(Color.WHITE);
        postJobButton.addActionListener(e -> showPostJobPage(dashboardFrame, userEmail));

        JButton manageJobsButton = new JButton("Manage Jobs");
        manageJobsButton.setBounds(200, 150, 200, 30);
        manageJobsButton.setBackground(new Color(255, 99, 71));
        manageJobsButton.setForeground(Color.WHITE);
        manageJobsButton.addActionListener(e -> showManageJobsPage(dashboardFrame, userEmail));

        JButton manageApplicationsButton = new JButton("Manage Applications");
        manageApplicationsButton.setBounds(200, 200, 200, 30);
        manageApplicationsButton.setBackground(new Color(255, 69, 0));
        manageApplicationsButton.setForeground(Color.WHITE);
        manageApplicationsButton.addActionListener(e -> showManageApplicationsPage(dashboardFrame, userEmail));

        dashboardFrame.setLayout(null);
        dashboardFrame.add(welcomeLabel);
        dashboardFrame.add(postJobButton);
        dashboardFrame.add(manageJobsButton);
        dashboardFrame.add(manageApplicationsButton);
        dashboardFrame.setVisible(true);
    }

    private static void showManageApplicationsPage(JFrame frame, String userEmail) {
    	JFrame manageApplicationsFrame = new JFrame("Manage Applications");
    	manageApplicationsFrame.setSize(600, 500);
    	manageApplicationsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	manageApplicationsFrame.getContentPane().setBackground(new Color(245, 245, 220));

    	JTextArea applicationsArea = new JTextArea();
    	applicationsArea.setBounds(20, 20, 540, 350);
    	applicationsArea.setEditable(false);

    	try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
    	    // Update the query to reflect the correct column names and table schema
    	    String query = "SELECT applications.application_id, applications.user_email, applications.status, jobs.title " +
    	                   "FROM applications " +
    	                   "JOIN jobs ON applications.job_id = jobs.job_id " +
    	                   "WHERE jobs.company_email = ?";
    	    PreparedStatement stmt = conn.prepareStatement(query);
    	    stmt.setString(1, userEmail);
    	    ResultSet rs = stmt.executeQuery();

    	    StringBuilder applicationsText = new StringBuilder();
    	    while (rs.next()) {
    	        applicationsText.append("Application ID: ").append(rs.getInt("application_id")).append("\n");
    	        applicationsText.append("Applicant Email: ").append(rs.getString("user_email")).append("\n");
    	        applicationsText.append("Job Title: ").append(rs.getString("title")).append("\n");
    	        applicationsText.append("Status: ").append(rs.getString("status")).append("\n\n");
    	    }
    	    applicationsArea.setText(applicationsText.toString());
    	} catch (SQLException e) {
    	    e.printStackTrace();
    	    applicationsArea.setText("Error loading applications.");
    	}

    	JButton acceptButton = new JButton("Accept Application");
    	acceptButton.setBounds(150, 400, 150, 30);
    	acceptButton.setBackground(new Color(50, 205, 50));
    	acceptButton.setForeground(Color.WHITE);
    	acceptButton.addActionListener(e -> updateApplicationStatus(manageApplicationsFrame, "Accepted"));

    	JButton declineButton = new JButton("Decline Application");
    	declineButton.setBounds(320, 400, 150, 30);
    	declineButton.setBackground(new Color(255, 69, 0));
    	declineButton.setForeground(Color.WHITE);
    	declineButton.addActionListener(e -> updateApplicationStatus(manageApplicationsFrame, "Rejected"));

    	manageApplicationsFrame.setLayout(null);
    	manageApplicationsFrame.add(applicationsArea);
    	manageApplicationsFrame.add(acceptButton);
    	manageApplicationsFrame.add(declineButton);
    	manageApplicationsFrame.setVisible(true);
    	}

    	private static void updateApplicationStatus(JFrame frame, String status) {
    	    String applicationIdStr = JOptionPane.showInputDialog(frame, "Enter Application ID to " + status.toLowerCase() + ":");
    	    if (applicationIdStr != null && !applicationIdStr.trim().isEmpty()) {
    	        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
    	            // Update the status of the application using application_id
    	            String updateQuery = "UPDATE applications SET status = ? WHERE application_id = ?";
    	            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
    	            updateStmt.setString(1, status);  // Set the status to 'Accepted' or 'Rejected'
    	            updateStmt.setInt(2, Integer.parseInt(applicationIdStr));  // Set the application_id
    	            
    	            int rowsAffected = updateStmt.executeUpdate();

    	            if (rowsAffected > 0) {
    	                JOptionPane.showMessageDialog(frame, "Application " + status.toLowerCase() + " successfully!");
    	                frame.dispose();
    	                showManageApplicationsPage(frame, DB_USER);  // Refresh applications list
    	            } else {
    	                JOptionPane.showMessageDialog(frame, "Application ID not found.");
    	            }
    	        } catch (SQLException ex) {
    	            ex.printStackTrace();
    	            JOptionPane.showMessageDialog(frame, "Error updating application status.");
    	        }
    	    }
    	}

    // Include all previously defined methods for showJobListings, showApplicationsPage, showProfilePage, showPostJobPage, showManageJobsPage, and deleteJob

    private static void showProfilePage(JFrame frame, String userEmail) {
        JFrame profileFrame = new JFrame("Profile");
        profileFrame.setSize(400, 500);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.getContentPane().setBackground(new Color(250, 240, 230));

        JLabel emailLabel = new JLabel("Email: " + userEmail);
        emailLabel.setBounds(50, 20, 300, 30);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 70, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 70, 180, 30);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(50, 120, 100, 30);
        JTextField phoneField = new JTextField();
        phoneField.setBounds(150, 120, 180, 30);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(50, 170, 100, 30);
        JTextField ageField = new JTextField();
        ageField.setBounds(150, 170, 180, 30);

        JLabel experienceLabel = new JLabel("Experience:");
        experienceLabel.setBounds(50, 220, 100, 30);
        JTextArea experienceArea = new JTextArea();
        experienceArea.setBounds(150, 220, 180, 60);
        experienceArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel skillsLabel = new JLabel("Skills:");
        skillsLabel.setBounds(50, 300, 100, 30);
        JTextArea skillsArea = new JTextArea();
        skillsArea.setBounds(150, 300, 180, 60);
        skillsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(150, 380, 100, 30);
        saveButton.setBackground(new Color(50, 205, 50));
        saveButton.setForeground(Color.WHITE);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT full_name, phone_number, age, experience, skills FROM profile WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("full_name"));
                phoneField.setText(rs.getString("phone_number"));
                ageField.setText(rs.getString("age"));
                experienceArea.setText(rs.getString("experience"));
                skillsArea.setText(rs.getString("skills"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        saveButton.addActionListener(e -> {
            String fullName = nameField.getText();
            String phoneNumber = phoneField.getText();
            String age = ageField.getText();
            String experience = experienceArea.getText();
            String skills = skillsArea.getText();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String updateQuery = "UPDATE profile SET full_name = ?, phone_number = ?, age = ?, experience = ?, skills = ? WHERE email = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, fullName);
                updateStmt.setString(2, phoneNumber);
                updateStmt.setString(3, age);
                updateStmt.setString(4, experience);
                updateStmt.setString(5, skills);
                updateStmt.setString(6, userEmail);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(profileFrame, "Profile updated successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(profileFrame, "Error updating profile.");
            }
        });

        profileFrame.setLayout(null);
        profileFrame.add(emailLabel);
        profileFrame.add(nameLabel);
        profileFrame.add(nameField);
        profileFrame.add(phoneLabel);
        profileFrame.add(phoneField);
        profileFrame.add(ageLabel);
        profileFrame.add(ageField);
        profileFrame.add(experienceLabel);
        profileFrame.add(experienceArea);
        profileFrame.add(skillsLabel);
        profileFrame.add(skillsArea);
        profileFrame.add(saveButton);
        profileFrame.setVisible(true);
    }

    private static void showJobListings(JFrame frame, String userEmail) {
        JFrame jobListFrame = new JFrame("Job Listings");
        jobListFrame.setSize(600, 400);
        jobListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jobListFrame.getContentPane().setBackground(new Color(240, 255, 255));

        JTextArea jobListArea = new JTextArea();
        jobListArea.setBounds(20, 20, 540, 300);
        jobListArea.setEditable(false);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT title, description, company, location, job_id FROM jobs";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder jobListText = new StringBuilder();
            while (rs.next()) {
                jobListText.append("Title: ").append(rs.getString("title")).append("\n");
                jobListText.append("Company: ").append(rs.getString("company")).append("\n");
                jobListText.append("Location: ").append(rs.getString("location")).append("\n");
                jobListText.append("Description: ").append(rs.getString("description")).append("\n");
                jobListText.append("Job ID: ").append(rs.getString("job_id")).append("\n\n");
            }
            jobListArea.setText(jobListText.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            jobListArea.setText("Error loading job listings.");
        }

        JButton applyButton = new JButton("Apply");
        applyButton.setBounds(250, 330, 100, 30);
        applyButton.setBackground(new Color(34, 139, 34));
        applyButton.setForeground(Color.WHITE);

        applyButton.addActionListener(e -> {
            String jobIdStr = JOptionPane.showInputDialog(jobListFrame, "Enter Job ID to apply:");
            if (jobIdStr != null && !jobIdStr.trim().isEmpty()) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    String insertQuery = "INSERT INTO applications (user_email, job_id, status) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                    insertStmt.setString(1, userEmail);
                    insertStmt.setString(2, jobIdStr);
                    insertStmt.setString(3, "Pending");
                    insertStmt.executeUpdate();
                    JOptionPane.showMessageDialog(jobListFrame, "Application submitted successfully!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(jobListFrame, "Error applying for the job.");
                }
            }
        });

        jobListFrame.setLayout(null);
        jobListFrame.add(jobListArea);
        jobListFrame.add(applyButton);
        jobListFrame.setVisible(true);
    }

    private static void showApplicationsPage(JFrame frame, String userEmail) {
        JFrame applicationsFrame = new JFrame("My Applications");
        applicationsFrame.setSize(600, 400);
        applicationsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        applicationsFrame.getContentPane().setBackground(new Color(245, 255, 250));

        JTextArea applicationsArea = new JTextArea();
        applicationsArea.setBounds(20, 20, 540, 300);
        applicationsArea.setEditable(false);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT jobs.title, applications.status, jobs.company FROM applications " +
                    "JOIN jobs ON applications.job_id = jobs.job_id WHERE applications.user_email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            StringBuilder applicationsText = new StringBuilder();
            while (rs.next()) {
                applicationsText.append("Job Title: ").append(rs.getString("title")).append("\n");
                applicationsText.append("Company: ").append(rs.getString("company")).append("\n");
                applicationsText.append("Status: ").append(rs.getString("status")).append("\n\n");
            }
            applicationsArea.setText(applicationsText.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            applicationsArea.setText("Error loading applications.");
        }

        applicationsFrame.setLayout(null);
        applicationsFrame.add(applicationsArea);
        applicationsFrame.setVisible(true);
    }

    private static void showPostJobPage(JFrame frame, String userEmail) {
        JFrame postJobFrame = new JFrame("Post Job");
        postJobFrame.setSize(400, 400);
        postJobFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        postJobFrame.getContentPane().setBackground(new Color(250, 250, 240));

        JLabel titleLabel = new JLabel("Job Title:");
        titleLabel.setBounds(50, 50, 100, 30);
        JTextField titleField = new JTextField();
        titleField.setBounds(150, 50, 150, 30);

        JLabel companyLabel = new JLabel("Company:");
        companyLabel.setBounds(50, 100, 100, 30);
        JTextField companyField = new JTextField();
        companyField.setBounds(150, 100, 150, 30);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(50, 150, 100, 30);
        JTextField locationField = new JTextField();
        locationField.setBounds(150, 150, 150, 30);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(50, 200, 100, 30);
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setBounds(150, 200, 150, 80);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(150, 300, 100, 30);
        saveButton.setBackground(new Color(255, 69, 0));
        saveButton.setForeground(Color.WHITE);

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String company = companyField.getText();
            String location = locationField.getText();
            String description = descriptionArea.getText();
            if (title.isEmpty() || company.isEmpty() || location.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(postJobFrame, "Please fill in all fields.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String insertQuery = "INSERT INTO jobs (title, description, company, location, company_email) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, title);
                insertStmt.setString(2, description);
                insertStmt.setString(3, company);
                insertStmt.setString(4, location);
                insertStmt.setString(5, userEmail);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(postJobFrame, "Job posted successfully!");
                postJobFrame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(postJobFrame, "Error posting job.");
            }
        });

        postJobFrame.setLayout(null);
        postJobFrame.add(titleLabel);
        postJobFrame.add(titleField);
        postJobFrame.add(companyLabel);
        postJobFrame.add(companyField);
        postJobFrame.add(locationLabel);
        postJobFrame.add(locationField);
        postJobFrame.add(descriptionLabel);
        postJobFrame.add(descriptionArea);
        postJobFrame.add(saveButton);
        postJobFrame.setVisible(true);
    }

    private static void showManageJobsPage(JFrame frame, String userEmail) {
        JFrame manageJobsFrame = new JFrame("Manage Jobs");
        manageJobsFrame.setSize(600, 400);
        manageJobsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        manageJobsFrame.getContentPane().setBackground(new Color(255, 248, 220));

        JTextArea jobListArea = new JTextArea();
        jobListArea.setBounds(20, 20, 540, 250);
        jobListArea.setEditable(false);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT job_id, title, description, location FROM jobs WHERE company_email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();

            StringBuilder jobListText = new StringBuilder();
            while (rs.next()) {
                jobListText.append("Job ID: ").append(rs.getString("job_id")).append("\n");
                jobListText.append("Title: ").append(rs.getString("title")).append("\n");
                jobListText.append("Location: ").append(rs.getString("location")).append("\n");
                jobListText.append("Description: ").append(rs.getString("description")).append("\n\n");
            }
            jobListArea.setText(jobListText.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            jobListArea.setText("Error loading posted jobs.");
        }

        JButton editButton = new JButton("Edit Job");
        editButton.setBounds(150, 300, 100, 30);
        editButton.setBackground(new Color(34, 139, 34));
        editButton.setForeground(Color.WHITE);

        editButton.addActionListener(e -> {
            String jobIdStr = JOptionPane.showInputDialog(manageJobsFrame, "Enter Job ID to edit:");
            if (jobIdStr != null && !jobIdStr.trim().isEmpty()) {
                showEditJobPage(manageJobsFrame, userEmail, jobIdStr);
            }
        });

        JButton deleteButton = new JButton("Delete Job");
        deleteButton.setBounds(300, 300, 100, 30);
        deleteButton.setBackground(new Color(255, 0, 0));
        deleteButton.setForeground(Color.WHITE);

        deleteButton.addActionListener(e -> {
            String jobIdStr = JOptionPane.showInputDialog(manageJobsFrame, "Enter Job ID to delete:");
            if (jobIdStr != null && !jobIdStr.trim().isEmpty()) {
                deleteJob(userEmail, jobIdStr);
                JOptionPane.showMessageDialog(manageJobsFrame, "Job deleted successfully!");
                manageJobsFrame.dispose();
                showManageJobsPage(frame, userEmail);
            }
        });

        manageJobsFrame.setLayout(null);
        manageJobsFrame.add(jobListArea);
        manageJobsFrame.add(editButton);
        manageJobsFrame.add(deleteButton);
        manageJobsFrame.setVisible(true);
    }

    private static void deleteJob(String userEmail, String jobId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String deleteQuery = "DELETE FROM jobs WHERE company_email = ? AND job_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setString(1, userEmail);
            deleteStmt.setString(2, jobId);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showEditJobPage(JFrame frame, String userEmail, String jobId) {
        JFrame editJobFrame = new JFrame("Edit Job");
        editJobFrame.setSize(400, 400);
        editJobFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editJobFrame.getContentPane().setBackground(new Color(255, 250, 240));

        JLabel titleLabel = new JLabel("Job Title:");
        titleLabel.setBounds(50, 50, 100, 30);
        JTextField titleField = new JTextField();
        titleField.setBounds(150, 50, 150, 30);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(50, 100, 100, 30);
        JTextField locationField = new JTextField();
        locationField.setBounds(150, 100, 150, 30);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(50, 150, 100, 30);
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setBounds(150, 150, 150, 100);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT title, location, description FROM jobs WHERE job_id = ? AND company_email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, jobId);
            stmt.setString(2, userEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                locationField.setText(rs.getString("location"));
                descriptionArea.setText(rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(150, 280, 100, 30);
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String location = locationField.getText();
            String description = descriptionArea.getText();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String updateQuery = "UPDATE jobs SET title = ?, location = ?, description = ? WHERE job_id = ? AND company_email = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, title);
                updateStmt.setString(2, location);
                updateStmt.setString(3, description);
                updateStmt.setString(4, jobId);
                updateStmt.setString(5, userEmail);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(editJobFrame, "Job updated successfully!");
                editJobFrame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editJobFrame, "Error updating job.");
            }
        });

        editJobFrame.setLayout(null);
        editJobFrame.add(titleLabel);
        editJobFrame.add(titleField);
        editJobFrame.add(locationLabel);
        editJobFrame.add(locationField);
        editJobFrame.add(descriptionLabel);
        editJobFrame.add(descriptionArea);
        editJobFrame.add(saveButton);
        editJobFrame.setVisible(true);
    }
}