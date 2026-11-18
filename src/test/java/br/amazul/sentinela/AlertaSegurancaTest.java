package br.amazul.sentinela;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertaSegurancaTest {

    private final ZonaRestrita zonaAfetada = new ZonaRestrita("ZNR-01", "Casa do Reator", 4.0);

    @Test
    void alertaDeveReferenciarMesmoSensorQueDisparou() {
        SensorRadiacao sensorOrigem = new SensorRadiacao("SEN-RAD-01", zonaAfetada, 4.0);

        AlertaSeguranca alerta = new AlertaSeguranca(sensorOrigem, zonaAfetada, Severidade.CRITICO, "Leitura alta");

        assertSame(sensorOrigem, alerta.getSensorOrigem());
        assertSame(zonaAfetada, alerta.getZonaAfetada());
    }

    @Test
    void alertaDeveRejeitarDadosObrigatoriosAusentes() {
        SensorRadiacao sensorOrigem = new SensorRadiacao("SEN-RAD-01", zonaAfetada, 4.0);

        assertThrows(IllegalArgumentException.class,
                () -> new AlertaSeguranca(null, zonaAfetada, Severidade.CRITICO, "x"));
        assertThrows(IllegalArgumentException.class,
                () -> new AlertaSeguranca(sensorOrigem, null, Severidade.CRITICO, "x"));
        assertThrows(IllegalArgumentException.class,
                () -> new AlertaSeguranca(sensorOrigem, zonaAfetada, null, "x"));
        assertThrows(IllegalArgumentException.class,
                () -> new AlertaSeguranca(sensorOrigem, zonaAfetada, Severidade.CRITICO, " "));
    }

    @Test
    void alertaDeveDarAcessoALocalizacaoDoDispositivoDeOrigem() {
        SensorRadiacao sensorOrigem = new SensorRadiacao("SEN-RAD-01", zonaAfetada, 4.0);
        AlertaSeguranca alerta = new AlertaSeguranca(sensorOrigem, zonaAfetada, Severidade.ATENCAO, "x");

        assertEquals("Casa do Reator", alerta.getSensorOrigem().getLocalizacao());
    }
}
