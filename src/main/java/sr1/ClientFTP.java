package sr1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cette classe permet de communiquer avec un serveur FTP
 * La classe contient tous les méthodes permettant de communiquer avec le serveur
 * L'objectif, une fois connecté au serveur FTP est d'utiliser la méthode listDirectories pour lister touts les répertoires et fichier du serveur.
 *
 * @author pochet
 */
public class ClientFTP {

    private String urlServer;
    private String login;
    private String password;
    private int port;
    private BufferedReader reader;
    private PrintWriter printer;
    private String line;
    private String tab;
    private int numberDirectoryRead;
    private int numberFileRead;

    /**
     * Ce constructeur permet de créer ClientFTP avec en paramètre l'URL du serveur FTP que l'on souhaite lister.
     *
     * @param url correspond au serveur FTP que l'on souhaite lister
     */
    public ClientFTP(String url) {
        this.urlServer = url;
        this.login = "";
        this.password = "";
        this.port = 21;
        this.line = "";
        this.tab = "    ";
        this.numberDirectoryRead = 0;
        this.numberFileRead = 0;
    }

    /**
     * Cette méthode permet de se connecter puis de s'authentifier au serveur FTP
     *
     * @throws IOException
     * @throws ConnectionException
     */
    public void connect() throws ConnectionException, IOException {
        try {
            // Création de la socket, du reader et du printer
            Socket socket = new Socket(urlServer, port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new ConnectionException("Erreur de la connexion au serveur FTP : " + e.getMessage());
        }

        //Si le FTP nous retourne une ligne qui commence par 220, cela veut dire que nous sommes bien connecté au serveur FTP
        if (reader.readLine().startsWith("220")) {
            System.out.println("Connexion au serveur FTP réussi");
        } else {
            throw new ConnectionException("Erreur de la connexion au serveur FTP");
        }
        // On set le login
        printer.println("USER " + login);
        reader.readLine();

        // On set le password
        printer.println("PASS " + password);

        //Si le FTP nous retourne une ligne qui commence par 230, cela veut dire que nous sommes bien loggé au serveur FTP
        if (reader.readLine().startsWith("230")) {
            System.out.println("Authentification au serveur FTP réussi");
        } else {
            throw new ConnectionException("Erreur de l'authentification au serveur FTP");
        }
    }

    /**
     * @return Cette methode retourne le chemin du repertoire courant grâce à la commande 'PWD'
     * @throws IOException
     */
    public String currentDirectory() throws IOException {
        printer.println("PWD ");
        String currentDir = reader.readLine().split(" ")[1].replace("\"", "");
        if (currentDir.equals("/")) return "";
        else return currentDir;
    }

    /**
     * Cette methode permet de changer de repertoire, grâce à la commande 'CWD'.
     *
     * @param directoryName    est le nom du repertoire où le vont accèder
     * @param currentDirectory et le repertoire courant.
     * @return elle retourne true si elle a réussi a changer de réportoire ou false si non
     * @throws IOException
     */
    public boolean changeDirectory(String directoryName, String currentDirectory) throws IOException {
        String changeDir = "CWD " + currentDirectory + "/" + directoryName;
        printer.println(changeDir);
        String statusDirectory = reader.readLine();
        return Integer.parseInt(statusDirectory.split(" ")[0]) == 250;
    }

    /**
     * Cette méthode permet d'afficher l'arborescence du serveur FTP
     * C'est une méthode récursive, dès que l'on peut rentrer dans un répertoire alots elle s'appel récursivement
     *
     * @param hideFile boolean pour activer l'affichage des fichiers cachés.
     * @param depth entier qui défini la profondeur de l'arborescence
     * @param json boolean pour choisir entre l'affichage JSON (true) ou l'affichage classique
     * @throws IOException
     */
    public void listDirectories(boolean hideFile, int depth, boolean json) throws IOException {
        // Création de la socket pour récupérer les données du serveur FTP
        Socket dataSocket = this.getDataSock();
        BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

        // On envoie la méthode LIST ou LIST -a (pour les fichiers cachés) au serveur pour qu'il retourne les fichiers et dossier contenu
        if (hideFile) printer.println("LIST -a");
        else printer.println("LIST");

        reader.readLine();
        reader.readLine();

        //On créer les données dont on a besoin dans la boucle
        String tabTemp = tab;
        String lineTemp = line;
        String files = dataReader.readLine();
        String currentDirectory = currentDirectory();

        //Tant que ce le file que le socket nous envois est différent de null on continue
        while (files != null) {
            //On créer un FileFTP avec le string donnée par la socket
            FileFTP fileFTP = new FileFTP(files);
            files = dataReader.readLine();

            //Si le fichier est affichable (n'est pas l'un des deux repertoire : (. ou ..))
            if (fileFTP.isPrintable()) {
                if (files != null) {
                    //Si c'est un répertoire on l'affiche et on incrémente le compteur (meme chose pour les fichiers)
                    if (fileFTP.isDirectory()) {
                        numberDirectoryRead++;
                        if (json) System.out.println(tab + "{\"type\":\"directory\",\"name\":\"" + fileFTP.getFilename() + "\", \"contents\":[");
                        else System.out.println(line + "├── " + fileFTP.getFilename());
                    } else {
                        numberFileRead++;
                        if (json) System.out.println(tab + "{\"type\":\"file\",\"name\":\"" + fileFTP.getFilename() + "\"},");
                        else System.out.println(line + "├── " + fileFTP.getFilename());
                    }
                }else{
                    if (fileFTP.isDirectory()) {
                        numberDirectoryRead++;
                        if (json) System.out.println(tab + "{\"type\":\"directory\",\"name\":\"" + fileFTP.getFilename() + "\", \"contents\":[");
                        else System.out.println(line + "└── " + fileFTP.getFilename());
                    } else {
                        numberFileRead++;
                        if (json)  System.out.println(tab + "{\"type\":\"file\",\"name\":\"" + fileFTP.getFilename() + "\"}");
                        else System.out.println(line + "└── " + fileFTP.getFilename());
                    }
                }
            // Sinon c'est un lien symbolique et on l'affiche
            }else {
                if (files == null) {
                    if (json) System.out.println(tab + "{\"type\":\"directory\",\"name\":\"" + fileFTP.getFilename() + "\", \"contents\":[ \n" + tab + "]}");
                    else System.out.println(line + "└── "  + fileFTP.getFilename());
                }
                else {
                    if (json) System.out.println(tab + "{\"type\":\"directory\",\"name\":\"" + fileFTP.getFilename() + "\", \"contents\":[ \n" + tab + "]},");
                    else System.out.println(line + "├── "  + fileFTP.getFilename());
                }
            }

            //Si le File est un répertoire
            if (fileFTP.isDirectory()) {
                //Et si on réussi a rentrer dans le répetoire grâce à la commande 'CWD'
                if (changeDirectory(fileFTP.getFilename(), currentDirectory) && fileFTP.isPrintable()) {
                    // On fait appel a notre même fonction en récursif
                    line += "│   ";
                    tab += "    ";
                    if (depth != 0) listDirectories(hideFile, depth-1, json);
                    line = line.substring(0, line.length()-4);
                    tab = tab.substring(0, tab.length()-4);
                }
                if (json) {
                    if (files != null) System.out.println(tab + " ]},");
                    else System.out.println(tab + "]}");
                }
            }

        }
        line = lineTemp;
        tab = tabTemp;
        dataSocket.close();
    }

    /**
     * Cette méthode permet passer le serveur FTP en mode passif, il créer la Socket pour récupérer les données du serveur FTP
     * Le client reçoit une chaine de caractère qui correspond à l'URL et au port de la socket.
     *
     * @return la socket connecté avec l'url et le port obtenu
     * @throws IOException
     */
    public Socket getDataSock() throws IOException {
        this.printer.println("PASV ");
        String s = reader.readLine();
        s = s.substring(s.indexOf("(") + 1);
        s = s.substring(0, s.indexOf(")"));
        String[] tab = s.split(",");
        return new Socket(tab[0] + '.' + tab[1] + '.' + tab[2] + '.' + tab[3], Integer.parseInt(tab[4]) * 256 + Integer.parseInt(tab[5]));
    }

    public String getUrlServer() {
        return urlServer;
    }

    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNumberDirectoryRead() {
        return numberDirectoryRead;
    }

    public void setNumberDirectoryRead(int numberDirectoryRead) {
        this.numberDirectoryRead = numberDirectoryRead;
    }

    public int getNumberFileRead() {
        return numberFileRead;
    }

    public void setNumberFileRead(int numberFileRead) {
        this.numberFileRead = numberFileRead;
    }

}
