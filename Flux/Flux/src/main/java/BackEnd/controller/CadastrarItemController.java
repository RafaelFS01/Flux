package BackEnd.controller;

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
import BackEnd.Main;
import BackEnd.model.entity.Categoria;
import BackEnd.model.entity.Item;
import BackEnd.model.service.CategoriaService;
import BackEnd.model.service.ItemService;
import BackEnd.util.AlertHelper;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CadastrarItemController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField nomeField;
    @FXML private TextField descricaoField;
    @FXML private TextField precoVendaField;
    @FXML private TextField precoCustoField;
    @FXML private TextField unidadeMedidaField;
    @FXML private TextField quantidadeEstoqueField;
    @FXML private TextField quantidadeMinimaField;
    @FXML private ComboBox<Categoria> categoriaComboBox;

    private final ItemService itemService;
    private final CategoriaService categoriaService;

    public CadastrarItemController() {
        this.itemService = new ItemService();
        this.categoriaService = new CategoriaService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCampos();
        carregarCategorias();
    }

    private void configurarCampos() {
        // Configurar formatação e validação dos campos

        idField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                idField.setText(novo.toUpperCase());
            }
        });

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

    private void carregarCategorias() {
        try {
            List<Categoria> categoriasList = categoriaService.listarCategorias();
            ObservableList<Categoria> categorias = FXCollections.observableArrayList(categoriasList);
            categoriaComboBox.setItems(categorias);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar categorias", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void salvarItem(ActionEvent event) {
        try {
            Item item = criarItem();
            itemService.salvarItem(item);
            AlertHelper.showSuccess("Item salvo com sucesso!");
            limparCampos();
        } catch (Exception e) {
            AlertHelper.showError("Erro ao salvar item", e.getMessage());
        }
    }

    private Item criarItem() {
        Item item = new Item();
        item.setId(idField.getText().trim());
        item.setNome(nomeField.getText().trim());
        item.setDescricao(descricaoField.getText().trim());
        item.setPrecoVenda(Double.parseDouble(precoVendaField.getText()));
        item.setPrecoCusto(Double.parseDouble(precoCustoField.getText()));
        item.setUnidadeMedida(unidadeMedidaField.getText().trim());
        item.setQuantidadeEstoque(Integer.parseInt(quantidadeEstoqueField.getText()));
        item.setQuantidadeMinima(Integer.parseInt(quantidadeMinimaField.getText()));
        item.setQuantidadeAtual(Integer.parseInt(quantidadeEstoqueField.getText()));
        item.setCategoria(categoriaComboBox.getValue());
        return item;
    }

    @FXML
    private void limparCampos() {
        idField.clear();
        nomeField.clear();
        descricaoField.clear();
        precoVendaField.clear();
        precoCustoField.clear();
        unidadeMedidaField.clear();
        quantidadeEstoqueField.clear();
        quantidadeMinimaField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void abrirCadastroCategoria(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CadastroCategoria.fxml"));
            Parent root = loader.load();

            CadastrarCategoriaController controller = loader.getController();
            controller.setCategoriaService(categoriaService);

            Stage stage = new Stage();
            stage.setTitle("Cadastrar Categoria");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon.png")));
            stage.showAndWait();
            carregarCategorias();

        } catch (IOException e) {
            AlertHelper.showError("Erro ao abrir a janela de cadastro de categoria", e.getMessage());
        }
    }
}