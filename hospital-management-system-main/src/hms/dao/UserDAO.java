package hms.dao;

import hms.model.User;
import java.util.List;

public class UserDAO extends FileBasedDAO<User, String> {

    public UserDAO() {
        super("data/users.txt");
    }

    @Override
    protected User parseEntity(String line) {
        try {
            String[] data = line.split(",");
            if (data.length >= 4) {
                User user = new User(
                        data[0], // username
                        data[1], // password
                        data[2], // fullName
                        data[3]  // role
                );
                if (data.length >= 5) {
                    user.setActive(Boolean.parseBoolean(data[4]));
                }
                return user;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String entityToFileString(User user) {
        return user.toFileString();
    }

    @Override
    protected String getIdFromEntity(User user) {
        return user.getUsername();
    }

    @Override
    protected boolean matchesProperty(User user, String propertyName, Object value) {
        if (value == null) return false;

        switch (propertyName.toLowerCase()) {
            case "username":
                return user.getUsername().equals(value.toString());
            case "role":
                return user.getRole().equals(value.toString());
            case "fullname":
                return user.getFullName().toLowerCase().contains(value.toString().toLowerCase());
            case "active":
                if (value instanceof Boolean) {
                    return user.isActive() == (Boolean) value;
                }
                return user.isActive() == Boolean.parseBoolean(value.toString());
            default:
                return false;
        }
    }

    // Additional method for authentication
    public User authenticate(String username, String password) {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password) &&
                    user.isActive()) {
                return user;
            }
        }
        return null;
    }

    // Find users by role
    public List<User> findByRole(String role) {
        return findByProperty("role", role);
    }
}

