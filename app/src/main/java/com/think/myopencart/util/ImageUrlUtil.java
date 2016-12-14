package com.think.myopencart.util;

public class ImageUrlUtil {

    private static final String IMAGE_BASE_URL = "http://www.storedoor.in/image/cache/";

    public static String getImage270(String url) {
        return getImage(url, "-270x270");
    }

    public static String getImage600x422(String url) {
        return getImage(url, "-600x422");
    }

    private static String getImage(String url, String dimen) {
        if (null != url && url != "false") {
            try {
                StringBuilder productUrl = new StringBuilder(url);
                productUrl.insert(url.indexOf("."), dimen);
                return IMAGE_BASE_URL.concat(productUrl.toString());
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

}
