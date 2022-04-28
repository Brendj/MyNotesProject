package ru.axetta.ecafe.processor.web.ui.org;

public class FoodBoxParallelUI {
    private String nameParallel;
    private Integer parallel;
    private boolean available;

    FoodBoxParallelUI()
    {

    }

    FoodBoxParallelUI(String nameParallel, boolean available, Integer parallel)
    {
        this.nameParallel = nameParallel;
        this.available = available;
        this.parallel = parallel;
    }

    public String getNameParallel() {
        return nameParallel;
    }

    public void setNameParallel(String nameParallel) {
        this.nameParallel = nameParallel;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Integer getParallel() {
        return parallel;
    }

    public void setParallel(Integer parallel) {
        this.parallel = parallel;
    }
}
