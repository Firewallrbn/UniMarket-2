package com.unimarket.model;

/**
 * Producto digital de útiles escolares: un PDF de curso.
 */
public class CoursePDF extends Product implements IDigitalProduct {

    private String downloadLink;
    private double fileSize;

    public CoursePDF() {
        super();
    }

    public CoursePDF(String name, double price) {
        super(name, price, "Útiles Escolares");
        this.downloadLink = "https://unimarket.com/courses/" + name.toLowerCase().replace(" ", "-");
        this.fileSize = 2.5;
    }

    public CoursePDF(int id, String name, double price) {
        super(id, name, price, "Útiles Escolares");
        this.downloadLink = "https://unimarket.com/courses/" + name.toLowerCase().replace(" ", "-");
        this.fileSize = 2.5;
    }

    @Override
    public String getProductType() {
        return "CoursePDF";
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
