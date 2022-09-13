package sr1;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Cette classe permet de tester les différentes méthode de la calsse FileFTP
 *
 * @author pochet
 */
public class FileFTPTest {
    /**
     * Cette méthode permet de tester la méthode isDirectory()
     */
    @Test
    public void testIsNotDirectory() {
        String file = "-rw-r--r--    1 997      997          5793 Jan 15  2021 FOOTER.html";

        FileFTP fileFTP = new FileFTP(file);

        assertFalse(fileFTP.isDirectory());
    }

    /**
     * Cette méthode permet de tester la méthode isDirectory()
     */
    @Test
    public void testIsDirectory() {
        String file = "drwxr-xr-x   27 997      997          4096 Jan 16 09:13 cloud-images";

        FileFTP fileFTP = new FileFTP(file);

        assertTrue(fileFTP.isDirectory());
    }

    /**
     * Cette méthode permet de tester la méthode isPrintable()
     */
    @Test
    public void testIsPrintable() {
        String file = "drwxr-xr-x   27 997      997          4096 Jan 16 09:13 cloud-images";

        FileFTP fileFTP = new FileFTP(file);

        assertTrue(fileFTP.isPrintable());
    }

    /**
     * Cette méthode permet de tester la méthode isPrintable()
     */
    @Test
    public void testIsNotPrintable() {
        String file = "lrwxr-xr-x   27 997      997          4096 Jan 16 09:13 .";

        FileFTP fileFTP = new FileFTP(file);

        assertFalse(fileFTP.isPrintable());
    }

    /**
     * Cette méthode permet de tester la méthode getFilename()
     */
    @Test
    public void testGetFilename() {
        String file = "drwxr-xr-x   27 997      997          4096 Jan 16 09:13 cloud-images";

        FileFTP fileFTP = new FileFTP(file);

        assertEquals(fileFTP.getFilename(), "cloud-images");
    }
}
