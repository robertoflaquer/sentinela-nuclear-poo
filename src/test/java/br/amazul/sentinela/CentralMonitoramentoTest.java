package br.amazul.sentinela;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CentralMonitoramentoTest {

    private ZonaRestrita casaDoReator;
    private ZonaRestrita patioCombustivelIrradiado;
    private SensorRadiacao sensorRadiacaoReator;
    private SensorRadiacao sensorRadiacaoPatio;
    private SensorPerimetral sensorPerimetralNorte;
    private DronePatrulha dronePatrulha;
    private CentralMonitoramento central;

    @BeforeEach
    void montarInstalacao() {
        casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 4.0);
        patioCombustivelIrradiado = new ZonaRestrita("ZNR-02", "Pátio de Combustível Irradiado", 6.0);
        sensorRadiacaoReator = new SensorRadiacao("SEN-RAD-01", casaDoReator, 4.0);
        sensorRadiacaoPatio = new SensorRadiacao("SEN-RAD-02", patioCombustivelIrradiado, 6.0);
        sensorPerimetralNorte = new SensorPerimetral("SEN-PER-01", "Setor Norte", casaDoReator);
        dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", casaDoReator);
        central = new CentralMonitoramento(new DispositivoMonitoramento[] {
                sensorRadiacaoReator, sensorRadiacaoPatio, sensorPerimetralNorte, dronePatrulha
        });
        sensorRadiacaoReator.registrarLeitura(0.2);
        sensorRadiacaoPatio.registrarLeitura(0.3);
    }

    @Test
    void rotinaNormalNaoDeveGerarAlertas() {
        assertTrue(central.analisarLeituras().isEmpty());
    }

    @Test
    void picoDeRadiacaoDeveGerarAlertaCriticoNaZonaCorreta() {
        sensorRadiacaoReator.registrarLeitura(4.7);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        AlertaSeguranca alerta = alertas.get(0);
        assertEquals(Severidade.CRITICO, alerta.getSeveridade());
        assertSame(sensorRadiacaoReator, alerta.getSensorOrigem());
        assertSame(casaDoReator, alerta.getZonaAfetada());
    }

    @Test
    void sensorDoPatioNaoDeveAlertarNaZonaDoReator() {
        sensorRadiacaoPatio.registrarLeitura(6.5);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        assertSame(patioCombustivelIrradiado, alertas.get(0).getZonaAfetada());
    }

    @Test
    void leituraProximaDoLimiteDeveGerarAlertaDeAtencao() {
        sensorRadiacaoReator.registrarLeitura(3.5);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(Severidade.ATENCAO, alertas.get(0).getSeveridade());
    }

    @Test
    void acessoIndevidoDeveDespacharDroneEConfirmarVeiculoComAlertaCritico() {
        sensorPerimetralNorte.detectarViolacao();
        dronePatrulha.getCamera().simularCena(ObjetoDetectado.VEICULO_NAO_IDENTIFICADO);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(2, alertas.size());
        assertEquals(Severidade.ATENCAO, alertas.get(0).getSeveridade());
        assertSame(sensorPerimetralNorte, alertas.get(0).getSensorOrigem());
        assertEquals(Severidade.CRITICO, alertas.get(1).getSeveridade());
        assertSame(dronePatrulha, alertas.get(1).getSensorOrigem());
        assertTrue(dronePatrulha.isDespachado());
        assertEquals("Setor Norte", dronePatrulha.getSetorDeDespacho());
    }

    @Test
    void violacaoSemDeteccaoDoDroneNaoDeveEscalarParaCritico() {
        sensorPerimetralNorte.detectarViolacao();

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(1, alertas.size());
        assertEquals(Severidade.ATENCAO, alertas.get(0).getSeveridade());
        assertTrue(dronePatrulha.isDespachado());
    }

    @Test
    void violacaoSemDroneDisponivelDeveEscalarParaCritico() {
        dronePatrulha.desativar();
        sensorPerimetralNorte.detectarViolacao();

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(2, alertas.size());
        assertEquals(Severidade.CRITICO, alertas.get(1).getSeveridade());
        assertFalse(dronePatrulha.isDespachado());
    }

    @Test
    void leituraAbsurdaDoSensorDeveGerarAlertaDeFalhaSemDerrubarACentral() {
        sensorRadiacaoReator.registrarLeitura(5_000_000);
        sensorRadiacaoPatio.registrarLeitura(6.5);

        List<AlertaSeguranca> alertas = central.analisarLeituras();

        assertEquals(2, alertas.size());
        assertEquals(Severidade.ATENCAO, alertas.get(0).getSeveridade());
        assertEquals(0.0, casaDoReator.getNivelRadiacaoAtual());
        assertEquals(Severidade.CRITICO, alertas.get(1).getSeveridade());
    }

    @Test
    void dispositivoForaDeOperacaoNaoDeveGerarAlertas() {
        sensorRadiacaoReator.registrarLeitura(10.0);
        sensorRadiacaoReator.desativar();

        assertTrue(central.analisarLeituras().isEmpty());
    }

    @Test
    void centralNaoDeveAceitarArrayNulo() {
        assertThrows(IllegalArgumentException.class, () -> new CentralMonitoramento(null));
    }
}
