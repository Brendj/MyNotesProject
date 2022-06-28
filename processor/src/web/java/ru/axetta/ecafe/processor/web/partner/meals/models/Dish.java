package ru.axetta.ecafe.processor.web.partner.meals.models;


import java.util.Objects;

/**
 * Блюдо.
 */
public class Dish {
    private Long id = null;
    private String code = null;
    private String name = null;
    private Long price = null;
    private String ingredients = null;
    private Integer calories = null;
    private String weight = null;
    private Integer protein = null;
    private Integer fat = null;
    private Integer carbohydrates = null;
    public Dish id(Long id) {
        this.id = id;
        return this;
    }



    /**
     * Идентификатор блюда.
     * @return id
     **/
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Dish code(String code) {
        this.code = code;
        return this;
    }



    /**
     * Код блюда.
     * @return code
     **/
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Dish name(String name) {
        this.name = name;
        return this;
    }



    /**
     * Название блюда.
     * @return name
     **/
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Dish price(Long price) {
        this.price = price;
        return this;
    }



    /**
     * Цена блюда в копейках.
     * @return price
     **/
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public Dish ingredients(String ingredients) {
        this.ingredients = ingredients;
        return this;
    }



    /**
     * Состав блюда.
     * @return ingredients
     **/
    public String getIngredients() {
        return ingredients;
    }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    public Dish calories(Integer calories) {
        this.calories = calories;
        return this;
    }



    /**
     * Калорийность блюда.
     * @return calories
     **/
    public Integer getCalories() {
        return calories;
    }
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    public Dish weight(String weight) {
        this.weight = weight;
        return this;
    }



    /**
     * Масса блюда в граммах.
     * @return weight
     **/
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }
    public Dish protein(Integer protein) {
        this.protein = protein;
        return this;
    }



    /**
     * Белки.
     * @return protein
     **/
    public Integer getProtein() {
        return protein;
    }
    public void setProtein(Integer protein) {
        this.protein = protein;
    }
    public Dish fat(Integer fat) {
        this.fat = fat;
        return this;
    }



    /**
     * Жиры.
     * @return fat
     **/
    public Integer getFat() {
        return fat;
    }
    public void setFat(Integer fat) {
        this.fat = fat;
    }
    public Dish carbohydrates(Integer carbohydrates) {
        this.carbohydrates = carbohydrates;
        return this;
    }



    /**
     * Углеводы.
     * @return carbohydrates
     **/
    public Integer getCarbohydrates() {
        return carbohydrates;
    }
    public void setCarbohydrates(Integer carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dish dish = (Dish) o;
        return Objects.equals(this.id, dish.id) &&
                Objects.equals(this.code, dish.code) &&
                Objects.equals(this.name, dish.name) &&
                Objects.equals(this.price, dish.price) &&
                Objects.equals(this.ingredients, dish.ingredients) &&
                Objects.equals(this.calories, dish.calories) &&
                Objects.equals(this.weight, dish.weight) &&
                Objects.equals(this.protein, dish.protein) &&
                Objects.equals(this.fat, dish.fat) &&
                Objects.equals(this.carbohydrates, dish.carbohydrates);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, code, name, price, ingredients, calories, weight, protein, fat, carbohydrates);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Dish {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    ingredients: ").append(toIndentedString(ingredients)).append("\n");
        sb.append("    calories: ").append(toIndentedString(calories)).append("\n");
        sb.append("    weight: ").append(toIndentedString(weight)).append("\n");
        sb.append("    protein: ").append(toIndentedString(protein)).append("\n");
        sb.append("    fat: ").append(toIndentedString(fat)).append("\n");
        sb.append("    carbohydrates: ").append(toIndentedString(carbohydrates)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
