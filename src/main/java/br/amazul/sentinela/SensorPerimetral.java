package br.amazul.sentinela;

public class SensorPerimetral extends DispositivoMonitoramento {

    private final String setorVigiado;
    private boolean violacaoDetectada;

    public SensorPerimetral(String codigoIdentificador, String setorVigiado, ZonaRestrita zonaProtegida) {
        super(codigoIdentificador, "Cerca do " + setorVigiado, zonaProtegida);
        this.setorVigiado = setorVigiado;
    }

    public void detectarViolacao() { this.violacaoDetectada = true; }
    public void restaurarPerimetro() { this.violacaoDetectada = false; }

    public boolean isViolacaoDetectada() { return violacaoDetectada; }
    public String getSetorVigiado() { return setorVigiado; }
}
