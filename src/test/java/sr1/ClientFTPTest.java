package sr1;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Cette classe permet de tester les différentes méthode de la calsse ClientFTP
 *
 * @author pochet
 */
public class ClientFTPTest {
    /**
     * Cette méthode permet de tester la méthode getPassword()
     */
    @Test
    public void testGetPassword() {
        String password = "test";

        ClientFTP client = new ClientFTP("webtp.fil.univ-lille1.fr");
        client.setPassword(password);

        assertEquals(client.getPassword(), password);
    }

    /**
     * Cette méthode permet de tester la méthode getLogin()
     */
    @Test
    public void testGetLogin() {
        String login = "test";

        ClientFTP client = new ClientFTP("webtp.fil.univ-lille1.fr");
        client.setLogin(login);

        assertEquals(client.getLogin(), login);
    }

    /**
     * Cette méthode permet de tester la méthode connect(), elle doit nous retourner une exception ConnectionException car l'url n'est pas bonne.
     */
    @Test(expected = ConnectionException.class)
    public void testConnectURLInvalide() throws IOException, ConnectionException {
        String login = "pochet";
        String password = "test";

        ClientFTP client = new ClientFTP("ftp.test.fr");
        client.setLogin(login);
        client.setPassword(password);

        client.connect();
    }

    /**
     * Cette méthode permet de tester la méthode connect(), elle doit nous retourner une exception ConnectionException car le login et le password ne sont pas corrects.
     */
    @Test(expected = ConnectionException.class)
    public void testConnectInvalide() throws IOException, ConnectionException {
        String login = "test";
        String password = "test";

        ClientFTP client = new ClientFTP("ftp.ubuntu.com");
        client.setLogin(login);
        client.setPassword(password);

        client.connect();
    }

    /**
     * Cette méthode permet de tester la méthode connect(), elle doit se connecter correctement au serveurFTP
     */
    @Test
    public void testConnectValide() throws IOException, ConnectionException {
        String login = "anonymous";
        String password = "anonymous";

        ClientFTP client = new ClientFTP("ftp.ubuntu.com");
        client.setLogin(login);
        client.setPassword(password);

        client.connect();
    }

    /**
     * Cette méthode permet de tester la méthode getDataSock(), elle doit se connecter correctement au serveurFTP et réussir a créer la socket.
     */
    @Test
    public void testGetDataSock() throws IOException, ConnectionException {
        String login = "anonymous";
        String password = "anonymous";

        ClientFTP client = new ClientFTP("ftp.ubuntu.com");
        client.setLogin(login);
        client.setPassword(password);

        client.connect();

        client.getDataSock();
    }
}
