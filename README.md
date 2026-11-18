# SENTINELA: domínio base (Sprint 1)

Protótipo em console que modela as zonas restritas de uma instalação nuclear, os dispositivos de monitoramento (sensores e drone de patrulha) e a central que analisa as leituras e emite alertas por severidade.

## Como executar

Requer Java 21 e Maven.

```
mvn test                  # 33 testes unitários
mvn compile exec:java -Dexec.mainClass=br.amazul.sentinela.Main
```

Ou rode `Main` direto na IDE. Se o terminal do Windows mostrar o "µ" quebrado, use `chcp 65001` antes.

## Estrutura

| Classe | Papel |
|---|---|
| `ZonaRestrita` | Zona com radiação monitorada. Valida leituras e classifica o risco (NORMAL, ATENCAO, CRITICO). |
| `DispositivoMonitoramento` | Superclasse abstrata: código identificador, localização, zona monitorada, em operação. |
| `SensorRadiacao` | É-UM dispositivo. Guarda `leituraAtual` e `limiteSeguranca`. |
| `SensorPerimetral` | É-UM dispositivo. Guarda `setorVigiado` e detecta violação da cerca. |
| `DronePatrulha` | É-UM dispositivo. Guarda `rotaPatrulha`, `bateriaPercentual` e TEM-UMA `CameraVisaoComputacional`. |
| `CameraVisaoComputacional` | Visão computacional simulada (`ObjetoDetectado`). |
| `AlertaSeguranca` | TEM-UM dispositivo de origem, TEM-UMA zona afetada, `Severidade` e mensagem. |
| `CentralMonitoramento` | TEM-UM array de `DispositivoMonitoramento`. `analisarLeituras()` é o motor de regras. |

Decisões de projeto:

- **Cada dispositivo conhece a sua zona** (`zonaMonitorada`). A central não adivinha zona por texto de localização.
- **Severidade é enum** (`ATENCAO`, `CRITICO`), sem erro de digitação em texto solto.
- **Escalonamento do acesso indevido:** a violação da cerca gera `ATENCAO`, a central despacha o drone, e só a confirmação da câmera escala para `CRITICO`. Sem drone disponível (desativado ou bateria abaixo de 20%), escala direto para `CRITICO`.
- **Leitura inválida não derruba a central:** a zona rejeita a leitura (mantém o último valor válido) e a central gera um alerta de falha de sensor.
- **Limite de 3 parâmetros** respeitado nos construtores. A exceção é `AlertaSeguranca` (4 campos exigidos pelo enunciado: origem, zona, severidade, mensagem).

## Cenários executados pelo `Main`

1. **Rotina normal:** leituras abaixo dos limites, perímetro íntegro, drone em patrulha sem detecções. Nenhum alerta.
2. **Pico de radiação:** a Casa do Reator sobe de 0,2 para 4,7 µSv/h (limite 4,0). Alerta `CRITICO`.
3. **Acesso indevido:** o sensor perimetral detecta violação, a central envia o drone, a câmera confirma veículo não identificado. Alerta `ATENCAO` seguido de `CRITICO`.

## Perguntas de reflexão

**Classes e objetos.** `ZonaRestrita` é o molde: define quais dados e comportamentos toda zona tem. "ZNR-01 - Casa do Reator" é uma zona concreta, com valores próprios e um nível de radiação que muda ao longo do tempo. Uma classe, vários objetos.

**Métodos e comportamentos.** A regra de risco depende dos dados da própria zona (limite e nível atual). Se ela ficasse em `if`s no `main`, cada trecho que classifica risco repetiria a regra, e uma mudança de faixa exigiria caçar todos os `if`s. Dentro da classe, há um único lugar para mudar e testar.

**Encapsulamento.** Com `nivelRadiacaoAtual` público, qualquer código poderia gravar `-5` ou `0` direto e o relatório da central mostraria uma instalação segura durante um vazamento, sem passar por validação nem deixar rastro. Privado, a única porta de entrada é `registrarLeituraRadiacao`, que rejeita valores impossíveis.

**Construtores.** Ao declarar um construtor próprio, o Java deixa de gerar o construtor padrão. Isso impede `new ZonaRestrita()`, uma "zona fantasma" sem identificador, descrição ou limite. Toda zona nasce válida ou não nasce.

**Associação.** Se o alerta guardasse só o código do sensor em texto, a central perderia o acesso à localização, à zona e ao estado do dispositivo. Para escalonar o incidente, teria de procurar o sensor por código em outra estrutura, com risco de não achar ou achar o errado. Guardando a referência, os dados vêm direto do objeto.

**Herança.** `codigoIdentificador` é privado na mãe para que ninguém altere a identidade de um dispositivo por fora, nem as filhas. O `super()` obriga o dado a passar pelo construtor da mãe, onde é validado uma única vez. O princípio protegido é o encapsulamento: a classe é dona dos seus dados e das regras sobre eles.
