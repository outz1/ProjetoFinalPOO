package view;

import exception.LocadoraException;
import model.Carro;
import model.Moto;
import model.Utilitario;
import model.Veiculo;
import service.Locadora;

/** UC01: gerenciar veículos (só o Gerente chega aqui). */
public class TelaVeiculos extends Tela {
    private static final String INCLUIR = "Incluir veículo";
    private static final String ALTERAR = "Alterar veículo";
    private static final String EXCLUIR = "Excluir veículo";
    private static final String CONSULTAR = "Consultar por placa";
    private static final String LISTAR = "Listar todos";
    private static final String LISTAR_CATEGORIA = "Listar por categoria";
    private static final String MANUTENCAO = "Enviar para manutenção";
    private static final String RETORNAR = "Retornar da manutenção";

    public TelaVeiculos(Locadora locadora) {
        super(locadora);
    }

    /** Campos que todo veículo tem, lidos de uma vez para que a alteração seja tudo ou nada. */
    private record DadosComuns(String placa, String marca, String modelo, int ano, String cor, int km, double diaria) {
        void aplicar(Veiculo v) {
            v.setPlaca(placa);
            v.setMarca(marca);
            v.setModelo(modelo);
            v.setAno(ano);
            v.setCor(cor);
            v.setQuilometragem(km);
            v.setValorDiariaBase(diaria);
        }
    }

    @Override
    protected String titulo() {
        return "Veículos";
    }

    @Override
    protected String[] opcoes() {
        return new String[]{INCLUIR, ALTERAR, EXCLUIR, CONSULTAR, LISTAR, LISTAR_CATEGORIA, MANUTENCAO, RETORNAR};
    }

    @Override
    protected void executar(String opcao) throws LocadoraException {
        switch (opcao) {
            case INCLUIR -> incluir();
            case ALTERAR -> alterar();
            case EXCLUIR -> excluir();
            case CONSULTAR -> mostrarDetalhe("Veículo", buscar("Placa do veículo:"));
            case LISTAR -> mostrarLista("Todos os veículos", locadora.getVeiculos().listar());
            case LISTAR_CATEGORIA -> listarPorCategoria();
            case MANUTENCAO -> {
                Veiculo v = buscar("Placa do veículo que vai para manutenção:");
                locadora.enviarParaManutencao(v.getId());
                mostrar("Veículo " + v.getPlaca() + " enviado para manutenção.");
            }
            case RETORNAR -> {
                Veiculo v = buscar("Placa do veículo que volta da manutenção:");
                locadora.retornarDaManutencao(v.getId());
                mostrar("Veículo " + v.getPlaca() + " voltou para a frota.");
            }
            default -> throw new IllegalStateException("Opção desconhecida: " + opcao);
        }
    }

    private Veiculo buscar(String mensagem) throws LocadoraException {
        return locadora.getVeiculos().buscarPorPlaca(lerTexto(mensagem));
    }

    private void incluir() throws LocadoraException {
        String[] categorias = Veiculo.CATEGORIAS.toArray(new String[0]);
        String categoria = categorias[lerOpcao("Categoria do veículo:", categorias)];
        DadosComuns d = lerDadosComuns(null);

        Veiculo veiculo;
        switch (categoria) {
            case Carro.CATEGORIA -> {
                int portas = lerInteiro("Número de portas:");
                boolean automatico = lerSimNao("O câmbio é automático?");
                boolean ar = lerSimNao("Possui ar-condicionado?");
                veiculo = new Carro(d.placa(), d.marca(), d.modelo(), d.ano(), d.cor(), d.km(), d.diaria(),
                        portas, automatico, ar);
            }
            case Moto.CATEGORIA -> {
                int cilindradas = lerInteiro("Cilindradas (cc):");
                veiculo = new Moto(d.placa(), d.marca(), d.modelo(), d.ano(), d.cor(), d.km(), d.diaria(), cilindradas);
            }
            default -> {
                double carga = lerDecimal("Capacidade de carga (kg):");
                String carroceria = lerTexto("Tipo de carroceria (ex.: furgão, baú):");
                veiculo = new Utilitario(d.placa(), d.marca(), d.modelo(), d.ano(), d.cor(), d.km(), d.diaria(),
                        carga, carroceria);
            }
        }
        locadora.incluirVeiculo(veiculo);
        mostrar("Veículo incluído com o código " + veiculo.getId() + ".");
    }

    private void alterar() throws LocadoraException {
        Veiculo atual = buscar("Placa do veículo a alterar:");
        DadosComuns d = lerDadosComuns(atual);

        if (atual instanceof Carro carro) {
            int portas = lerInteiro("Número de portas:", carro.getNumeroPortas());
            boolean automatico = lerSimNao("O câmbio é automático? (atual: " + (carro.isAutomatico() ? "sim" : "não") + ")");
            boolean ar = lerSimNao("Possui ar-condicionado? (atual: " + (carro.isArCondicionado() ? "sim" : "não") + ")");
            d.aplicar(carro);
            carro.setNumeroPortas(portas);
            carro.setAutomatico(automatico);
            carro.setArCondicionado(ar);
        } else if (atual instanceof Moto moto) {
            int cilindradas = lerInteiro("Cilindradas (cc):", moto.getCilindradas());
            d.aplicar(moto);
            moto.setCilindradas(cilindradas);
        } else {
            Utilitario utilitario = (Utilitario) atual;
            double carga = lerDecimal("Capacidade de carga (kg):", utilitario.getCapacidadeCargaKg());
            String carroceria = lerTexto("Tipo de carroceria:", utilitario.getTipoCarroceria());
            d.aplicar(utilitario);
            utilitario.setCapacidadeCargaKg(carga);
            utilitario.setTipoCarroceria(carroceria);
        }
        locadora.alterarVeiculo(atual);
        mostrar("Veículo " + atual.getPlaca() + " alterado.");
    }

    /** Com um veículo, usa os valores atuais como sugestão; com null, os campos começam vazios. */
    private DadosComuns lerDadosComuns(Veiculo atual) throws LocadoraException {
        String placa = lerTexto("Placa (ex.: ABC1D23):", atual == null ? "" : atual.getPlaca());
        String marca = lerTexto("Marca:", atual == null ? "" : atual.getMarca());
        String modelo = lerTexto("Modelo:", atual == null ? "" : atual.getModelo());
        int ano = atual == null ? lerInteiro("Ano:") : lerInteiro("Ano:", atual.getAno());
        String cor = lerTexto("Cor:", atual == null ? "" : atual.getCor());
        int km = atual == null ? lerInteiro("Quilometragem atual:") : lerInteiro("Quilometragem atual:", atual.getQuilometragem());
        double diaria = atual == null ? lerDecimal("Diária base (R$):") : lerDecimal("Diária base (R$):", atual.getValorDiariaBase());
        return new DadosComuns(placa, marca, modelo, ano, cor, km, diaria);
    }

    private void excluir() throws LocadoraException {
        Veiculo v = buscar("Placa do veículo a excluir:");
        if (lerSimNao("Excluir o veículo " + v.getPlaca() + " (" + v.getMarca() + " " + v.getModelo() + ")?")) {
            locadora.excluirVeiculo(v.getId());
            mostrar("Veículo excluído.");
        }
    }

    private void listarPorCategoria() {
        String[] categorias = Veiculo.CATEGORIAS.toArray(new String[0]);
        String categoria = categorias[lerOpcao("Categoria:", categorias)];
        mostrarLista("Veículos da categoria " + categoria, locadora.getVeiculos().listarPorCategoria(categoria));
    }
}
