package sample;

/**
 * Represents food for animals
 */
public class Food {
    private String name;
    private FoodType type;
    private double calories;

    public Food(String name, FoodType type, double calories) {
        this.name = name;
        this.type = type;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public FoodType getType() {
        return type;
    }

    public double getCalories() {
        return calories;
    }
}
