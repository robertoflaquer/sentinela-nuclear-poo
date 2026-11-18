package br.amazul.sentinela;

/**
 * Superclasse de tudo que monitora a instalação (sensores e drone).
 * O código identificador é privado: as filhas o entregam via super().
 */
public abstract class DispositivoMonitoramento {

    private final String codigoIdentificador;
    private final String localizacao;
    private final ZonaRestrita zonaMonitorada;
    private boolean emOperacao;

    protected DispositivoMonitoramento(String codigoIdentificador, String localizacao, ZonaRestrita zonaMonitorada) {
        if (codigoIdentificador == null || codigoIdentificador.isBlank()) {
            throw new IllegalArgumentException("Código identificador do dispositivo é obrigatório.");
        }
        if (localizacao == null || localizacao.isBlank()) {
            throw new IllegalArgumentException("Localização do dispositivo é obrigatória.");
        }
        if (zonaMonitorada == null) {
            throw new IllegalArgumentException("Todo dispositivo precisa estar ligado a uma zona restrita.");
        }
        this.codigoIdentificador = codigoIdentificador;
        this.localizacao = localizacao;
        this.zonaMonitorada = zonaMonitorada;
        this.emOperacao = true;
    }

    public void ativar() { this.emOperacao = true; }
    public void desativar() { this.emOperacao = false; }

    public String getCodigoIdentificador() { return codigoIdentificador; }
    public String getLocalizacao() { return localizacao; }
    public ZonaRestrita getZonaMonitorada() { return zonaMonitorada; }
    public boolean isEmOperacao() { return emOperacao; }
}
