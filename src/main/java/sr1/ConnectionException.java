package sr1;

/**
 * Cette classe permet de relever les exceptions lors de la connexion du client au serveur FTP
 *
 * @author pochet
 */
public class ConnectionException extends Exception {

    public ConnectionException() {
        super();
    }

    public ConnectionException(String msg) {
        super(msg);
    }
}
