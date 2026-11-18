package br.amazul.sentinela;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HerancaTest {

    private final ZonaRestrita zona = new ZonaRestrita("ZNR-01", "Casa do Reator", 4.0);

    @Test
    void filhasDevemHerdarGetCodigoIdentificadorDaSuperclasse() {
        SensorRadiacao sensorRadiacao = new SensorRadiacao("SEN-RAD-01", zona, 4.0);
        SensorPerimetral sensorPerimetral = new SensorPerimetral("SEN-PER-01", "Setor Norte", zona);
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", zona);

        assertEquals("SEN-RAD-01", sensorRadiacao.getCodigoIdentificador());
        assertEquals("SEN-PER-01", sensorPerimetral.getCodigoIdentificador());
        assertEquals("DRN-01", dronePatrulha.getCodigoIdentificador());
    }

    @Test
    void todasAsFilhasDevemSerDispositivosMonitoramentoOperantesPorPadrao() {
        DispositivoMonitoramento[] dispositivos = {
                new SensorRadiacao("SEN-RAD-01", zona, 4.0),
                new SensorPerimetral("SEN-PER-01", "Setor Norte", zona),
                new DronePatrulha("DRN-01", "Perímetro externo", zona)
        };
        for (DispositivoMonitoramento dispositivo : dispositivos) {
            assertTrue(dispositivo.isEmOperacao());
            assertSame(zona, dispositivo.getZonaMonitorada());
        }
    }

    @Test
    void dronePatrulhaNaoDeveSerSensorDeRadiacao() {
        DispositivoMonitoramento dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", zona);
        assertFalse(dronePatrulha instanceof SensorRadiacao);
    }

    @Test
    void superclasseDeveRejeitarCodigoVazioPassadoPeloSuper() {
        assertThrows(IllegalArgumentException.class, () -> new SensorRadiacao("", zona, 4.0));
        assertThrows(IllegalArgumentException.class, () -> new SensorPerimetral(" ", "Setor Norte", zona));
        assertThrows(IllegalArgumentException.class, () -> new DronePatrulha(null, "Rota", zona));
    }

    @Test
    void dispositivoSemZonaDeveSerRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> new SensorRadiacao("SEN-RAD-01", null, 4.0));
    }

    @Test
    void dispositivoDesativadoDeveSairDeOperacao() {
        SensorPerimetral sensorPerimetral = new SensorPerimetral("SEN-PER-01", "Setor Norte", zona);
        sensorPerimetral.desativar();
        assertFalse(sensorPerimetral.isEmOperacao());
        sensorPerimetral.ativar();
        assertTrue(sensorPerimetral.isEmOperacao());
    }
}
