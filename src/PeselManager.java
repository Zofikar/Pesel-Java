import java.io.*;
import java.util.*;

public class PeselManager {

    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static boolean isSameDay(Pesel val1, Pesel val2) {
        return val1.day == val2.day && val1.month == val2.month && val1.year == val2.year;
    }

    public static IndexedPesel parsePesel(String peselStr) {
        String[] parts = peselStr.split(" ");
        int index = Integer.parseInt(parts[0]);
        String peselDigits = parts[1];

        int year = Integer.parseInt(peselDigits.substring(0, 2));
        int month = Integer.parseInt(peselDigits.substring(2, 4));
        int day = Integer.parseInt(peselDigits.substring(4, 6));
        int ordinalNumber = Integer.parseInt(peselDigits.substring(6, 9));
        int genderDigit = Integer.parseInt(peselDigits.substring(9, 10));
        int controlDigit = Integer.parseInt(peselDigits.substring(10, 11));

        if (month > 60) {
            month -= 60;
            year += 2200;
        } else if (month > 40) {
            month -= 40;
            year += 2100;
        } else if (month > 20) {
            month -= 20;
            year += 2000;
        }

        Pesel pesel = new Pesel(day, month, year, ordinalNumber, genderDigit, controlDigit);
        return new IndexedPesel(index, pesel);
    }

    private static int daysInMonth(int month, int year) {
        if (month == 2) {
            if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) return 29;
            return 28;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) return 30;
        return 31;
    }

    private static boolean validatePesel(Pesel pesel) {
        if (pesel.year < 1900 || pesel.year >= 2300) return false;
        if (pesel.month < 1 || pesel.month > 12) return false;
        if (pesel.day < 1 || pesel.day > daysInMonth(pesel.month, pesel.year)) return false;
        return computeControlDigit(pesel) == pesel.controlDigit;
    }

    public static String stringifyPesel(IndexedPesel indexedPesel) {
        Pesel pesel = indexedPesel.pesel;
        int monthOffset = 0;
        if (pesel.year >= 2200) monthOffset = 60;
        else if (pesel.year >= 2100) monthOffset = 40;
        else if (pesel.year >= 2000) monthOffset = 20;

        return String.format("%d %02d%02d%02d%03d%01d%01d", indexedPesel.index,
                pesel.year % 100, pesel.month + monthOffset, pesel.day,
                pesel.ordinalNumber, pesel.genderDigit, pesel.controlDigit);
    }

    public static boolean isOlder(Pesel val1, Pesel val2) {
        if (val1.year != val2.year) return val1.year > val2.year;
        if (val1.month != val2.month) return val1.month > val2.month;
        return val1.day > val2.day;
    }

    public static boolean isSameAge(Pesel val1, Pesel val2) {
        return val1.year == val2.year && val1.month == val2.month && val1.day == val2.day;
    }

    public static boolean isFemale(Pesel pesel) {
        return isEven(pesel.genderDigit);
    }

    public static int genderCompare(Pesel val1, Pesel val2) {
        boolean af = isFemale(val1);
        boolean bf = isFemale(val2);
        if (af == bf) return 0;
        return af ? -1 : 1;
    }

    public static int order(IndexedPesel val1, IndexedPesel val2) {
        if (isOlder(val1.pesel, val2.pesel)) return -1;
        if (isSameAge(val1.pesel, val2.pesel)) {
            int rs = genderCompare(val1.pesel, val2.pesel);
            return rs != 0 ? rs : Integer.compare(val1.index, val2.index);
        }
        return 1;
    }

    public static int computeControlDigit(Pesel pesel) {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;

        String peselStr = String.format("%02d%02d%02d%03d%01d",
                pesel.year % 100, pesel.month, pesel.day,
                pesel.ordinalNumber, pesel.genderDigit);

        for (int i = 0; i < 10; i++) {
            sum += (peselStr.charAt(i) - '0') * weights[i];
        }

        return sum % 10;
    }

    private static int getYear() {
        System.out.println("Podaj czterocyfrowy rok urodzenia, np. 1993.");
        Scanner scanner = new Scanner(System.in);
        int year = scanner.nextInt();
        if (year < 1900 || year > 2299) {
            System.out.println("Podałeś złą formę roku, wciśnij p jeśli chcesz podać ponownie rok lub wciśnij inny klawisz jeśli chcesz zakończyć");
            if (checkButtonPress('p')) {
                return getYear();
            } else {
                System.exit(0);
            }
        }
        return year;
    }

    private static int getMonth() {
        System.out.println("Podaj miesiąc urodzenia, np. czerwiec albo 6.");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        String[] monthNames = {"", "styczeń", "luty", "marzec", "kwiecień", "maj", "czerwiec", "lipiec", "sierpień", "wrzesień", "październik", "listopad", "grudzień"};
        int month = 0;

        try {
            month = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            for (int i = 1; i < monthNames.length; i++) {
                if (monthNames[i].equalsIgnoreCase(input)) {
                    month = i;
                    break;
                }
            }
        }

        if (month < 1 || month > 12) {
            System.out.println("Nieprawidłowy miesiąc. Wciśnij p jeśli chcesz podać ponownie, lub inny klawisz by zakończyć.");
            if (checkButtonPress('p')) {
                return getMonth();
            } else {
                System.exit(0);
            }
        }
        return month;
    }

    private static int getDay(int maxDay) {
        System.out.printf("Podaj dzień urodzenia (1-%d):%n", maxDay);
        Scanner scanner = new Scanner(System.in);
        int day = scanner.nextInt();
        if (day < 1 || day > maxDay) {
            System.out.println("Nieprawidłowy dzień. Wciśnij p jeśli chcesz podać ponownie, lub inny klawisz by zakończyć.");
            if (checkButtonPress('p')) {
                return getDay(maxDay);
            } else {
                System.exit(0);
            }
        }
        return day;
    }

    private static boolean checkButtonPress(char expectedChar) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        return input.length() == 1 && input.charAt(0) == expectedChar;
    }

    private static boolean isMale() {
        System.out.println("Podaj płeć: k - kobieta, m - mężczyzna.");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("m")) return true;
        if (input.equals("k")) return false;
        System.out.println("Nieprawidłowa płeć. Wciśnij p jeśli chcesz podać ponownie, lub inny klawisz by zakończyć.");
        if (checkButtonPress('p')) {
            return isMale();
        } else {
            System.exit(0);
        }
        return false; // unreachable
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        List<IndexedPesel> pesels = new ArrayList<>();

        // Load data from file
        File file = new File("pesel.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    IndexedPesel indexedPesel = parsePesel(line);
                    if (validatePesel(indexedPesel.pesel))
                        pesels.add(indexedPesel);
                }
            }
        }

        boolean continueAdding;
        do {
            Pesel newPesel = new Pesel();
            System.out.print("Enter year of birth (4 digits): ");
            newPesel.year = scanner.nextInt();

            System.out.print("Enter month of birth (1-12): ");
            newPesel.month = scanner.nextInt();

            System.out.print("Enter day of birth: ");
            newPesel.day = scanner.nextInt();

            System.out.print("Enter gender (m/f): ");
            char gender = scanner.next().charAt(0);
            boolean isFemale = (gender == 'f');

            newPesel.ordinalNumber = pesels.stream()
                    .filter(p -> isSameDay(p.pesel, newPesel) && isFemale(p.pesel) == isFemale)
                    .mapToInt(p -> 1)
                    .sum() * 2 + (isFemale ? 0 : 1);

            newPesel.genderDigit = newPesel.ordinalNumber % 10;
            newPesel.ordinalNumber /= 10;
            newPesel.controlDigit = computeControlDigit(newPesel);

            System.out.printf("Czy chcesz dokonać wpisu %4d, %d, %d, %c? Klawisz t – tak, pozostałe – nie.%n",
                    newPesel.year, newPesel.month, newPesel.day, isEven(newPesel.genderDigit) ? 'k' : 'm');

            if (scanner.next().equalsIgnoreCase("t"))
                pesels.add(new IndexedPesel(pesels.size(), newPesel));

            if (scanner.next().equalsIgnoreCase("t")){
                pesels.add(new IndexedPesel(pesels.size(), newPesel));
                pesels.sort(PeselManager::order);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (IndexedPesel indexedPesel : pesels) {
                        writer.write(stringifyPesel(indexedPesel));
                        writer.newLine();
                    }
                }
            }

            System.out.print("Do you want to add another entry? (y/n): ");
            continueAdding = scanner.next().equalsIgnoreCase("y");

        } while (continueAdding);



        scanner.close();
    }
}