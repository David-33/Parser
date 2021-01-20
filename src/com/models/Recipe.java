package com.models;

import java.util.HashMap;
import java.util.Map;


public final class Recipe {

    public final String name;
    public final String[] categories;
    public final String time;
    public final int portion;
    public final float calories;
    public final float proteins;
    public final float fats;
    public final float carbohydrates;

    private Map<String, String> ingredients = new HashMap<>();

    private Recipe(Builder builder) {
        name = builder.name;
        categories = builder.categories;
        time = builder.time;
        portion = builder.portion;
        calories = builder.calories;
        proteins = builder.proteins;
        fats = builder.fats;
        carbohydrates = builder.carbohydrates;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String[] categories;
        private String time;
        private int portion;

        private float calories;
        private float proteins;
        private float fats;
        private float carbohydrates;

        private Builder name(String val) {
            name = val;
            return this;
        }

        public Builder categories(String[] val) {
            categories = val;
            return this;
        }

        public Builder setTime(String val) {
            time = val;
            return this;
        }

        public Builder setPortion(int val) {
            portion = val;
            return this;
        }

        public Builder setCalories(float val) {
            calories = val;
            return this;
        }

        public Builder setProteins(float val) {
            proteins = val;
            return this;
        }

        public Builder setFats(float val) {
            fats = val;
            return this;
        }

        public Builder setCarbohydrates(float val) {
            carbohydrates = val;
            return this;
        }
    }

}
