package sr1;

import java.io.IOException;

/**
 * Classe main du projet pour faire tourner le clientFTP
 * @author pochet
 */
public class Main {

    /**
     * Cette méthode est la méthode principal qui vérifie les paramètres et qui fait tourner le clientFTP
     *
     * @param args
     * @throws IOException
     * @throws ConnectionException
     */
    public static void main(String[] args) throws IOException, ConnectionException {
        //Verfication de l'Url fournis
        if (args.length < 1) {
            System.err.println("L'url ne peux pas être vide");
            System.exit(1);
        } else {

            //Profondeur et fichier cachés
            boolean hideFile = false, json = false;
            int depth = -1;

            //Récupération de l'url du serveur
            String urlServer = args[0];

            for (String param : args){
                if(param.contains("-p"))
                    depth = Integer.parseInt(param.split("=")[1]);
                if(param.contains("-a"))
                    hideFile = true;
                if(param.contains("-j"))
                    json = true;
            }

            //Création du ClentFTP avec l'url du serveur
            ClientFTP clientFTP = new ClientFTP(urlServer);

            //Récupération du login
            if (args.length >= 2) clientFTP.setLogin(args[1]);

            //Récupération du mot de passe
            if (args.length >= 3) clientFTP.setPassword(args[2]);

            //Connection au client FTP
            try {
                System.out.println("Connexion vers " + clientFTP.getUrlServer() + "...");
                clientFTP.connect();
            } catch (ConnectionException ce) {
                System.err.println(ce.getMessage());
                System.exit(1);
            }

            //Si on demande le json, alors on écrit directement la premiere ligne
            if (json) System.out.println("[{\"type\":\"directory\",\"name\": \".\",\"contents\":[");

            // Listage des fichiers et répertoire
            clientFTP.listDirectories(hideFile, depth, json);

            //Affichage du nombre de dossier et fichiers répertoriés
            if (json) System.out.println("   ]}, \n   {\"type\":\"report\",\"directories\":" + clientFTP.getNumberDirectoryRead() + ",\"files\":" + clientFTP.getNumberFileRead() + "}"  +"\n" + "]");
            else System.out.println("\n" + clientFTP.getNumberDirectoryRead() + " directories, " + clientFTP.getNumberFileRead() + " files");
        }
    }

}
