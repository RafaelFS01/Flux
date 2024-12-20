package trackbug.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import trackbug.model.dao.interfaces.CategoriaDAO;
import trackbug.model.dao.interfaces.ItemDAO;
import trackbug.model.entity.Categoria;
import trackbug.model.entity.Item;
import trackbug.util.AlertHelper;
import trackbug.util.ConnectionFactory;
import trackbug.util.ValidationHelper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CadastroItemController implements Initializable {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField descricaoField;
    @FXML
    private TextField precoVendaField;
    @FXML
    private TextField precoCustoField;
    @FXML
    private TextField unidadeMedidaField;
    @FXML
    private TextField quantidadeEstoqueField;
    @FXML
    private TextField quantidadeMinimaField;
    @FXML
    private ComboBox<Categoria> categoriaComboBox;
    @FXML
    private ComboBox<Categoria> embalagemComboBox;
    @FXML
    private ComboBox<Categoria> etiquetaComboBox;

    private ItemDAO itemDAO;
    private CategoriaDAO categoriaDAO;

    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public void setCategoriaDAO(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCampos();
        configurarValidacoes();
        carregarCategorias();
        carregarEmbalagens();
        carregarEtiquetas();
    }

    private void configurarCampos() {
        // Configurar formatação e validação dos campos
        nomeField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                nomeField.setText(novo.toUpperCase());
            }
        });

        // Para campos numéricos, você pode adicionar listeners semelhantes ao exemplo para restringir a entrada

        precoVendaField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*(\\.\\d*)?")) {
                precoVendaField.setText(old);
            }
        });

        precoCustoField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*(\\.\\d*)?")) {
                precoCustoField.setText(old);
            }
        });

        quantidadeEstoqueField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*")) {
                quantidadeEstoqueField.setText(novo.replaceAll("[^\\d]", ""));
            }
        });

        quantidadeMinimaField.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*")) {
                quantidadeMinimaField.setText(novo.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void configurarValidacoes() {
        // Validação para campos numéricos decimais
        configurarCampoDecimal(precoVendaField);
        configurarCampoDecimal(precoCustoField);
    }

    private void configurarCampoDecimal(TextField campo) {
        campo.textProperty().addListener((obs, old, novo) -> {
            if (!novo.matches("\\d*(\\.\\d*)?")) {
                campo.setText(old);
            }
        });
    }

    private void carregarCategorias() {
        try {
            List<Categoria> categoriasList = categoriaDAO.buscarCategoriasPorTipo("Geral");
            ObservableList<Categoria> categorias = FXCollections.observableArrayList(categoriasList);
            categoriaComboBox.setItems(categorias);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar categorias", e.getMessage());
        }
    }

    private void carregarEmbalagens() {
        try {
            List<Categoria> embalagensList = categoriaDAO.buscarCategoriasPorTipo("Embalagem");
            ObservableList<Categoria> embalagens = FXCollections.observableArrayList(embalagensList);
            embalagemComboBox.setItems(embalagens);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar embalagens", e.getMessage());
        }
    }

    private void carregarEtiquetas() {
        try {
            List<Categoria> etiquetasList = categoriaDAO.buscarCategoriasPorTipo("Etiqueta");
            ObservableList<Categoria> etiquetas = FXCollections.observableArrayList(etiquetasList);
            etiquetaComboBox.setItems(etiquetas);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar etiquetas", e.getMessage());
        }
    }

    @FXML
    private void salvarItem(ActionEvent event) {
        try {
            if (validarCampos()) {
                Item item = criarItem();
                itemDAO.salvarItem(item);
                ConnectionFactory.exportarBancoDeDados("backup_database.sql");

                AlertHelper.showSuccess("Item salvo com sucesso!");
                limparCampos();
            }
        } catch (Exception e) {
            AlertHelper.showError("Erro ao salvar item", e.getMessage());
        }
    }

    private Item criarItem() {
        Item item = new Item();
        item.setNome(nomeField.getText().trim());
        item.setDescricao(descricaoField.getText().trim());
        item.setPrecoVenda(Double.parseDouble(precoVendaField.getText()));
        item.setPrecoCusto(Double.parseDouble(precoCustoField.getText()));
        item.setUnidadeMedida(unidadeMedidaField.getText().trim());
        item.setQuantidadeEstoque(Integer.parseInt(quantidadeEstoqueField.getText()));
        item.setQuantidadeMinima(Integer.parseInt(quantidadeMinimaField.getText()));
        item.setQuantidadeAtual(Integer.parseInt(quantidadeEstoqueField.getText()));
        item.setCategoria(categoriaComboBox.getValue());
        item.setEmbalagem(embalagemComboBox.getValue());
        item.setEtiqueta(etiquetaComboBox.getValue());
        return item;
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (ValidationHelper.isNullOrEmpty(nomeField.getText())) {
            erros.append("O nome é obrigatório.\n");
        } else if (nomeField.getText().length() < 3 || nomeField.getText().length() > 255){
            erros.append("O nome deve ter entre 3 e 255 caracteres.\n");
        }

        if (ValidationHelper.isNullOrEmpty(descricaoField.getText())) {
            erros.append("A descrição é obrigatória.\n");
        } else if (descricaoField.getText().length() < 3 || descricaoField.getText().length() > 255){
            erros.append("A descrição deve ter entre 3 e 255 caracteres.\n");
        }

        if (ValidationHelper.isNullOrEmpty(precoVendaField.getText())) {
            erros.append("O preço de venda é obrigatório.\n");
        }

        if (ValidationHelper.isNullOrEmpty(precoCustoField.getText())) {
            erros.append("O preço de custo é obrigatório.\n");
        }

        if (ValidationHelper.isNullOrEmpty(unidadeMedidaField.getText())) {
            erros.append("A unidade de medida é obrigatória.\n");
        } else if (unidadeMedidaField.getText().length() < 2 || unidadeMedidaField.getText().length() > 5){
            erros.append("A unidade de medida deve ter entre 2 e 5 caracteres.\n");
        }

        if (ValidationHelper.isNullOrEmpty(quantidadeEstoqueField.getText())) {
            erros.append("A quantidade de estoque é obrigatória.\n");
        }

        if (ValidationHelper.isNullOrEmpty(quantidadeMinimaField.getText())) {
            erros.append("A quantidade mínima é obrigatória.\n");
        }

        if (categoriaComboBox.getValue() == null) {
            erros.append("Selecione uma categoria.\n");
        }

        if (embalagemComboBox.getValue() == null) {
            erros.append("Selecione uma embalagem.\n");
        }

        if (etiquetaComboBox.getValue() == null) {
            erros.append("Selecione uma etiqueta.\n");
        }

        if (erros.length() > 0) {
            AlertHelper.showWarning("Campos inválidos", erros.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void limparCampos() {
        nomeField.clear();
        descricaoField.clear();
        precoVendaField.clear();
        precoCustoField.clear();
        unidadeMedidaField.clear();
        quantidadeEstoqueField.clear();
        quantidadeMinimaField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        embalagemComboBox.getSelectionModel().clearSelection();
        etiquetaComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void abrirCadastroCategoria(ActionEvent event) {
        abrirModalCategoria("Geral");
    }

    @FXML
    private void abrirCadastroEmbalagem(ActionEvent event) {
        abrirModalCategoria("Embalagem");
    }

    @FXML
    private void abrirCadastroEtiqueta(ActionEvent event) {
        abrirModalCategoria("Etiqueta");
    }

    private void abrirModalCategoria(String tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/seuprojeto/view/CadastroCategoria.fxml"));
            Parent root = loader.load();

            CadastroCategoriaController controller = loader.getController();
            controller.setCategoriaDAO(categoriaDAO);
            controller.setTipoCategoria(tipo);

            Stage stage = new Stage();
            stage.setTitle("Cadastrar Categoria");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Atualiza as ComboBoxes após fechar a janela
            switch (tipo) {
                case "Geral":
                    carregarCategorias();
                    break;
                case "Embalagem":
                    carregarEmbalagens();
                    break;
                case "Etiqueta":
                    carregarEtiquetas();
                    break;
            }

        } catch (IOException e) {
            AlertHelper.showError("Erro ao abrir a janela de cadastro de categoria", e.getMessage());
        }
    }
}