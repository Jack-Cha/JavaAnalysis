package sample;

/**
 * Abstract base class for all animals
 */
public abstract class Animal {
    private String name;
    private int age;
    protected String species;

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public abstract void makeSound();

    public void eat(Food food) {
        System.out.println(name + " is eating " + food.getName());
    }

    public void sleep() {
        System.out.println(name + " is sleeping");
    }
}
