package client.utilities;

import common.models.Coordinates;
import common.models.MusicBand;
import common.models.MusicGenre;
import common.models.Studio;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

public class MusicBandValidator {

    private Scanner scanner;

    public MusicBandValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public MusicBand askMusicBand() {
        String name = askName();
        Coordinates coordinates = askCoordinates();
        Long numOfParts = askNumberOfParticipants();
        Integer singlesCount = askSinglesCount();
        LocalDate establishmentDate = askEstablishmentDate();
        MusicGenre genre = askMusicGenre();
        Studio studio = askStudio();

        return new MusicBand(name, coordinates, numOfParts, singlesCount, establishmentDate, genre, studio);
    }

    public String askName() {
        while (true) {
            System.out.print("Enter band name: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }
            return input;
        }
    }

    public Coordinates askCoordinates() {
        return new Coordinates(askX(), askY());
    }

    public double askX() {
        while (true) {
            System.out.print("Enter X Coordinate (Must be > -431): ");
            try {
                double x = Double.parseDouble(scanner.nextLine().trim());
                if (x <= -431) {
                    System.out.println("X must be greater than -431");
                    continue;
                }
                return x;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public Integer askY() {
        while (true) {
            System.out.print("Enter Y Coordinate (cannot be null): ");
            String y = scanner.nextLine().trim();
            if (y.isEmpty()) {
                System.out.println("Y cannot be null");
                continue;
            }
            try {
                return Integer.parseInt(y);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public Long askNumberOfParticipants() {
        while (true) {
            System.out.print("Enter number of participants: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Number of participants cannot be null");
                continue;
            }
            try {
                long value = Long.parseLong(input);
                if (value <= 0) {
                    System.out.println("Number of participants should be greater than 0");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public Integer askSinglesCount() {
        while (true) {
            System.out.print("Enter the count of singles: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    System.out.println("Number of singles must be greater than 0");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public LocalDate askEstablishmentDate() {
        while (true) {
            System.out.print("Enter date of establishment(yyyy-MM-dd or press Enter for null): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeException e) {
                System.out.println("Please enter date in correct format(yyyy-MM-dd).");
            }
        }
    }

    public MusicGenre askMusicGenre() {
        System.out.println("Available genres: " + Arrays.toString(MusicGenre.values()));
        while (true) {
            System.out.print("Enter music genre (or press Enter for null): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return MusicGenre.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter correct music genre. Available genres:\n" + Arrays.toString(MusicGenre.values()));
            }
        }
    }

    public Studio askStudio() {
        System.out.print("Enter name of the studio: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        return new Studio(input);
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}

