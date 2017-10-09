package com.prathab.shopping.model;

import java.util.ArrayList;

public class ProductsModel {
  private String id;
  private String name;
  private String price;
  private int rating;
  private ArrayList<String> images = new ArrayList<>();
  private ArrayList<String> tags = new ArrayList<>();
  private String description;

  public ProductsModel() {
  }

  public ProductsModel(String id, String name, String price, int rating, ArrayList<String> images,
      ArrayList<String> tags, String description) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.rating = rating;
    this.images = images;
    this.tags = tags;
    this.description = description;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProductsModel that = (ProductsModel) o;

    return id.equals(that.id);
  }

  @Override public int hashCode() {
    return id.hashCode();
  }

  @Override public String toString() {
    return "ProductsModel{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", price='"
        + price
        + '\''
        + ", rating="
        + rating
        + ", images="
        + images
        + ", tags="
        + tags
        + ", description='"
        + description
        + '\''
        + '}';
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public ArrayList<String> getImages() {
    return images;
  }

  public void setImages(ArrayList<String> images) {
    this.images = images;
  }

  public ArrayList<String> getTags() {
    return tags;
  }

  public void setTags(ArrayList<String> tags) {
    this.tags = tags;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
