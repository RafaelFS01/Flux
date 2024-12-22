package BackEnd.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import BackEnd.model.entity.Item;
import BackEnd.model.service.ItemService;
import BackEnd.util.AlertHelper;
import BackEnd.util.ConnectionFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListarItensController implements Initializable {

    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> filtroTipo;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private TableView<Item> tabelaEquipamentos;
    @FXML private TableColumn<Item, String> colunaId;
    @FXML private TableColumn<Item, String> colunaDescricao;
    @FXML private TableColumn<Item, Integer> colunaQuantidadeAtual;
    @FXML private TableColumn<Item, Integer> colunaQuantidadeEstoque;
    @FXML private TableColumn<Item, Integer> colunaQuantidadeMinima;
    @FXML private TableColumn<Item, String> colunaTipo;
    @FXML private TableColumn<Item, String> colunaStatus;
    @FXML private TableColumn<Item, Void> colunaAcoes;
    @FXML private Label statusLabel;

    private final ItemService itemService;
    private ObservableList<Item> items;

    public ListarItensController() {
        this.itemService = new ItemService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFiltros();
        configurarColunas();
        configurarPesquisa();
        carregarEquipamentos();
    }

    private void configurarFiltros() {
        filtroTipo.setItems(FXCollections.observableArrayList(
                "Todos",
                "Emprestáveis",
                "Consumíveis"
        ));
        filtroTipo.setValue("Todos");

        filtroStatus.setItems(FXCollections.observableArrayList(
                "Todos",
                "Disponível",
                "Em uso",
                "Estoque baixo"
        ));
        filtroStatus.setValue("Todos");

        filtroTipo.setOnAction(e -> aplicarFiltros());
        filtroStatus.setOnAction(e -> aplicarFiltros());
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));

        colunaDescricao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDescricao()));

        colunaQuantidadeAtual.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeAtual()
                ).asObject());

        colunaQuantidadeEstoque.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeEstoque()
                ).asObject());

        colunaQuantidadeMinima.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantidadeMinima()
                ).asObject());


        colunaStatus.setCellValueFactory(data -> {
            Item eq = data.getValue();
            String status;
            if (eq.getQuantidadeAtual() < eq.getQuantidadeMinima()) {
                status = "Estoque baixo";
            } else if (eq.getQuantidadeAtual() == eq.getQuantidadeEstoque()) {
                status = "Disponível";
            } else {
                status = "Em uso";
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Configurar coluna de ações
        colunaAcoes.setCellFactory(column -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDeletar = new Button("Deletar");
            private final Button btnAvaria = new Button("Registrar Avaria");
            private final HBox box = new HBox(5);

            {
                btnEditar.getStyleClass().add("btn-edit");
                btnDeletar.getStyleClass().add("btn-delete");
                btnAvaria.getStyleClass().add("btn-avaria");

                btnDeletar.setOnAction(e -> deletarEquipamento(getTableRow().getItem()));

                box.getChildren().addAll(btnEditar, btnDeletar, btnAvaria);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Estilizar coluna de status
        colunaStatus.setCellFactory(column -> new TableCell<Item, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Disponível":
                            setStyle("-fx-text-fill: #2e7d32;"); // Verde
                            break;
                        case "Em uso":
                            setStyle("-fx-text-fill: #1565c0;"); // Azul
                            break;
                        case "Estoque baixo":
                            setStyle("-fx-text-fill: #c62828;"); // Vermelho
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });
    }

    private void configurarPesquisa() {
        campoBusca.textProperty().addListener((obs, old, novo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (items == null) return;

        FilteredList<Item> dadosFiltrados = new FilteredList<>(items);
        String textoBusca = campoBusca.getText().toLowerCase();
        String tipoSelecionado = filtroTipo.getValue();
        String statusSelecionado = filtroStatus.getValue();

        dadosFiltrados.setPredicate(equipamento -> {
            boolean matchBusca = textoBusca.isEmpty() ||
                    equipamento.getDescricao().toLowerCase().contains(textoBusca) ||
                    equipamento.getId().toLowerCase().contains(textoBusca);


            String status;
            if (equipamento.getQuantidadeAtual() < equipamento.getQuantidadeMinima()) {
                status = "Estoque baixo";
            } else if (equipamento.getQuantidadeAtual() == equipamento.getQuantidadeEstoque()) {
                status = "Disponível";
            } else {
                status = "Em uso";
            }

            boolean matchStatus = statusSelecionado.equals("Todos") ||
                    statusSelecionado.equals(status);

            return matchBusca && matchStatus;
        });
        ConnectionFactory.importarBancoDeDados("BACKUP.2024");
        tabelaEquipamentos.setItems(dadosFiltrados);
        atualizarStatusLabel();

    }

    private void carregarEquipamentos() {
        try {
            items = FXCollections.observableArrayList(
                    itemService.listarItens()
            );
            tabelaEquipamentos.setItems(items);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar equipamentos", e.getMessage());
        }
    }

    private void deletarEquipamento(Item item) {
        try {


            Optional<ButtonType> result = AlertHelper.showConfirmation(
                    "Confirmar Exclusão",
                    "Deseja realmente excluir o equipamento?",
                    "Esta ação não poderá ser desfeita."
            );

            if (result.isPresent() && result.get() == ButtonType.OK) {
                itemService.deletar(item.getId());
                ConnectionFactory.exportarBancoDeDados("BACKUP.2024");
                carregarEquipamentos();
                AlertHelper.showSuccess("Equipamento excluído com sucesso!");
            }
        } catch (Exception e) {
            AlertHelper.showError("Erro ao excluir equipamento", e.getMessage());
        }
    }

    @FXML
    private void exportarLista() {
        AlertHelper.showWarning("Em desenvolvimento",
                "A funcionalidade de exportação será implementada em breve.");
    }

    private void atualizarStatusLabel() {
        int total = tabelaEquipamentos.getItems().size();
        long disponiveis = tabelaEquipamentos.getItems().stream()
                .filter(e -> e.getQuantidadeAtual() == e.getQuantidadeEstoque())
                .count();
        long emUso = tabelaEquipamentos.getItems().stream()
                .filter(e -> e.getQuantidadeAtual() < e.getQuantidadeEstoque() &&
                        e.getQuantidadeAtual() >= e.getQuantidadeMinima())
                .count();
        long estoqueBaixo = tabelaEquipamentos.getItems().stream()
                .filter(e -> e.getQuantidadeAtual() < e.getQuantidadeMinima())
                .count();

        statusLabel.setText(String.format(
                "Total: %d | Disponíveis: %d | Em uso: %d | Estoque baixo: %d",
                total, disponiveis, emUso, estoqueBaixo
        ));
    }
}