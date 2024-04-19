package com.example.lab2sd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessadorArquivoHTML {

    //João Lucas Freitas de Almeida Rocha 22121004-0 e Aline Stolai 22121003-2

    public static void main(String[] args) {
        String nomeArquivo = "/home/joarocha/IdeaProjects/Lab2SD/src/main/java/com/example/lab2sd/teste.html"; // Nome do arquivo HTML

        try {
            BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo));
            String linha;

            while ((linha = leitor.readLine()) != null) {
                if (linha.contains("<link")) {
                    processarTag(linha, "href");
                } else if (linha.contains("<img")) {
                    processarTag(linha, "src");
                } else if (linha.contains("<script")) {
                    processarTag(linha, "src");
                } else if (linha.contains("<seguranca")) {
                    processarTagSeguranca(linha);
                }
            }

            leitor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processarTag(String tag, String atributo) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String valorAtributo = obterValorAtributo(tag, atributo);
            if (valorAtributo != null) {
                long tamanhoConteudo = valorAtributo.getBytes().length;
                System.out.println("Tamanho do conteúdo referenciado por " + atributo + ": " + tamanhoConteudo + " bytes");
            }
        });
        executor.shutdown(); // Encerra o ExecutorService após a conclusão da tarefa
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Aguarda a conclusão de todas as tarefas
        } catch (InterruptedException e) {
            // Tratamento de exceção, se necessário
        }
    }

    private static String obterValorAtributo(String tag, String nomeAtributo) {
        Pattern pattern = Pattern.compile(nomeAtributo + "\\s*=\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(tag);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void processarTagSeguranca(String tag) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String certificador = obterValorAtributo(tag, "certificador");
            if (certificador != null) {
                long tamanhoConteudo = certificador.getBytes().length;
                InetAddress endereco = null;
                try {
                    endereco = InetAddress.getByName(certificador);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                System.out.println("Tamanho do conteúdo da tag <seguranca>: " + tamanhoConteudo + " bytes");
                System.out.println("Certificador: " + certificador + ", IP associado: " + Objects.requireNonNull(endereco).getHostAddress());
            }
        });
        executor.shutdown(); // Encerra o ExecutorService após a conclusão da tarefa
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Aguarda a conclusão de todas as tarefas
        } catch (InterruptedException e) {
            // Tratamento de exceção, se necessário
        }

    }
}
