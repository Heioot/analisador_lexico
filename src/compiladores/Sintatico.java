package compiladores;

public class Sintatico {

    private Token token;
    private Lexico lexico;

    public Sintatico(String nomeArquivo) {
        lexico = new Lexico(nomeArquivo);
        token = lexico.getNextToken();
    }

    public void analisar() {
        processarPrograma();
    }

    // Estruturas Principais
    private void processarPrograma() {
        if (checarPalavraReservada("program")) {
            token = lexico.getNextToken();
            if (token.getClasse() == ClasseToken.cId) {
                token = lexico.getNextToken();
                if (token.getClasse() == ClasseToken.cPontoVirgula) {
                    token = lexico.getNextToken();
                    processarCorpo();
                    if (token.getClasse() == ClasseToken.cPonto) {
                        token = lexico.getNextToken();
                    } else {
                        emitirErro("Esperado '.' para finalizar o programa.");
                    }
                } else {
                    emitirErro("Esperado ';' após o nome do programa.");
                }
            } else {
                emitirErro("Esperado identificador como nome do programa.");
            }
        } else {
            emitirErro("Esperado a palavra-chave 'program' no início.");
        }
    }

    private void processarCorpo() {
        processarDeclaracoes();
        processarRotinas();
        if (checarPalavraReservada("begin")) {
            token = lexico.getNextToken();
            processarListaSentencas();
            if (checarPalavraReservada("end")) {
                token = lexico.getNextToken();
            } else {
                emitirErro("Esperado 'end' para encerrar o bloco.");
            }
        } else {
            emitirErro("Esperado 'begin' para iniciar o corpo de comandos.");
        }
    }

    // Declarações (VAR)
    private void processarDeclaracoes() {
        if (checarPalavraReservada("var")) {
            token = lexico.getNextToken();
            processarDVar();
            processarMaisDc();
        }
    }

    private void processarMaisDc() {
        if (token.getClasse() == ClasseToken.cPontoVirgula) {
            token = lexico.getNextToken();
            if (token.getClasse() == ClasseToken.cId) {
                processarDVar();
                processarMaisDc();
            }
        } else if (!checarPalavraReservada("begin")
                && !checarPalavraReservada("procedure")
                && !checarPalavraReservada("function")) {
            emitirErro("Esperado ';' ao final da declaração ou início de bloco.");
        }
    }

    private void processarDVar() {
        processarVariaveis();
        if (token.getClasse() == ClasseToken.cDoisPontos) {
            token = lexico.getNextToken();
            processarTipoVar();
        } else {
            emitirErro("Esperado ':' entre variáveis e tipo.");
        }
    }

    private void processarTipoVar() {
        if (checarPalavraReservada("integer")) {
            token = lexico.getNextToken();
        } else {
            emitirErro("Tipo inválido: permitido apenas 'integer'.");
        }
    }

    private void processarVariaveis() {
        if (token.getClasse() == ClasseToken.cId) {
            token = lexico.getNextToken();
            processarMaisVar();
        } else {
            emitirErro("Esperado identificador de variável.");
        }
    }

    private void processarMaisVar() {
        if (token.getClasse() == ClasseToken.cVirgula) {
            token = lexico.getNextToken();
            processarVariaveis();
        }
    }

    // Rotinas (Procedure/Function)
    private void processarRotinas() {
        while (checarPalavraReservada("procedure") || checarPalavraReservada("function")) {
            token = lexico.getNextToken();
            if (token.getClasse() == ClasseToken.cId) {
                token = lexico.getNextToken();
                if (token.getClasse() == ClasseToken.cPontoVirgula) {
                    token = lexico.getNextToken();
                } else {
                    emitirErro("Esperado ';' ao final do cabeçalho da rotina.");
                }
            } else {
                emitirErro("Esperado identificador da rotina (procedure/function).");
            }
        }
    }

    // Lista de Sentenças e Comandos
    private void processarListaSentencas() {
        while (token.getClasse() == ClasseToken.cId ||
               checarPalavraReservada("begin") ||
               checarPalavraReservada("read") ||
               checarPalavraReservada("write") ||
               checarPalavraReservada("writeln") ||
               checarPalavraReservada("for") ||
               checarPalavraReservada("while") ||
               checarPalavraReservada("if") ||
               checarPalavraReservada("repeat")) {

            processarComando();

            if (token.getClasse() == ClasseToken.cPontoVirgula) {
                token = lexico.getNextToken();
            } else if (!checarPalavraReservada("end")
                    && !checarPalavraReservada("until")
                    && !checarPalavraReservada("else")) {
                emitirErro("Esperado ';' após o comando.");
            }
        }
    }

    private void processarComando() {
        if (checarPalavraReservada("read")) {
            processarRead();
        } else if (checarPalavraReservada("write") || checarPalavraReservada("writeln")) {
            processarWriteWriteln();
        } else if (checarPalavraReservada("for")) {
            processarFor();
        } else if (checarPalavraReservada("while")) {
            processarWhile();
        } else if (checarPalavraReservada("if")) {
            processarIf();
        } else if (checarPalavraReservada("repeat")) {
            processarRepeat();
        } else if (checarPalavraReservada("begin")) {
            processarBlocoSentencas();
        } else if (token.getClasse() == ClasseToken.cId) {
            processarAtribuicao();
        } else {
            emitirErro("Esperado início de comando válido.");
        }
    }

    private void processarAtribuicao() {
        if (token.getClasse() == ClasseToken.cId) {
            token = lexico.getNextToken();
            if (token.getClasse() == ClasseToken.cAtrib) {
                token = lexico.getNextToken();
                processarExpressao();
            } else {
                emitirErro("Esperado ':=' após o identificador.");
            }
        } else {
            emitirErro("Esperado identificador no início da atribuição.");
        }
    }

    private void processarRead() {
        token = lexico.getNextToken();
        consumirParametrosIO(true);
    }

    private void processarWriteWriteln() {
        token = lexico.getNextToken();
        consumirParametrosIO(false);
    }

    private void processarFor() {
        token = lexico.getNextToken();
        if (token.getClasse() == ClasseToken.cId) {
            token = lexico.getNextToken();
            if (token.getClasse() == ClasseToken.cAtrib) {
                token = lexico.getNextToken();
                processarExpressao();
                if (checarPalavraReservada("to")) {
                    token = lexico.getNextToken();
                    processarExpressao();
                    if (checarPalavraReservada("do")) {
                        token = lexico.getNextToken();
                        processarComando();
                    } else {
                        emitirErro("Esperado 'do' após os limites do 'for'.");
                    }
                } else {
                    emitirErro("Esperado 'to' no comando 'for'.");
                }
            } else {
                emitirErro("Esperado ':=' na inicialização do 'for'.");
            }
        } else {
            emitirErro("Esperado identificador da variável de controle do 'for'.");
        }
    }

    private void processarWhile() {
        token = lexico.getNextToken();
        if (token.getClasse() == ClasseToken.cParEsq) {
            token = lexico.getNextToken();
            processarExpressaoLogica();
            if (token.getClasse() == ClasseToken.cParDir) {
                token = lexico.getNextToken();
            } else {
                emitirErro("Esperado ')' para fechar a condição do 'while'.");
            }
        } else {
            processarExpressaoLogica();
        }

        if (checarPalavraReservada("do")) {
            token = lexico.getNextToken();
            processarComando();
        } else {
            emitirErro("Esperado 'do' após a condição do 'while'.");
        }
    }

    private void processarIf() {
        token = lexico.getNextToken();
        processarExpressaoLogica();
        if (checarPalavraReservada("then")) {
            token = lexico.getNextToken();
            processarComando();
            processarPfalsa();
        } else {
            emitirErro("Esperado 'then' após a condição do 'if'.");
        }
    }

    private void processarPfalsa() {
        if (checarPalavraReservada("else")) {
            token = lexico.getNextToken();
            processarComando();
        }
    }

    private void processarRepeat() {
        token = lexico.getNextToken();
        processarListaSentencas();
        if (checarPalavraReservada("until")) {
            token = lexico.getNextToken();
            if (token.getClasse() == ClasseToken.cParEsq) {
                token = lexico.getNextToken();
                processarExpressaoLogica();
                if (token.getClasse() == ClasseToken.cParDir) {
                    token = lexico.getNextToken();
                } else {
                    emitirErro("Esperado ')' para fechar a condição do 'until'.");
                }
            } else {
                emitirErro("Esperado '(' após 'until'.");
            }
        } else {
            emitirErro("Esperado 'until' após o bloco do 'repeat'.");
        }
    }

    private void processarBlocoSentencas() {
         token = lexico.getNextToken();
         processarListaSentencas();
         if (checarPalavraReservada("end")) {
             token = lexico.getNextToken();
         } else {
             emitirErro("Esperado 'end' para encerrar o bloco 'begin'.");
         }
    }

    // Expressões Lógicas
    private void processarExpressaoLogica() {
        processarTermoLogico();
        while (checarPalavraReservada("or")) {
            token = lexico.getNextToken();
            processarTermoLogico();
        }
    }

    private void processarTermoLogico() {
        processarFatorLogico();
        while (checarPalavraReservada("and")) {
            token = lexico.getNextToken();
            processarFatorLogico();
        }
    }

    private void processarFatorLogico() {
        if (checarPalavraReservada("not")) {
            token = lexico.getNextToken();
            processarFatorLogico();
        } else if (token.getClasse() == ClasseToken.cParEsq) {
            token = lexico.getNextToken();
            processarExpressaoLogica();
            if (token.getClasse() == ClasseToken.cParDir) {
                token = lexico.getNextToken();
            } else {
                emitirErro("Esperado ')' ao fechar a expressão lógica.");
            }
        } else if (checarPalavraReservada("true") || checarPalavraReservada("false")) {
             token = lexico.getNextToken();
        } else {
            processarRelacional();
        }
    }

    private void processarRelacional() {
        processarExpressao();
        if (isOperadorRelacional(token)) {
            token = lexico.getNextToken();
            processarExpressao();
        } else {
            emitirErro("Esperado operador relacional (=, <>, <, <=, >, >=).");
        }
    }

    // Expressões Aritméticas
    private void processarExpressao() {
        processarTermo();
        while (token.getClasse() == ClasseToken.cAdicao || token.getClasse() == ClasseToken.cSubtracao) {
            token = lexico.getNextToken();
            processarTermo();
        }
    }

    private void processarTermo() {
        processarFator();
        while (token.getClasse() == ClasseToken.cMultiplicacao || token.getClasse() == ClasseToken.cDivisao) {
            token = lexico.getNextToken();
            processarFator();
        }
    }

    private void processarFator() {
        if (token.getClasse() == ClasseToken.cId || token.getClasse() == ClasseToken.cInt) {
            token = lexico.getNextToken();
        } else if (token.getClasse() == ClasseToken.cParEsq) {
            token = lexico.getNextToken();
            processarExpressao();
            if (token.getClasse() == ClasseToken.cParDir) {
                token = lexico.getNextToken();
            } else {
                emitirErro("Esperado ')' na expressão.");
            }
        } else {
            emitirErro("Esperado ID, número ou '('.");
        }
    }

    // Funções Auxiliares
    private void consumirParametrosIO(boolean isRead) {
        if (token.getClasse() == ClasseToken.cParEsq) {
            token = lexico.getNextToken();
            do {
                if (token.getClasse() == ClasseToken.cId) {
                    token = lexico.getNextToken();
                } else if (!isRead && (token.getClasse() == ClasseToken.cString || token.getClasse() == ClasseToken.cInt)) {
                    token = lexico.getNextToken();
                } else {
                    emitirErro("Parâmetro inválido em I/O.");
                }

                if (token.getClasse() == ClasseToken.cVirgula) {
                    token = lexico.getNextToken();
                } else {
                    break;
                }
            } while (true);

            if (token.getClasse() == ClasseToken.cParDir) {
                token = lexico.getNextToken();
            } else {
                emitirErro("Esperado ')' após os parâmetros de I/O.");
            }
        } else {
            emitirErro("Esperado '(' na chamada de I/O.");
        }
    }

    private boolean isOperadorRelacional(Token t) {
        return t.getClasse() == ClasseToken.cMaior ||
               t.getClasse() == ClasseToken.cMenor ||
               t.getClasse() == ClasseToken.cMaiorIgual ||
               t.getClasse() == ClasseToken.cMenorIgual ||
               t.getClasse() == ClasseToken.cIgual ||
               t.getClasse() == ClasseToken.cDiferente;
    }

    private void emitirErro(String msg) {
        System.out.println("[" + token.getLinha() + "," + token.getColuna() + "] " + msg);
        System.exit(-1);
    }

    private boolean checarPalavraReservada(String palavra) {
        return token.getClasse() == ClasseToken.cPalRes &&
                token.getValor().getTexto().equalsIgnoreCase(palavra);
    }
}
