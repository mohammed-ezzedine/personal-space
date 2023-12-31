package me.ezzedine.mohammed.personalspace.article.core.image;

public class ImageDoesNotExistException extends Exception {
    public ImageDoesNotExistException(String name) {
        super(String.format("Image '%s' does not exist in the storage", name));
    }
}
