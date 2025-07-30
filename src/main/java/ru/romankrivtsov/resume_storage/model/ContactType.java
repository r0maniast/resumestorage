package ru.romankrivtsov.resume_storage.model;

public enum ContactType {
    MOBILE_PHONE("Мобильный телефон"),
    MAIL("Электронная почта"),
    TELEGRAM("Телеграм аккаунт"),
    LINKEDIN("Профиль LinkedIn"),
    GITHUB("Профиль GitHub"),
    STACKOVERFLOW("Профиль StackOverFlow");

    private final String title;

    ContactType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String toHtml(String value){
        return (value== null) ? "" : title + ": " + value;
    }
}
