import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserAuthenticator {
    private static final String USERS_FILE = "users.csv";
    private Map<String, UserData> registeredUsers;

    public UserAuthenticator() {
        registeredUsers = new HashMap<>();
        loadUsers();
    }

    public Map<String, UserData> getRegisteredUsers() {
        return registeredUsers;
    }

    public void registerUser(UserData user) {
        registeredUsers.put(user.getUsername(), user);
        saveUsers();
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("No existing users file found. Starting with no registered users.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) { // Check if there's a line to skip (the header)
                scanner.nextLine(); // Skip the header line
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",", -1);
                if (parts.length == 7) { // Ensure all 7 fields are present
                    try {
                        String username = parts[0];
                        String password = parts[1];
                        String name = parts[2];
                        int age = Integer.parseInt(parts[3]);
                        String address = parts[4];
                        String email = parts[5];
                        String phoneNumber = parts[6];
                        registeredUsers.put(username, new UserData(username, password, name, age, address, email, phoneNumber));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed user line (age format error): " + line);
                    }
                } else {
                    System.err.println("Skipping malformed user line (incorrect number of fields): " + line);
                }
            }
            System.out.println("Users loaded successfully from " + USERS_FILE);
        } catch (FileNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        try (FileWriter fw = new FileWriter(USERS_FILE);
             BufferedWriter bw = new BufferedWriter(fw)) {
            // Write header
            bw.write("Username,Password,Name,Age,Address,Email,PhoneNumber");
            bw.newLine();
            for (UserData user : registeredUsers.values()) {
                bw.write(user.toString());
                bw.newLine();
            }
            System.out.println("Users saved successfully to " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            e.printStackTrace();
        }
    }
}