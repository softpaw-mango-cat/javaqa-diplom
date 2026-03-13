package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;

    public class DataHelper {
        private static final Faker FAKER = new Faker(new Locale("en"));

        private DataHelper() {
        }

        public static String getValidApprovedCardNumber() {
            return "4444 4444 4444 4441";}

        public static String getValidDeclinedCardNumber() {
            return "4444 4444 4444 4442";}

        public static String generateValidMonth() {
            // валидные значения месяца от 01 до 12
            int month = FAKER.number().numberBetween(1, 13);
            return String.format("%02d", month);
        }

        public static String generateInvalidMonth() {
            // невалидные значения месяца от 13 до 99 (00 надо будет отдельно проверять)
            int month = FAKER.number().numberBetween(13, 100);
            return String.format("%02d", month);
        }

        public static String generateValidYear() {
            // валидный год - от 2026, то есть год действия карты
            int month = FAKER.number().numberBetween(26, 30);
            return String.format("%02d", month);
        }

        public static String generateInvalidYear() {
            // невалидный год - всё что до 2026, то есть
            // от 00 до 25
            int month = FAKER.number().numberBetween(0, 26);
            return String.format("%02d", month);
        }

        public static String generateRandomName() {
            // просто имя на латинице
           return FAKER.name().fullName();
        }

        public static String generateRandomCVC() {
            // просто код из трех цифр
            return FAKER.numerify("###");
        }

        public static CardInfo generateValidApprovedCard() {
            return new CardInfo(getValidApprovedCardNumber(),generateValidMonth(),
                    generateValidYear(), generateRandomName(),generateRandomCVC());
        }

        public static CardInfo generateValidDeclinedCard() {
            return new CardInfo(getValidDeclinedCardNumber(),generateValidMonth(),
                    generateValidYear(), generateRandomName(),generateRandomCVC());
        }

        // методы для генерации карт с одним параметром - для проверки валидации полей
        public static CardInfo generateCardWithNumber(String cardNumber) {
            return new CardInfo(cardNumber,generateValidMonth(),
                    generateValidYear(), generateRandomName(),generateRandomCVC());
        }

        public static CardInfo generateCardWithMonth(String month) {
            return new CardInfo(getValidApprovedCardNumber(),month,
                    generateValidYear(), generateRandomName(),generateRandomCVC());
        }

        public static CardInfo generateCardWithYear(String year) {
            return new CardInfo(getValidApprovedCardNumber(),generateValidMonth(),
                    year, generateRandomName(),generateRandomCVC());
        }

        public static CardInfo generateCardWithOwner(String owner) {
            return new CardInfo(getValidApprovedCardNumber(),generateValidMonth(),
                    generateValidYear(),owner, generateRandomCVC());
        }

        public static CardInfo generateCardWithCVC(String cvc) {
            return new CardInfo(getValidApprovedCardNumber(),generateValidMonth(),
                    generateValidYear(), generateRandomName(), cvc);
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
