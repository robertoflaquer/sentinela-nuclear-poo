package br.amazul.sentinela;

/**
 * Drone É UM dispositivo de monitoramento, mas NÃO é um sensor de radiação.
 * Ele TEM uma câmera a bordo (associação), em vez de herdar comportamento de sensor.
 */
public class DronePatrulha extends DispositivoMonitoramento {

    static final int BATERIA_MINIMA_PARA_DESPACHO = 20;
    private static final int CONSUMO_PATRULHA = 5;
    private static final int CONSUMO_DESPACHO = 10;

    private final String rotaPatrulha;
    private final CameraVisaoComputacional camera = new CameraVisaoComputacional();
    private int bateriaPercentual = 100;
    private String setorDeDespacho;

    public DronePatrulha(String codigoIdentificador, String rotaPatrulha, ZonaRestrita zonaBase) {
        super(codigoIdentificador, "Base do drone", zonaBase);
        if (rotaPatrulha == null || rotaPatrulha.isBlank()) {
            throw new IllegalArgumentException("Rota de patrulha é obrigatória.");
        }
        this.rotaPatrulha = rotaPatrulha;
    }

    public void executarPatrulha() {
        consumirBateria(CONSUMO_PATRULHA);
    }

    public boolean podeSerDespachado() {
        return isEmOperacao() && bateriaPercentual >= BATERIA_MINIMA_PARA_DESPACHO;
    }

    /** Envia o drone ao setor da violação. Retorna false se ele não puder ir. */
    public boolean despacharPara(String setorVigiado) {
        if (!podeSerDespachado()) {
            return false;
        }
        consumirBateria(CONSUMO_DESPACHO);
        this.setorDeDespacho = setorVigiado;
        return true;
    }

    public void retornarParaBase() { this.setorDeDespacho = null; }

    public void recarregarBateria() { this.bateriaPercentual = 100; }

    private void consumirBateria(int percentual) {
        this.bateriaPercentual = Math.max(0, bateriaPercentual - percentual);
    }

    public boolean isDespachado() { return setorDeDespacho != null; }
    public String getSetorDeDespacho() { return setorDeDespacho; }
    public String getRotaPatrulha() { return rotaPatrulha; }
    public int getBateriaPercentual() { return bateriaPercentual; }
    public CameraVisaoComputacional getCamera() { return camera; }
}
