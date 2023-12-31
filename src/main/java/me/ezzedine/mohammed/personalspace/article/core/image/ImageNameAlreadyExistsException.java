package me.ezzedine.mohammed.personalspace.article.core.image;

public class ImageNameAlreadyExistsException extends Exception {
    public ImageNameAlreadyExistsException(String name) {
        super(String.format("Image name %s already exists.", name));
    }
}
