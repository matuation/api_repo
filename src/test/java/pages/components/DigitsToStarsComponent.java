package pages.components;

public class DigitsToStarsComponent {
    public String digitsToStars(int digits) {
        int total = 5;

        return "★".repeat(digits) + "☆".repeat(total - digits);
    }
}
