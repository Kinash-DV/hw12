package dv.kinash.hw12.exceptions;

public class ItemPriceNotFoundException extends RuntimeException{
    public ItemPriceNotFoundException(String message) {
        super(message);
    }
    public ItemPriceNotFoundException() {
        super();
    }
}
