package BackEnd.controller;

import BackEnd.model.entity.Item;
import BackEnd.model.entity.ItemPedido;
import BackEnd.model.entity.Pedido;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualizarPedidosController {

    @FXML
    private TableView<ItemResumo> tabelaResumo;
    @FXML
    private TableColumn<ItemResumo, Integer> colunaResumoId;
    @FXML
    private TableColumn<ItemResumo, String> colunaResumoNome;
    @FXML
    private TableColumn<ItemResumo, Double> colunaResumoPrecoVenda;
    @FXML
    private TableColumn<ItemResumo, String> colunaResumoUnidadeMedida;
    @FXML
    private TableColumn<ItemResumo, Double> colunaResumoQuantidade;
    @FXML
    private TableColumn<ItemResumo, Double> colunaResumoQtdAtual;
    @FXML
    private TableColumn<ItemResumo, Double> colunaResumoQtdEstoque;
    @FXML
    private TableColumn<ItemResumo, String> colunaResumoCategoria;
    @FXML
    private Label labelValorTotal;
    @FXML
    private Label labelQuantidadeTotal;
    @FXML
    private VBox containerTabelasIndividuais;

    private ObservableList<Pedido> pedidosSelecionados;
    private ObservableList<ItemResumo> itensResumo = FXCollections.observableArrayList();

    public void initialize() {
        configurarColunasTabelaResumo();
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidosSelecionados = FXCollections.observableArrayList(pedidos);
        preencherTabelaResumo();
        criarTabelasPedidosIndividuais();
        preencherTabelasPedidosIndividuais();
        calcularTotais();
    }

    private void configurarColunasTabelaResumo() {
        colunaResumoId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colunaResumoNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colunaResumoPrecoVenda.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecoVenda()).asObject());
        colunaResumoUnidadeMedida.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUnidadeMedida()));
        colunaResumoQuantidade.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantidade()).asObject());
        colunaResumoQtdAtual.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQtdAtual()).asObject());
        colunaResumoQtdEstoque.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQtdEstoque()).asObject());
        colunaResumoCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria()));
    }

    private void preencherTabelaResumo() {
        Map<Integer, ItemResumo> itensAgrupados = new HashMap<>();

        for (Pedido pedido : pedidosSelecionados) {
            for (ItemPedido itemPedido : pedido.getItens()) {
                int idItem = itemPedido.getItem().getId();
                if (itensAgrupados.containsKey(idItem)) {
                    ItemResumo itemResumo = itensAgrupados.get(idItem);
                    itemResumo.setQuantidade(itemResumo.getQuantidade() + itemPedido.getQuantidade()); // Usando Double
                } else {
                    ItemResumo itemResumo = new ItemResumo(itemPedido.getItem());
                    itemResumo.setQuantidade(itemPedido.getQuantidade()); // Usando Double
                    itensAgrupados.put(idItem, itemResumo);
                }
            }
        }

        itensResumo.addAll(itensAgrupados.values());
        tabelaResumo.setItems(itensResumo);
    }

    private void criarTabelasPedidosIndividuais() {
        for (Pedido pedido : pedidosSelecionados) {
            Label labelCliente = new Label("Cliente: " + pedido.getCliente().getNome() + " (" + pedido.getCliente().getCpfCnpj() + ")");
            labelCliente.getStyleClass().add("form-label");

            TableView<ItemPedido> tabelaPedido = new TableView<>();
            configurarColunasTabelaPedido(tabelaPedido);

            tabelaPedido.setItems(FXCollections.observableArrayList(pedido.getItens()));

            // Cria um HBox para os labels de totais do pedido
            HBox hboxTotaisPedido = new HBox(10); // Espaçamento de 10 entre os labels
            hboxTotaisPedido.setAlignment(Pos.CENTER_RIGHT);

            // Calcula os totais do pedido
            Map<String, Double> totaisPedido = calcularTotaisPedido(pedido);

            // Cria os labels e adiciona ao HBox
            Label labelValorTotalPedido = new Label("Valor Total: R$ " + String.format("%.2f", totaisPedido.get("valorTotal")));
            Label labelQuantidadeTotalPedido = new Label("Quantidade Total: " + String.format("%.2f", totaisPedido.get("quantidadeTotal")));
            labelValorTotalPedido.getStyleClass().add("form-label");
            labelQuantidadeTotalPedido.getStyleClass().add("form-label");

            hboxTotaisPedido.getChildren().addAll(labelValorTotalPedido, labelQuantidadeTotalPedido);

            // Adiciona o label do cliente, a tabela e o HBox de totais ao VBox
            VBox vboxPedido = new VBox(5); // Espaçamento de 5 entre os elementos
            vboxPedido.getChildren().addAll(labelCliente, tabelaPedido, hboxTotaisPedido);

            containerTabelasIndividuais.getChildren().add(vboxPedido);
        }
    }

    private void configurarColunasTabelaPedido(TableView<ItemPedido> tabelaPedido) {
        TableColumn<ItemPedido, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getItem().getId()).asObject());

        TableColumn<ItemPedido, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getNome()));

        TableColumn<ItemPedido, Double> colunaPrecoVenda = new TableColumn<>("Preço de Venda");
        colunaPrecoVenda.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecoVenda()).asObject());

        TableColumn<ItemPedido, String> colunaUnidadeMedida = new TableColumn<>("Unid. de Medida");
        colunaUnidadeMedida.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getUnidadeMedida()));

        TableColumn<ItemPedido, Double> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantidade()).asObject());

        TableColumn<ItemPedido, Double> colunaQtdAtual = new TableColumn<>("Qtd. Atual");
        colunaQtdAtual.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getItem().getQuantidadeAtual()).asObject());

        TableColumn<ItemPedido, Double> colunaQtdEstoque = new TableColumn<>("Qtd. Estoque");
        colunaQtdEstoque.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getItem().getQuantidadeEstoque()).asObject());

        TableColumn<ItemPedido, String> colunaCategoria = new TableColumn<>("Categoria");
        colunaCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getCategoria().getNome()));

        tabelaPedido.getColumns().addAll(colunaId, colunaNome, colunaPrecoVenda, colunaUnidadeMedida, colunaQuantidade, colunaQtdAtual, colunaQtdEstoque, colunaCategoria);
        tabelaPedido.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void preencherTabelasPedidosIndividuais() {
        // Não é necessário fazer nada aqui, pois as tabelas já são preenchidas em criarTabelasPedidosIndividuais()
    }

    private void calcularTotais() {
        double valorTotal = pedidosSelecionados.stream().mapToDouble(Pedido::getValorTotal).sum();
        double quantidadeTotal = itensResumo.stream().mapToDouble(ItemResumo::getQuantidade).sum(); // Usando Double

        labelValorTotal.setText("Valor Total: R$ " + String.format("%.2f", valorTotal));
        labelQuantidadeTotal.setText("Quantidade Total: " + String.format("%.2f", quantidadeTotal)); // Formatando para 2 casas decimais
    }

    // Método para calcular os totais de um pedido específico
    private Map<String, Double> calcularTotaisPedido(Pedido pedido) {
        double valorTotal = pedido.getItens().stream()
                .mapToDouble(itemPedido -> itemPedido.getQuantidade() * itemPedido.getPrecoVenda())
                .sum();

        double quantidadeTotal = pedido.getItens().stream()
                .mapToDouble(ItemPedido::getQuantidade)
                .sum();

        Map<String, Double> totais = new HashMap<>();
        totais.put("valorTotal", valorTotal);
        totais.put("quantidadeTotal", quantidadeTotal);
        return totais;
    }

    // Classe Interna para o Resumo
    public static class ItemResumo {
        private int id;
        private String nome;
        private double precoVenda;
        private String unidadeMedida;
        private double quantidade; // Alterado para Double
        private double qtdAtual; // Alterado para Double
        private double qtdEstoque; // Alterado para Double
        private String categoria;

        public ItemResumo(Item item) {
            this.id = item.getId();
            this.nome = item.getNome();
            this.precoVenda = item.getPrecoVenda();
            this.unidadeMedida = item.getUnidadeMedida();
            this.qtdAtual = item.getQuantidadeAtual();
            this.qtdEstoque = item.getQuantidadeEstoque();
            this.categoria = item.getCategoria().getNome();
        }

        // Getters e Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public double getPrecoVenda() {
            return precoVenda;
        }

        public void setPrecoVenda(double precoVenda) {
            this.precoVenda = precoVenda;
        }

        public String getUnidadeMedida() {
            return unidadeMedida;
        }

        public void setUnidadeMedida(String unidadeMedida) {
            this.unidadeMedida = unidadeMedida;
        }

        public double getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(double quantidade) {
            this.quantidade = quantidade;
        }

        public double getQtdAtual() {
            return qtdAtual;
        }

        public void setQtdAtual(double qtdAtual) {
            this.qtdAtual = qtdAtual;
        }

        public double getQtdEstoque() {
            return qtdEstoque;
        }

        public void setQtdEstoque(double qtdEstoque) {
            this.qtdEstoque = qtdEstoque;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }
    }
}