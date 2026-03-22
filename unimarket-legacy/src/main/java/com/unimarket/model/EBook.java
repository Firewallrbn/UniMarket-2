package com.unimarket.model;

/**
 * Producto digital de tecnología: un libro electrónico.
 */
public class EBook extends Product implements IDigitalProduct {

    private String downloadLink;
    private double fileSize;

    public EBook() {
        super();
    }

    public EBook(String name, double price) {
        super(name, price, "Tecnología");
        this.downloadLink = "https://unimarket.com/downloads/" + name.toLowerCase().replace(" ", "-");
        this.fileSize = 5.0;
    }

    public EBook(int id, String name, double price) {
        super(id, name, price, "Tecnología");
        this.downloadLink = "https://unimarket.com/downloads/" + name.toLowerCase().replace(" ", "-");
        this.fileSize = 5.0;
    }

    @Override
    public String getProductType() {
        return "EBook";
    }

    @Override
    public String getDownloadLink() {
        return downloadLink;
    }

    @Override
    public double getFileSize() {
        return fileSize;
    }
}
