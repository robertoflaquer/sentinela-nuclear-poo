package br.amazul.sentinela;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de regras da Sprint 1: varre os dispositivos e gera um alerta para cada
 * situação atípica. Cada dispositivo já sabe a qual zona pertence.
 */
public class CentralMonitoramento {

    private final DispositivoMonitoramento[] dispositivos;

    public CentralMonitoramento(DispositivoMonitoramento[] dispositivos) {
        if (dispositivos == null) {
            throw new IllegalArgumentException("A central precisa de dispositivos para monitorar.");
        }
        this.dispositivos = dispositivos.clone();
    }

    public List<AlertaSeguranca> analisarLeituras() {
        List<AlertaSeguranca> alertas = new ArrayList<>();
        for (DispositivoMonitoramento dispositivo : dispositivos) {
            if (!dispositivo.isEmOperacao()) {
                continue;
            }
            if (dispositivo instanceof SensorRadiacao sensorRadiacao) {
                analisarSensorRadiacao(sensorRadiacao, alertas);
            } else if (dispositivo instanceof SensorPerimetral sensorPerimetral) {
                analisarSensorPerimetral(sensorPerimetral, alertas);
            }
        }
        return alertas;
    }

    private void analisarSensorRadiacao(SensorRadiacao sensorRadiacao, List<AlertaSeguranca> alertas) {
        ZonaRestrita zonaMonitorada = sensorRadiacao.getZonaMonitorada();
        try {
            zonaMonitorada.registrarLeituraRadiacao(sensorRadiacao.getLeituraAtual());
        } catch (IllegalArgumentException leituraRejeitada) {
            alertas.add(new AlertaSeguranca(sensorRadiacao, zonaMonitorada, Severidade.ATENCAO,
                    "Leitura rejeitada, possível falha do sensor: " + leituraRejeitada.getMessage()));
            return;
        }
        String risco = zonaMonitorada.classificarRisco();
        if (!risco.equals(ZonaRestrita.RISCO_NORMAL)) {
            Severidade severidade = Severidade.valueOf(risco);
            alertas.add(new AlertaSeguranca(sensorRadiacao, zonaMonitorada, severidade,
                    "Radiação em " + zonaMonitorada.getNivelRadiacaoAtual() + " µSv/h (limite "
                            + zonaMonitorada.getLimiteRadiacao() + " µSv/h)"));
        }
    }

    private void analisarSensorPerimetral(SensorPerimetral sensorPerimetral, List<AlertaSeguranca> alertas) {
        if (!sensorPerimetral.isViolacaoDetectada()) {
            return;
        }
        ZonaRestrita zonaProtegida = sensorPerimetral.getZonaMonitorada();
        alertas.add(new AlertaSeguranca(sensorPerimetral, zonaProtegida, Severidade.ATENCAO,
                "Violação da cerca no " + sensorPerimetral.getSetorVigiado() + ". Enviando drone para confirmar."));

        DronePatrulha dronePatrulha = localizarDroneDisponivel();
        if (dronePatrulha == null) {
            alertas.add(new AlertaSeguranca(sensorPerimetral, zonaProtegida, Severidade.CRITICO,
                    "Nenhum drone disponível para confirmar a violação. Acionar equipe de segurança."));
            return;
        }
        dronePatrulha.despacharPara(sensorPerimetral.getSetorVigiado());
        ObjetoDetectado objetoDetectado = dronePatrulha.getCamera().analisarImagem();
        if (objetoDetectado != ObjetoDetectado.NENHUM) {
            alertas.add(new AlertaSeguranca(dronePatrulha, zonaProtegida, Severidade.CRITICO,
                    "Visão computacional confirmou " + objetoDetectado + " no " + sensorPerimetral.getSetorVigiado()
                            + " (bateria do drone: " + dronePatrulha.getBateriaPercentual() + "%)"));
        }
    }

    private DronePatrulha localizarDroneDisponivel() {
        for (DispositivoMonitoramento dispositivo : dispositivos) {
            if (dispositivo instanceof DronePatrulha dronePatrulha && dronePatrulha.podeSerDespachado()) {
                return dronePatrulha;
            }
        }
        return null;
    }
}
