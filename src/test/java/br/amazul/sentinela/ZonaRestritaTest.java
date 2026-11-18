package br.amazul.sentinela;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZonaRestritaTest {

    private ZonaRestrita criarCasaDoReator() {
        return new ZonaRestrita("ZNR-01", "Casa do Reator", 4.0);
    }

    @Test
    void zonaNaoDeveSerNulaAposInstanciacao() {
        assertNotNull(criarCasaDoReator());
    }

    @Test
    void leituraDeveMudarClassificacaoDeNormalParaCritico() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(0.2);
        assertEquals("NORMAL", zona.classificarRisco());

        zona.registrarLeituraRadiacao(4.7);

        assertEquals(4.7, zona.getNivelRadiacaoAtual());
        assertEquals("CRITICO", zona.classificarRisco());
    }

    @Test
    void leituraProximaDoLimiteDeveClassificarComoAtencao() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(3.5);
        assertEquals("ATENCAO", zona.classificarRisco());
    }

    @Test
    void leituraExatamenteNoLimiteDeveSerCritica() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(4.0);
        assertEquals("CRITICO", zona.classificarRisco());
    }

    @Test
    void radiacaoNegativaDeveSerRejeitadaEManterUltimoValor() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(2.0);

        assertThrows(IllegalArgumentException.class, () -> zona.registrarLeituraRadiacao(-5.0));

        assertEquals(2.0, zona.getNivelRadiacaoAtual());
    }

    @Test
    void leituraAbsurdaDeveSerRejeitadaComoFalhaDeSensor() {
        ZonaRestrita zona = criarCasaDoReator();
        zona.registrarLeituraRadiacao(1.0);

        assertThrows(IllegalArgumentException.class, () -> zona.registrarLeituraRadiacao(1_000_001));
        assertThrows(IllegalArgumentException.class, () -> zona.registrarLeituraRadiacao(Double.NaN));

        assertEquals(1.0, zona.getNivelRadiacaoAtual());
    }

    @Test
    void identificadorVazioDeveSerRejeitadoNoConstrutor() {
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("", "Casa do Reator", 4.0));
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita(null, "Casa do Reator", 4.0));
    }

    @Test
    void descricaoVaziaELimiteInvalidoDevemSerRejeitados() {
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", " ", 4.0));
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", "Casa do Reator", 0));
        assertThrows(IllegalArgumentException.class, () -> new ZonaRestrita("ZNR-01", "Casa do Reator", -1));
    }

    @Test
    void zonaRecemCriadaDeveComecarComRadiacaoZeroEClassificacaoNormal() {
        ZonaRestrita zona = criarCasaDoReator();
        assertEquals(0.0, zona.getNivelRadiacaoAtual());
        assertEquals("NORMAL", zona.classificarRisco());
    }
}
