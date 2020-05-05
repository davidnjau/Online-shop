package com.centafrique.homework;

public class Products {

    String name;
    String price;
    String main_image;

    public Products(String name, String price, String main_image) {
        this.name = name;
        this.price = price;
        this.main_image = main_image;
    }

    public Products() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMain_image() {
        return main_image;
    }

    public void setMain_image(String main_image) {
        this.main_image = main_image;
    }
}
