package hms.service;

import hms.dao.UserDAO;
import hms.interfaces.ManagementService;
import hms.model.User;
import hms.util.PasswordUtils;

import java.util.ArrayList;
import java.util.List;

public class UserService implements ManagementService<User, String> {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();

        // Initialize with admin user if no users exist
        if (userDAO.count() == 0) {
            User adminUser = new User("admin", "admin", "Administrator", "ADMIN");
            userDAO.save(adminUser);
        }
    }

    // Method for user authentication
    public User authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }

    @Override
    public boolean add(User user) {
        // Check if username already exists
        if (userDAO.exists(user.getUsername())) {
            return false;
        }

        // In a real application, you'd hash the password before saving
        // user.setPassword(PasswordUtils.hashPassword(user.getPassword()));

        return userDAO.save(user);
    }

    @Override
    public User getById(String username) {
        return userDAO.findById(username);
    }

    @Override
    public List<User> getAll() {
        return userDAO.findAll();
    }

    @Override
    public boolean update(User user) {
        User existingUser = userDAO.findById(user.getUsername());
        if (existingUser == null) {
            return false;
        }

        // Don't update password if it's empty (not changed)
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        } else {
            // In a real application, you'd hash the new password
            // user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        }

        return userDAO.update(user);
    }

    @Override
    public boolean delete(String username) {
        return userDAO.delete(username);
    }

    @Override
    public List<User> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userDAO.findAll();
        }

        List<User> allUsers = userDAO.findAll();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getRole().toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    // Find users by role
    public List<User> findByRole(String role) {
        return userDAO.findByRole(role);
    }

    // Change password
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userDAO.authenticate(username, oldPassword);
        if (user == null) {
            return false; // Authentication failed
        }

        // In a real application, you'd hash the new password
        // user.setPassword(PasswordUtils.hashPassword(newPassword));
        user.setPassword(newPassword);

        return userDAO.update(user);
    }

    // Enable or disable user
    public boolean setUserStatus(String username, boolean active) {
        User user = userDAO.findById(username);
        if (user == null) {
            return false;
        }

        user.setActive(active);
        return userDAO.update(user);
    }
}
