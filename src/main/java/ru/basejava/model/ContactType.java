package ru.basejava.model;

public enum ContactType {
    MOBILE_PHONE("Мобильный телефон"),
    MAIL("Электронная почта"),
    TELEGRAM("Телеграм аккаунт"),
    LINKEDIN("Профиль LinkedIn"),
    GITHUB("Профиль GitHub"),
    STACKOVERFLOW("Профиль StackOverFlow");

    private String title;

    ContactType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
