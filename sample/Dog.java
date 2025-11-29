package sample;

/**
 * Represents a Dog
 */
public class Dog extends Animal implements Mammal, Pet {
    private String breed;
    private boolean isTrained;

    public Dog(String name, int age, String breed) {
        super(name, age);
        this.breed = breed;
        this.species = "Canis familiaris";
        this.isTrained = false;
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " says: Woof! Woof!");
    }

    @Override
    public void feedMilk() {
        System.out.println("Dog is feeding milk to puppies");
    }

    @Override
    public boolean hasHair() {
        return true;
    }

    @Override
    public void play() {
        System.out.println(getName() + " is playing fetch");
    }

    @Override
    public void showAffection() {
        System.out.println(getName() + " is wagging tail");
    }

    public void train(String command) {
        System.out.println("Training " + getName() + " to " + command);
        isTrained = true;
    }

    public String getBreed() {
        return breed;
    }

    public boolean isTrained() {
        return isTrained;
    }
}
