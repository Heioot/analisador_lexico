package compiladores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Lexico {

    private String nomeArquivo;
    private BufferedReader br;
    private char caractere;
    private List<String> palavrasReservadas;
    private int linha;
    private int coluna;

    public Lexico(String nomeArquivo){
        this.nomeArquivo = nomeArquivo;
        String caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        try {
            br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
            caractere = (char) br.read();
        } catch (IOException ex){
            System.out.println("Erro abrindo o arquivo " + nomeArquivo);
            System.out.println("Caminho do arquivo: " + caminhoArquivo);
        }
        palavrasReservadas = Arrays.asList(
            "program", "begin", "end", "var", "integer", "procedure", "function", "read", "write", "writeln",
            "for", "do", "repeat", "until", "while", "if", "then", "else", "or", "and", "not", "true", "false"
        );
        linha = 1;
        coluna = 1;
    }

    public Token getNextToken(){
        StringBuilder lexema;
        Token token;

        try {
            while (caractere != 65535) {
                while (caractere == ' ' || caractere == '\t' || caractere == '\n' || caractere == '\r') {
                    if (caractere == '\n') {
                        linha++;
                        coluna = 1;
                    } else {
                        coluna++;
                    }
                    caractere = (char) br.read();
                }

                if (caractere == -1) break;

                token = new Token(linha, coluna);

                if (Character.isLetter(caractere)) {
                    lexema = new StringBuilder();
                    while (Character.isLetterOrDigit(caractere)) {
                        lexema.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    String s = lexema.toString();
                    if (palavrasReservadas.contains(s.toLowerCase())) {
                        token.setClasse(ClasseToken.cPalRes);
                    } else {
                        token.setClasse(ClasseToken.cId);
                    }
                    token.setValor(new ValorToken(s));
                    return token;
                }

                else if (Character.isDigit(caractere)) {
                    lexema = new StringBuilder();
                    while (Character.isDigit(caractere)) {
                        lexema.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    token.setClasse(ClasseToken.cInt);
                    token.setValor(new ValorToken(Integer.parseInt(lexema.toString())));
                    return token;
                }

                else if (caractere == ';'){
                    token.setClasse(ClasseToken.cPontoVirgula);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == ':'){
                    token.setClasse(ClasseToken.cDoisPontos);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '='){
                    token.setClasse(ClasseToken.cIgual);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '>'){
                    token.setClasse(ClasseToken.cMaior);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '<'){
                    token.setClasse(ClasseToken.cMenor);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '+'){
                    token.setClasse(ClasseToken.cAdicao);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '-'){
                    token.setClasse(ClasseToken.cSubtracao);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '*'){
                    token.setClasse(ClasseToken.cMultiplicacao);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '/'){
                    token.setClasse(ClasseToken.cDivisao);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '('){
                    token.setClasse(ClasseToken.cParEsq);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == ')'){
                    token.setClasse(ClasseToken.cParDir);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '"'){
                    lexema = new StringBuilder();
                    caractere = (char) br.read();
                    while (caractere != '"') {
                        lexema.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    token.setClasse(ClasseToken.cString);
                    token.setValor(new ValorToken(lexema.toString()));
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '.'){
                    token.setClasse(ClasseToken.cPonto);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == ','){
                    token.setClasse(ClasseToken.cVirgula);
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                } else if (caractere == '\''){
                    lexema = new StringBuilder();
                    caractere = (char) br.read();
                    while (caractere != '\'') {
                        if (caractere == -1) {
                            System.err.println("Erro léxico. String não fechada.");
                            System.exit(-1);
                        }
                        lexema.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    token.setClasse(ClasseToken.cString);
                    token.setValor(new ValorToken(lexema.toString()));
                    caractere = (char) br.read();
                    coluna++;
                    return token;
                }else if (caractere == '{') {
                    // Ignorar comentários de linha única
                    while (caractere != '\n' && caractere != -1) {
                        caractere = (char) br.read();
                        coluna++;
                    }
                    if (caractere == '\n') {
                        linha++;
                        coluna = 1;
                        caractere = (char) br.read();
                    }
                } else if (caractere == '{') {
                    // Ignorar comentários de bloco
                    caractere = (char) br.read();
                    coluna++;
                    while (caractere != '}' && caractere != -1) {
                        if (caractere == '\n') {
                            linha++;
                            coluna = 1;
                        } else {
                            coluna++;
                        }
                        caractere = (char) br.read();
                    }
                    if (caractere == '}') {
                        caractere = (char) br.read();
                        coluna++;
                    }
                }

            else if(caractere == 65535){
                token = new Token(linha, coluna);
                token.setClasse(ClasseToken.cEOF);
                String valor = "sem valor";
                System.out.println("linha=" + token.getLinha() + ", coluna=" + token.getColuna() +
                    " - Token [classe=" + token.getClasse() + ", valor=" + valor + "]");
                return token;
            }

                else {
                    System.err.println("Erro léxico. Caractere inválido = " + caractere);
                    System.exit(-1);
                }
            }

            token = new Token(linha, coluna);
            token.setClasse(ClasseToken.cEOF);
            return token;

        } catch (IOException e) {
            System.err.println("Não foi possível ler do arquivo: " + nomeArquivo);
            e.printStackTrace();
            return null;
        }
    }
}
