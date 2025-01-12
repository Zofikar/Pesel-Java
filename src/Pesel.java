public class Pesel {
    int day; // 2 digits
    int month; // 2 digits
    int year; // 2 digits
    int ordinalNumber; // 3 digits
    int genderDigit; // 1 digit
    int controlDigit; // 1 digit

    public Pesel() {}

    public Pesel(int day, int month, int year, int ordinalNumber, int genderDigit, int controlDigit) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.ordinalNumber = ordinalNumber;
        this.genderDigit = genderDigit;
        this.controlDigit = controlDigit;
    }
}