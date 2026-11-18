package br.amazul.sentinela;

public class SensorRadiacao extends DispositivoMonitoramento {

    private final double limiteSeguranca;
    private double leituraAtual;

    /** O sensor fica dentro da zona monitorada; a localização vem da descrição dela. */
    public SensorRadiacao(String codigoIdentificador, ZonaRestrita zonaMonitorada, double limiteSeguranca) {
        super(codigoIdentificador, zonaMonitorada == null ? null : zonaMonitorada.getDescricao(), zonaMonitorada);
        if (limiteSeguranca <= 0) {
            throw new IllegalArgumentException("Limite de segurança do sensor deve ser positivo.");
        }
        this.limiteSeguranca = limiteSeguranca;
    }

    /**
     * O sensor guarda o que o hardware mediu, mesmo que seja lixo.
     * Quem valida a leitura é a zona (regra de domínio), e a central trata a falha.
     */
    public void registrarLeitura(double leitura) {
        this.leituraAtual = leitura;
    }

    public boolean excedeLimiteSeguranca() {
        return leituraAtual >= limiteSeguranca;
    }

    public double getLeituraAtual() { return leituraAtual; }
    public double getLimiteSeguranca() { return limiteSeguranca; }
}
