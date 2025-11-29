package sample;

/**
 * Represents a Cat
 */
public class Cat extends Animal implements Mammal, Pet {
    private int livesRemaining;
    private boolean isIndoor;

    public Cat(String name, int age) {
        super(name, age);
        this.species = "Felis catus";
        this.livesRemaining = 9;
        this.isIndoor = true;
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " says: Meow!");
    }

    @Override
    public void feedMilk() {
        System.out.println("Cat is feeding milk to kittens");
    }

    @Override
    public boolean hasHair() {
        return true;
    }

    @Override
    public void play() {
        System.out.println(getName() + " is playing with yarn");
    }

    @Override
    public void showAffection() {
        System.out.println(getName() + " is purring");
    }

    public void scratch() {
        System.out.println(getName() + " is scratching");
    }

    public void loseLife() {
        if (livesRemaining > 0) {
            livesRemaining--;
            System.out.println(getName() + " has " + livesRemaining + " lives remaining");
        }
    }

    public int getLivesRemaining() {
        return livesRemaining;
    }

    public boolean isIndoor() {
        return isIndoor;
    }

    public void setIndoor(boolean indoor) {
        isIndoor = indoor;
    }
}
