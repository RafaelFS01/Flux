package BackEnd.controller;

import BackEnd.model.entity.Dependencia;
import BackEnd.model.entity.Item;
import BackEnd.model.service.DependenciaService;
import BackEnd.model.service.ItemService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import BackEnd.model.entity.Categoria;
import BackEnd.model.service.CategoriaService;
import BackEnd.util.AlertHelper;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AdicionarDependenciaController implements Initializable {

    @FXML private ComboBox<Categoria> categoriaComboBox;
    @FXML private ComboBox<Item> dependenciaComboBox;
    @FXML private TextField quantidadeField;

    private final DependenciaService dependenciaService;
    private final CategoriaService categoriaService;
    private final ItemService itemService;

    private int idItemDependente;
    private Consumer<Item> onDependenciaSalva;

    public void setOnDependenciaSalva(Consumer<Item> onDependenciaSalva) {
        this.onDependenciaSalva = onDependenciaSalva;
    }

    // Método para definir o ID do item dependente
    public void setIdItemDependente(int idItemDependente) {
        this.idItemDependente = idItemDependente;
    }

    public AdicionarDependenciaController() {
        this.dependenciaService = new DependenciaService();
        this.categoriaService = new CategoriaService();
        this.itemService = new ItemService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            configurarCategoriaComboBox();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        configurarDependenciaComboBox();
        configurarQuantidadeField();
    }

    private void configurarCategoriaComboBox() throws Exception {
        List<Categoria> categorias = categoriaService.listarCategorias();
        categoriaComboBox.getItems().addAll(categorias);

        // Configurar o StringConverter para exibir o nome da categoria no ComboBox
        categoriaComboBox.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getNome() : "";
            }

            @Override
            public Categoria fromString(String string) {
                return null; // Não é necessário para este caso
            }
        });

        // Listener para carregar as dependências quando uma categoria for selecionada
        categoriaComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    carregarDependencias(newVal.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void configurarDependenciaComboBox() {
        // Configurar o StringConverter para exibir o nome do item no ComboBox
        dependenciaComboBox.setConverter(new StringConverter<Item>() {
            @Override
            public String toString(Item item) {
                return item != null ? item.getNome() : "";
            }

            @Override
            public Item fromString(String string) {
                return null; // Não é necessário para este caso
            }
        });
    }

    private void carregarDependencias(int idCategoria) throws Exception {
        List<Item> itens = itemService.listarItensPorCategoria(idCategoria);
        dependenciaComboBox.getItems().setAll(itens);
    }

    private void configurarQuantidadeField() {
        // Adicionar um listener para validar a entrada do campo quantidade (opcional)
        quantidadeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) { // Aceita números inteiros ou decimais
                quantidadeField.setText(oldValue);
            }
        });
    }

    @FXML
    private void salvarDependencia(ActionEvent event) {
        try {
            // Validação dos campos
            if (categoriaComboBox.getValue() == null || dependenciaComboBox.getValue() == null || quantidadeField.getText().isEmpty()) {
                AlertHelper.showError("Erro ao salvar", "Preencha todos os campos obrigatórios.");
                return;
            }

            // Obter os valores dos campos
            Categoria categoriaSelecionada = categoriaComboBox.getValue();
            Item itemSelecionado = dependenciaComboBox.getValue();
            double quantidade = Double.parseDouble(quantidadeField.getText());

            // Criar o objeto Dependencia
            Dependencia dependencia = new Dependencia();
            dependencia.setIdItemDependente(idItemDependente);
            dependencia.setIdItemNecessario(itemSelecionado.getId());
            dependencia.setIdCategoria(categoriaSelecionada.getId());
            dependencia.setQuantidade(quantidade);

            // Salvar a dependência usando o serviço
            dependenciaService.salvarDependencia(dependencia);

            // Executar a ação especificada pelo CadastrarItemController
            if (onDependenciaSalva != null) {
                onDependenciaSalva.accept(itemSelecionado);
            }

            // Feedback de sucesso
            AlertHelper.showSuccess("Dependência salva com sucesso!");

            // Limpar os campos (opcional)
            limparCampos();

            // Fechar a janela
            fecharModal(event);

        } catch (NumberFormatException e) {
            AlertHelper.showError("Erro ao salvar", "Quantidade inválida.");
        } catch (Exception e) {
            AlertHelper.showError("Erro ao salvar", "Ocorreu um erro ao salvar a dependência." + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        categoriaComboBox.getSelectionModel().clearSelection();
        dependenciaComboBox.getItems().clear();
        quantidadeField.clear();
    }

    @FXML
    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) quantidadeField.getScene().getWindow();
        stage.close();
    }
}