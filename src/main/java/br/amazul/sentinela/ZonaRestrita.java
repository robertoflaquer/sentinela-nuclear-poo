package br.amazul.sentinela;

/**
 * Área da instalação com acesso restrito e nível de radiação monitorado.
 * Guarda a regra de classificação de risco: quem sabe os limites é a própria zona.
 */
public class ZonaRestrita {

    public static final String RISCO_NORMAL = "NORMAL";
    public static final String RISCO_ATENCAO = "ATENCAO";
    public static final String RISCO_CRITICO = "CRITICO";

    /** Acima disso o valor é tratado como falha do sensor (µSv/h). */
    static final double LEITURA_MAXIMA_VALIDA = 1_000_000;
    /** A partir de 80% do limite a zona entra em atenção. */
    private static final double FATOR_ATENCAO = 0.8;

    private final String identificador;
    private final String descricao;
    private final double limiteRadiacao;
    private double nivelRadiacaoAtual;

    public ZonaRestrita(String identificador, String descricao, double limiteRadiacao) {
        if (identificador == null || identificador.isBlank()) {
            throw new IllegalArgumentException("Identificador da zona é obrigatório.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição da zona é obrigatória.");
        }
        if (limiteRadiacao <= 0 || limiteRadiacao > LEITURA_MAXIMA_VALIDA) {
            throw new IllegalArgumentException("Limite de radiação deve ser positivo e fisicamente plausível.");
        }
        this.identificador = identificador;
        this.descricao = descricao;
        this.limiteRadiacao = limiteRadiacao;
        this.nivelRadiacaoAtual = 0.0;
    }

    /** Atualiza o nível atual. Leitura inválida é rejeitada e o último valor válido é mantido. */
    public void registrarLeituraRadiacao(double leitura) {
        if (Double.isNaN(leitura) || leitura < 0) {
            throw new IllegalArgumentException("Radiação negativa ou inválida não existe fisicamente: " + leitura);
        }
        if (leitura > LEITURA_MAXIMA_VALIDA) {
            throw new IllegalArgumentException("Leitura absurda, tratada como falha de sensor: " + leitura);
        }
        this.nivelRadiacaoAtual = leitura;
    }

    public String classificarRisco() {
        if (nivelRadiacaoAtual >= limiteRadiacao) {
            return RISCO_CRITICO;
        }
        if (nivelRadiacaoAtual >= limiteRadiacao * FATOR_ATENCAO) {
            return RISCO_ATENCAO;
        }
        return RISCO_NORMAL;
    }

    public String getIdentificador() { return identificador; }
    public String getDescricao() { return descricao; }
    public double getLimiteRadiacao() { return limiteRadiacao; }
    public double getNivelRadiacaoAtual() { return nivelRadiacaoAtual; }

    @Override
    public String toString() {
        return identificador + " - " + descricao;
    }
}
