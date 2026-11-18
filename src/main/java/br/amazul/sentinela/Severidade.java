package br.amazul.sentinela;

/**
 * Níveis de severidade de um alerta. Usar enum evita erro de digitação
 * ("CRITICO" vs "CRÍTICO") e deixa o compilador conferir os valores.
 */
public enum Severidade {
    ATENCAO,
    CRITICO
}
