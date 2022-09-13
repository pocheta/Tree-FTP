package sr1;

/***
 * Cette Classe permet de modéliser les fichiers obtenu par la commande FTP 'LIST'.
 * @author pochet.
 */
public class FileFTP {

    private String filename;
    private char type;

    /**
     * Ce constructeur permet de créer FileFTP
     *
     * @param f représente le string du fichier que l'on recueille via la socket
     */
    public FileFTP(String f) {
        String[] infoFiles = f.split(" ");
        this.filename = infoFiles[infoFiles.length - 1];
        this.type = infoFiles[0].charAt(0);
    }

    /**
     * @return true ou false si le fichier est un repertoire et si ce n'est pas l'un des deux repertoire :(.ou ..)
     */
    public boolean isDirectory() {
        return type == 'd' && isPrintable();
    }

    /**
     * @return true ou false si le fichier n'est pas l'un des deux repertoire : (. ou ..)
     */
    public boolean isPrintable() {
        return !filename.equals(".") && !filename.equals("..") && type != 'l';
    }

    public String getFilename() {
        return filename;
    }

    public char getType() {
        return type;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setType(char type) {
        this.type = type;
    }
}
