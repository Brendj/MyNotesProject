package ru.axetta.ecafe.processor.web.partner.meals.security;

public class MealsInvalidToken extends Exception {
    public MealsInvalidToken() {
        super();
    }

    public MealsInvalidToken(String message) {
        super(message);
    }
}
