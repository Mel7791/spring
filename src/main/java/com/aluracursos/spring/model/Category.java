package com.aluracursos.spring.model;

public enum Category {
    ACCION("Action", "Accion"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIMEN("Crime", "Crimen");

    private String categoryOmdb;
    private String categoryEspanol;

    Category(String categoryOmdb, String categoryEspanol) {
        this.categoryOmdb = categoryOmdb;
        this.categoryEspanol = categoryEspanol;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.categoryOmdb.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException(("Ninguna categoria fue encontrada: " + text));
    }

    public static Category fromEspanol(String text) {
        for (Category category : Category.values()) {
            if (category.categoryEspanol.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException(("Ninguna categoria fue encontrada: " + text));
    }
}
