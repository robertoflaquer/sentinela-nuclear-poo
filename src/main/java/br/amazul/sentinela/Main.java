package br.amazul.sentinela;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // new ZonaRestrita();  // "zona fantasma": não compila, o construtor padrão deixou de existir

        ZonaRestrita casaDoReator = new ZonaRestrita("ZNR-01", "Casa do Reator", 4.0);
        ZonaRestrita patioCombustivelIrradiado = new ZonaRestrita("ZNR-02", "Pátio de Combustível Irradiado", 6.0);

        SensorRadiacao sensorRadiacaoReator = new SensorRadiacao("SEN-RAD-01", casaDoReator, 4.0);
        SensorRadiacao sensorRadiacaoPatio = new SensorRadiacao("SEN-RAD-02", patioCombustivelIrradiado, 6.0);
        SensorPerimetral sensorPerimetralNorte = new SensorPerimetral("SEN-PER-01", "Setor Norte", casaDoReator);
        DronePatrulha dronePatrulha = new DronePatrulha("DRN-01", "Perímetro externo, sentido horário", casaDoReator);

        CentralMonitoramento central = new CentralMonitoramento(new DispositivoMonitoramento[] {
                sensorRadiacaoReator, sensorRadiacaoPatio, sensorPerimetralNorte, dronePatrulha
        });

        // CENÁRIO 1
        imprimirTitulo("CENÁRIO 1: ROTINA NORMAL");
        sensorRadiacaoReator.registrarLeitura(0.2);
        sensorRadiacaoPatio.registrarLeitura(0.3);
        dronePatrulha.executarPatrulha();
        imprimirEstado(sensorRadiacaoReator, sensorRadiacaoPatio, sensorPerimetralNorte, dronePatrulha);
        imprimirAlertas(central.analisarLeituras());

        // CENÁRIO 2
        imprimirTitulo("CENÁRIO 2: PICO DE RADIAÇÃO");
        sensorRadiacaoReator.registrarLeitura(4.7);
        imprimirEstado(sensorRadiacaoReator, sensorRadiacaoPatio, sensorPerimetralNorte, dronePatrulha);
        imprimirAlertas(central.analisarLeituras());

        // Antes do cenário 3 a radiação volta ao normal, para o alerta do cenário 2 não se repetir.
        sensorRadiacaoReator.registrarLeitura(0.2);

        // CENÁRIO 3
        imprimirTitulo("CENÁRIO 3: ACESSO INDEVIDO");
        sensorPerimetralNorte.detectarViolacao();
        dronePatrulha.getCamera().simularCena(ObjetoDetectado.VEICULO_NAO_IDENTIFICADO);
        imprimirEstado(sensorRadiacaoReator, sensorRadiacaoPatio, sensorPerimetralNorte, dronePatrulha);
        imprimirAlertas(central.analisarLeituras());
    }

    private static void imprimirTitulo(String titulo) {
        System.out.println("\n=== " + titulo + " ===");
    }

    private static void imprimirEstado(SensorRadiacao sensorRadiacaoReator, SensorRadiacao sensorRadiacaoPatio,
                                       SensorPerimetral sensorPerimetral, DronePatrulha dronePatrulha) {
        System.out.println("Leituras registradas:");
        System.out.println("  " + sensorRadiacaoReator.getCodigoIdentificador() + " (" + sensorRadiacaoReator.getLocalizacao()
                + "): " + sensorRadiacaoReator.getLeituraAtual() + " µSv/h");
        System.out.println("  " + sensorRadiacaoPatio.getCodigoIdentificador() + " (" + sensorRadiacaoPatio.getLocalizacao()
                + "): " + sensorRadiacaoPatio.getLeituraAtual() + " µSv/h");
        System.out.println("  " + sensorPerimetral.getCodigoIdentificador() + " (" + sensorPerimetral.getLocalizacao()
                + "): " + (sensorPerimetral.isViolacaoDetectada() ? "VIOLAÇÃO" : "perímetro íntegro"));
        System.out.println("  " + dronePatrulha.getCodigoIdentificador() + ": bateria " + dronePatrulha.getBateriaPercentual()
                + "%, rota \"" + dronePatrulha.getRotaPatrulha() + "\", detecções: "
                + dronePatrulha.getCamera().analisarImagem());
    }

    private static void imprimirAlertas(List<AlertaSeguranca> alertas) {
        System.out.println("Alertas da central:");
        if (alertas.isEmpty()) {
            System.out.println("  nenhum alerta, instalação em rotina normal.");
            return;
        }
        for (AlertaSeguranca alertaSeguranca : alertas) {
            System.out.println("  " + alertaSeguranca);
        }
    }
}
