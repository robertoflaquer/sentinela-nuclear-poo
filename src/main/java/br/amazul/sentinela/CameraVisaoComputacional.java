package br.amazul.sentinela;

/**
 * Câmera de bordo do drone. A visão computacional é simulada: o cenário
 * informa o que está no campo de visão e a câmera "reconhece" isso.
 */
public class CameraVisaoComputacional {

    private ObjetoDetectado objetoNoCampoDeVisao = ObjetoDetectado.NENHUM;

    public void simularCena(ObjetoDetectado objetoNaCena) {
        if (objetoNaCena == null) {
            throw new IllegalArgumentException("A cena simulada precisa informar um objeto (use NENHUM).");
        }
        this.objetoNoCampoDeVisao = objetoNaCena;
    }

    public ObjetoDetectado analisarImagem() {
        return objetoNoCampoDeVisao;
    }
}
