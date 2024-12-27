package  BackEnd.controller;

import BackEnd.model.entity.Item;
import BackEnd.model.entity.Categoria;
import BackEnd.model.service.ItemService;
import BackEnd.model.service.CategoriaService;
import BackEnd.model.service.DependenciaService;
import BackEnd.util.AlertHelper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LancarItemEstoqueController {

    @FXML
    private ComboBox<Categoria> comboBoxCategoria;
    @FXML
    private ComboBox<Item> comboBoxProduto;
    @FXML
    private TextField textFieldQuantidade;
    @FXML
    private Button buttonAdicionar;
    @FXML
    private TableView<ItemLancamento> tableViewItens;
    @FXML
    private TableColumn<ItemLancamento, String> columnProduto;
    @FXML
    private TableColumn<ItemLancamento, Double> columnQuantidade;
    @FXML
    private TableColumn<ItemLancamento, Void> columnAcoes;
    @FXML
    private Button buttonLancar;
    @FXML
    private Button buttonCancelar;

    private CategoriaService categoriaService;
    private ItemService itemService;
    private DependenciaService dependenciaService;

    // ObservableList para a TableView
    private ObservableList<ItemLancamento> itensLancamento = FXCollections.observableArrayList();

    public void initialize() {
        categoriaService = new CategoriaService();
        itemService = new ItemService();
        dependenciaService = new DependenciaService();

        // Configuração das colunas da TableView
        columnProduto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getNome()));
        columnQuantidade.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantidade()).asObject());

        // Torna a coluna de quantidade editável
        columnQuantidade.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        columnQuantidade.setOnEditCommit(this::handleEditarQuantidade);

        configurarColunaAcoes();

        tableViewItens.setItems(itensLancamento);

        // Carrega as categorias no ComboBox
        carregarCategorias();

        // Outras inicializações (se necessário)
        comboBoxProduto.setDisable(true); // Desabilita o ComboBox de produtos inicialmente
        buttonAdicionar.setDisable(true); // Desabilita o botão Adicionar inicialmente

        // Adiciona listeners para habilitar/desabilitar botões
        comboBoxProduto.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            buttonAdicionar.setDisable(newVal == null || textFieldQuantidade.getText().trim().isEmpty());
        });

        textFieldQuantidade.textProperty().addListener((obs, oldVal, newVal) -> {
            buttonAdicionar.setDisable(comboBoxProduto.getValue() == null || newVal.trim().isEmpty() || !newVal.matches("\\d+(\\.\\d+)?"));
        });

        // Converter o Item para String no ComboBox de Produto
        comboBoxProduto.setConverter(new StringConverter<Item>() {
            @Override
            public String toString(Item item) {
                return item != null ? item.getNome() : "";
            }

            @Override
            public Item fromString(String string) {
                // Não é necessário para este caso, mas você pode implementar se precisar
                return null;
            }
        });
    }

    @FXML
    private void handleCategoriaSelection(ActionEvent event) {
        Categoria categoriaSelecionada = comboBoxCategoria.getValue();
        if (categoriaSelecionada != null) {
            carregarItensPorCategoria(categoriaSelecionada.getId());
            comboBoxProduto.setDisable(false); // Habilita o ComboBox de produtos
        } else {
            comboBoxProduto.getItems().clear();
            comboBoxProduto.setDisable(true); // Desabilita o ComboBox de produtos se nenhuma categoria for selecionada
        }
    }

    @FXML
    private void handleAdicionarProduto(ActionEvent event) {
        Item itemSelecionado = comboBoxProduto.getValue();
        try {
            double quantidade = Double.parseDouble(textFieldQuantidade.getText());
            if (quantidade <= 0) {
                AlertHelper.showError("Erro", "A quantidade deve ser maior que zero.");
                return;
            }
            if (itensLancamento.stream().anyMatch(il -> il.getItem().getId() == itemSelecionado.getId())) {
                AlertHelper.showError("Erro", "Este item já foi adicionado ao lançamento.");
                return;
            }

            itensLancamento.add(new ItemLancamento(itemSelecionado, quantidade));
            comboBoxProduto.getSelectionModel().clearSelection();
            textFieldQuantidade.clear();
            buttonAdicionar.setDisable(true);
        } catch (NumberFormatException e) {
            AlertHelper.showError("Erro", "Por favor, insira um número válido.");
        }
    }

    // Manipula a edição da quantidade na TableView
    @FXML
    private void handleEditarQuantidade(TableColumn.CellEditEvent<ItemLancamento, Double> event) {
        ItemLancamento itemLancamento = event.getRowValue();
        double novaQuantidade = event.getNewValue();

        if (novaQuantidade <= 0) {
            AlertHelper.showError("Erro", "A quantidade deve ser maior que zero.");
            tableViewItens.refresh(); // Atualiza a TableView para reverter a edição
            return;
        }

        itemLancamento.setQuantidade(novaQuantidade);
    }

    private void configurarColunaAcoes() {
        columnAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button removeButton = new Button("Remover");

            {
                removeButton.getStyleClass().add("btn-delete");
                removeButton.setOnAction(event -> {
                    ItemLancamento item = getTableView().getItems().get(getIndex());
                    itensLancamento.remove(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });
    }

    @FXML
    private void handleLancar(ActionEvent event) {
        if (itensLancamento.isEmpty()) {
            AlertHelper.showWarning("Aviso", "Adicione itens ao lançamento antes de prosseguir.");
            return;
        }

        // Confirmação do usuário
        Optional<ButtonType> result = AlertHelper.showConfirmation("Lançar Itens", "Confirmação", "Tem certeza que deseja lançar os itens no estoque?");
        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Mapa para armazenar ItemId e Quantidade
            Map<Integer, Double> itemQuantidadeMap = new HashMap<>();

            for (ItemLancamento itemLancamento : itensLancamento) {
                itemQuantidadeMap.put(itemLancamento.getItem().getId(), itemLancamento.getQuantidade());
            }

            try {
                // Chama o método do serviço para lançar os itens com tratamento de dependências
                List<String> erros = itemService.lancarItensNoEstoqueComDependencias(itemQuantidadeMap);

                if (erros.isEmpty()) {
                    AlertHelper.showSuccess("Os itens foram lançados com sucesso no estoque.");
                    itensLancamento.clear(); // Limpa a TableView
                } else {
                    String mensagemErros = erros.stream().collect(Collectors.joining("\n"));
                    AlertHelper.showError("Erro", "Os seguintes erros ocorreram:\n" + mensagemErros);
                    //Possivelmente, limpar a tabela e recarregar os itens
                }

            } catch (Exception e) {
                AlertHelper.showError("Erro", "Ocorreu um erro ao lançar os itens no estoque: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        // Limpa os campos e a TableView
        comboBoxCategoria.getSelectionModel().clearSelection();
        comboBoxProduto.getSelectionModel().clearSelection();
        comboBoxProduto.setDisable(true);
        textFieldQuantidade.clear();
        itensLancamento.clear();
        buttonAdicionar.setDisable(true);

        // Fecha a tela (ou redireciona para outra tela, se aplicável)
        // ... (código para fechar a janela) ...
    }

    private void carregarCategorias() {
        try {
            List<Categoria> categorias = categoriaService.listarCategorias();
            comboBoxCategoria.setItems(FXCollections.observableArrayList(categorias));
        } catch (Exception e) {
            AlertHelper.showError("Erro", "Não foi possível carregar as categorias: " + e.getMessage());
        }
    }

    private void carregarItensPorCategoria(int categoriaId) {
        try {
            List<Item> itens = itemService.listarItensPorCategoria(categoriaId);
            comboBoxProduto.setItems(FXCollections.observableArrayList(itens));
        } catch (Exception e) {
            AlertHelper.showError("Erro", "Não foi possível carregar os itens da categoria selecionada: " + e.getMessage());
        }
    }

    // Classe interna para representar um item na TableView
    public static class ItemLancamento {
        private Item item;
        private double quantidade;

        public ItemLancamento(Item item, double quantidade) {
            this.item = item;
            this.quantidade = quantidade;
        }

        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public double getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(double quantidade) {
            this.quantidade = quantidade;
        }
    }
}