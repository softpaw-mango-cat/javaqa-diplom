package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    private static final Faker FAKER = new Faker(new Locale("en"));
    private static final Faker FAKER_RU = new Faker(new Locale("ru"));

    private DataHelper() {
    }

    public static String getValidApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getValidDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String generateCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateCurrentYear() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateInvalidMonth() {
        int month = FAKER.number().numberBetween(13, 100);
        return String.format("%02d", month);
    }

    public static String generatePastYear() {
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy");
        LocalDate dateMinus5Years = LocalDate.now().minusYears(5);
        return dateMinus5Years.format(yearFormatter);
    }

    public static String generateFutureYear() {
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yy");
        LocalDate datePlus5Years = LocalDate.now().plusYears(5);
        return datePlus5Years.format(yearFormatter);
    }

    public static String generateRandomLetter() {
        return FAKER.regexify("[A-Za-z]{1}");
    }

    public static String generateRandomLongString() {
        return FAKER.regexify("[A-Za-z]{100}");
    }

    public static String generateRandomEngName() {
        return FAKER.name().fullName();
    }

    public static String generateRandomRuName() {
        return FAKER_RU.name().fullName();
    }

    public static String generateRandomCVC() {
        return FAKER.numerify("###");
    }

    public static String generateRandomCardNumber() {
        return FAKER.numerify("################");
    }

    public static String generate15DigitCardNumber() {
        return FAKER.numerify("###############");
    }

    public static String generateRandomSymbols() {
        return FAKER.regexify("[!@#$%^&*()_+]{10}");
    }

    public static String generateRandomDigits() {
        return FAKER.regexify("[0-9]{10}");
    }

    public static String generateRandomOneDigit() {
        return FAKER.numerify("#");
    }

    public static String generateRandomTwoDigits() {
        return FAKER.numerify("##");
    }

    public static CardInfo generateValidApprovedCard() {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateValidDeclinedCard() {
        return new CardInfo(getValidDeclinedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    // методы для генерации карт с одним параметром - для проверки валидации полей
    public static CardInfo generateCardWithNumber(String cardNumber) {
        return new CardInfo(cardNumber, generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithEmptyNumber() {
        return new CardInfo("", generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithZeroNumber() {
        return new CardInfo("0000000000000000", generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithMonth(String month) {
        return new CardInfo(getValidApprovedCardNumber(), month,
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithEmptyMonth() {
        return new CardInfo(getValidApprovedCardNumber(), "",
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithZeroMonth() {
        return new CardInfo(getValidApprovedCardNumber(), "00",
                generateCurrentYear(), generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithYear(String year) {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                year, generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithEmptyYear() {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                "", generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithZeroYear() {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                "00", generateRandomEngName(), generateRandomCVC());
    }

    public static CardInfo generateCardWithOwner(String owner) {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), owner, generateRandomCVC());
    }

    public static CardInfo generateCardWithEmptyOwner() {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), "", generateRandomCVC());
    }

    public static CardInfo generateCardWithCVC(String cvc) {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), cvc);
    }

    public static CardInfo generateCardWithEmptyCVC() {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), "");
    }

    public static CardInfo generateCardWithZeroCVC() {
        return new CardInfo(getValidApprovedCardNumber(), generateCurrentMonth(),
                generateCurrentYear(), generateRandomEngName(), "000");
    }

    @Value
    public static class CardInfo {
        String number;
        String month;
        String year;
        String owner;
        String cvc;
    }
}
