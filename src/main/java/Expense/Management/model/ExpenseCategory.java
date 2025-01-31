package Expense.Management.model;

public enum ExpenseCategory {

    HOUSING, TRANSPORTATION, FOOD_and_DINING, HEALTH_and_FITNESS, ENTERTAINMENT, EDUCATION, SHOPPING, SAVINGS_and_INVESTMENTS, INSURANCE, MISCELLANEOUS;

    public static String getAllowedCategories() {
        return String.join(", ", HOUSING.name(), TRANSPORTATION.name(), FOOD_and_DINING.name(),
                HEALTH_and_FITNESS.name(), ENTERTAINMENT.name(), EDUCATION.name(), SHOPPING.name(), SAVINGS_and_INVESTMENTS.name(),
                INSURANCE.name(), MISCELLANEOUS.name());
    }
}
