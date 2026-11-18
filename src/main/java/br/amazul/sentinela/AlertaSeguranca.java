package br.amazul.sentinela;

/**
 * Guarda REFERÊNCIAS ao dispositivo de origem e à zona afetada (TEM-UM), não apenas
 * seus códigos em texto: assim a central consulta localização, zona e estado depois.
 */
public class AlertaSeguranca {

    private final DispositivoMonitoramento sensorOrigem;
    private final ZonaRestrita zonaAfetada;
    private final Severidade severidade;
    private final String mensagem;

    public AlertaSeguranca(DispositivoMonitoramento sensorOrigem, ZonaRestrita zonaAfetada,
                           Severidade severidade, String mensagem) {
        if (sensorOrigem == null || zonaAfetada == null || severidade == null) {
            throw new IllegalArgumentException("Alerta precisa de origem, zona afetada e severidade.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("Alerta precisa de uma mensagem.");
        }
        this.sensorOrigem = sensorOrigem;
        this.zonaAfetada = zonaAfetada;
        this.severidade = severidade;
        this.mensagem = mensagem;
    }

    public DispositivoMonitoramento getSensorOrigem() { return sensorOrigem; }
    public ZonaRestrita getZonaAfetada() { return zonaAfetada; }
    public Severidade getSeveridade() { return severidade; }
    public String getMensagem() { return mensagem; }

    @Override
    public String toString() {
        return "[" + severidade + "] " + mensagem
                + " (origem: " + sensorOrigem.getCodigoIdentificador()
                + " em " + sensorOrigem.getLocalizacao()
                + " | zona: " + zonaAfetada + ")";
    }
}
