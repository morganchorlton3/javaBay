package javaBay.listings;

import javafx.scene.image.Image;

import java.io.Serializable;

public class lotImage extends Image implements Serializable {
    public Image lotImage;

    public lotImage(String url, Image lotImage) {
        super(url);
        this.lotImage = lotImage;
    }

}
