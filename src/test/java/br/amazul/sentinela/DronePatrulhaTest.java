package br.amazul.sentinela;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DronePatrulhaTest {

    private final ZonaRestrita zonaBase = new ZonaRestrita("ZNR-01", "Casa do Reator", 4.0);

    @Test
    void dronePatrulhaDeveConsumirBateriaEPodeSerDespachado() {
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", zonaBase);

        dronePatrulha.executarPatrulha();
        assertEquals(95, dronePatrulha.getBateriaPercentual());

        assertTrue(dronePatrulha.despacharPara("Setor Norte"));
        assertTrue(dronePatrulha.isDespachado());
        assertEquals("Setor Norte", dronePatrulha.getSetorDeDespacho());
        assertEquals(85, dronePatrulha.getBateriaPercentual());
    }

    @Test
    void dronePatrulhaComBateriaBaixaNaoDeveSerDespachado() {
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", zonaBase);
        for (int patrulha = 0; patrulha < 17; patrulha++) {
            dronePatrulha.executarPatrulha();
        }
        assertEquals(15, dronePatrulha.getBateriaPercentual());

        assertFalse(dronePatrulha.despacharPara("Setor Norte"));
        assertFalse(dronePatrulha.isDespachado());
    }

    @Test
    void bateriaNuncaDeveFicarNegativa() {
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", zonaBase);
        for (int patrulha = 0; patrulha < 30; patrulha++) {
            dronePatrulha.executarPatrulha();
        }
        assertEquals(0, dronePatrulha.getBateriaPercentual());
    }

    @Test
    void cameraDeveReconhecerSomenteOQueACenaSimulaEComecarSemDeteccoes() {
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo", zonaBase);
        assertEquals(ObjetoDetectado.NENHUM, dronePatrulha.getCamera().analisarImagem());

        dronePatrulha.getCamera().simularCena(ObjetoDetectado.VEICULO_NAO_IDENTIFICADO);

        assertEquals(ObjetoDetectado.VEICULO_NAO_IDENTIFICADO, dronePatrulha.getCamera().analisarImagem());
        assertThrows(IllegalArgumentException.class, () -> dronePatrulha.getCamera().simularCena(null));
    }

    @Test
    void rotaDePatrulhaVaziaDeveSerRejeitada() {
        assertThrows(IllegalArgumentException.class, () -> new DronePatrulha("DRN-01", "", zonaBase));
    }
}
