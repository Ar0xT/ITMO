package client.utilities;

import common.network.AuthCredentials;

import java.util.Scanner;

public final class AuthInput {

    public static AuthCredentials promptCredentials(Scanner scanner) {
        String login = promptLogin(scanner);
        return promptPassword(scanner, login);
    }

    public static String promptLogin(Scanner scanner) {
        while (true) {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            if (!login.isEmpty()) {
                return login;
            }
            System.out.println("Login cannot be empty.");
        }
    }

    public static AuthCredentials promptPassword(Scanner scanner, String login) {
        while (true) {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (password == null || password.isEmpty()) {
                System.out.println("Password cannot be empty.");
                continue;
            }
            String hash = PasswordHasher.hashToHex(password);
            return new AuthCredentials(login, hash);
        }
    }
}

