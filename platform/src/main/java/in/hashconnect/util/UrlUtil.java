package in.hashconnect.util;

import com.twelvemonkeys.imageio.stream.URLImageInputStreamSpi;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

@Component
public class UrlUtil {

    public static Boolean checkShopify(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        int responseCode = huc.getResponseCode();
        return (responseCode==200);
    }

    public static boolean isImageCanBeRead(String url) throws FileNotFoundException, IOException {
        if(url==null)
            return false;
        try {
            ImageInputStream heicInput = createInputStreamInstance(new URL(url), false, null);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(heicInput);
            if (!readers.hasNext()) {
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }

    }

    public static ImageInputStream createInputStreamInstance(final Object input, final boolean useCacheFile,
                                                             final File cacheDir) throws IOException {
        if (input instanceof URL) {
            URL url = (URL) input;
            return new URLImageInputStreamSpi().createInputStreamInstance(url);
        }

        throw new IllegalArgumentException("Expected input of type URL: " + input);
    }
}
