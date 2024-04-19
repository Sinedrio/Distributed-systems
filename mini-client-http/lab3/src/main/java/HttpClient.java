import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClient {

    public static void main(String[] args) {
        String host = "ufsc.br";
        int porta = 80;
        String caminho = "/";

        try (Socket socket = new Socket(host, porta);
             OutputStream outputStream = socket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Envia requisição HTTP GET
            String request = "GET " + caminho + " HTTP/1.1\r\n"
                    + "Host: " + host + "\r\n"
                    + "Connection: close\r\n"
                    + "\r\n";
            outputStream.write(request.getBytes());
            outputStream.flush();

            // Lê a resposta do servidor
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            // Extrai URLs de <img>, <link> e <script>
            extractAndDownloadResources(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractAndDownloadResources(String html) {
        Pattern pattern = Pattern.compile("<(img|link|script) [^>]*src=\"(http://[^\"'>]+)\"");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String tag = matcher.group(1);
            String url = matcher.group(2);

            if (!url.startsWith("https")) {
                new Thread(() -> downloadResource(url)).start();
            }
        }
    }

    private static void downloadResource(String url) {
        try (Socket socket = new Socket(getHost(url), 80);
             OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {

            String request = "GET " + getPath(url) + " HTTP/1.1\r\n"
                    + "Host: " + getHost(url) + "\r\n"
                    + "Connection: close\r\n"
                    + "Accept: image/png, image/jpeg, image/gif\r\n" // Adiciona cabeçalho para aceitar imagens
                    + "\r\n";
            outputStream.write(request.getBytes());
            outputStream.flush();

            // Lê os headers da resposta HTTP
            StringBuilder headers = new StringBuilder();
            int currentChar;
            while ((currentChar = inputStream.read()) != -1) {
                headers.append((char) currentChar);
                if (headers.toString().endsWith("\r\n\r\n")) {
                    break;
                }
            }

            // Lê o conteúdo da resposta (imagem) e salva em um arquivo
            saveImageToFile(inputStream, url);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImageToFile(InputStream inputStream, String url) {
        try {
            String fileName = getFileNameFromUrl(url);
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("Recurso salvo em: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getHost(String url) {
        return url.split("/")[2];
    }

    private static String getPath(String url) {
        String[] parts = url.split("/");
        StringBuilder path = new StringBuilder("/");
        for (int i = 3; i < parts.length; i++) {
            path.append(parts[i]);
            if (i < parts.length - 1) {
                path.append("/");
            }
        }
        return path.toString();
    }

    private static String getFileNameFromUrl(String url) {
        // Extrai o nome do arquivo da URL
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        // Adiciona a extensão do arquivo, se existir
        if (!fileName.contains(".")) {
            fileName += ".jpg"; // Adiciona extensão .jpg como padrão
        }
        return fileName;
    }
}
