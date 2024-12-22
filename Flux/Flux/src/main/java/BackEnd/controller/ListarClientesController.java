package BackEnd.controller;

import BackEnd.model.entity.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import BackEnd.model.service.ClienteService;
import BackEnd.util.AlertHelper;
import BackEnd.util.ConnectionFactory;
import BackEnd.util.DateUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListarClientesController implements Initializable {

    @FXML private TextField pesquisaField;
    @FXML private TableView<Cliente> tabelaFuncionarios;
    @FXML private TableColumn<Cliente, String> colunaId;
    @FXML private TableColumn<Cliente, String> colunaNome;
    @FXML private TableColumn<Cliente, String> colunaCpf;
    @FXML private TableColumn<Cliente, String> colunaFuncao;
    @FXML private TableColumn<Cliente, String> colunaData;
    @FXML private TableColumn<Cliente, Void> colunaAcoes;
    @FXML private Label statusLabel;

    private final ClienteService clienteService;
    private ObservableList<Cliente> clientes;

    public ListarClientesController() {
        this.clienteService = new ClienteService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        configurarPesquisa();
        carregarFuncionarios();
    }

    private void configurarColunas() {
        // Configuração das colunas da tabela
        colunaId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));

        colunaNome.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));

        colunaCpf.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCpf()));

        colunaFuncao.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFuncao()));

        colunaData.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtils.formatarData(data.getValue().getDataAdmissao())));

        configurarColunaAcoes();
    }

    private void configurarColunaAcoes() {
        colunaAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnDeletar = new Button("Deletar");
            private final HBox container = new HBox(5, btnEditar, btnDeletar);

            {
                btnEditar.getStyleClass().add("btn-edit");
                btnDeletar.getStyleClass().add("btn-delete");

                btnEditar.setOnAction(e -> editarFuncionario(getTableRow().getItem()));
                btnDeletar.setOnAction(e -> deletarFuncionario(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void configurarPesquisa() {
        pesquisaField.textProperty().addListener((obs, old, novo) -> {
            if (clientes != null) {
                FilteredList<Cliente> dadosFiltrados = new FilteredList<>(clientes);
                dadosFiltrados.setPredicate(funcionario -> {
                    if (novo == null || novo.isEmpty()) {
                        return true;
                    }
                    String filtroLowerCase = novo.toLowerCase();
                    return funcionario.getNome().toLowerCase().contains(filtroLowerCase) ||
                            funcionario.getFuncao().toLowerCase().contains(filtroLowerCase);
                });
                tabelaFuncionarios.setItems(dadosFiltrados);
                atualizarStatusLabel();
            }
        });
    }

    @FXML
    private void pesquisar() {
        // A pesquisa já é feita pelo listener do TextField
        // Este método existe para responder ao botão de pesquisa
    }

    @FXML
    private void atualizarLista() {
        pesquisaField.clear();
        carregarFuncionarios();
        ConnectionFactory.importarBancoDeDados("BACKUP.2024");
    }

    private void carregarFuncionarios() {
        try {
            clientes = FXCollections.observableArrayList(clienteService.listarTodos());
            tabelaFuncionarios.setItems(clientes);
            atualizarStatusLabel();
        } catch (Exception e) {
            AlertHelper.showError("Erro", "Erro ao carregar funcionários: " + e.getMessage());
        }
    }

    private void editarFuncionario(Cliente cliente) {
        if (cliente != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/EditarFuncionario.fxml"));
                VBox dialogContent = loader.load();

                EditarFuncionarioController controller = loader.getController();
                controller.setFuncionario(cliente);

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.DECORATED);
                dialogStage.setResizable(false);

                Scene scene = new Scene(dialogContent);
                scene.getStylesheets().add(
                        getClass().getResource("/styles/styles.css").toExternalForm());

                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                // Recarrega a lista após fechar o diálogo
                carregarFuncionarios();
            } catch (IOException e) {
                AlertHelper.showError("Erro",
                        "Erro ao abrir formulário de edição: " + e.getMessage());
            }
        }
    }

    private void deletarFuncionario(Cliente cliente) {
        if (cliente != null) {
            try {

                Optional<ButtonType> result = AlertHelper.showConfirmation(
                        "Confirmar Exclusão",
                        "Deseja realmente excluir o funcionário?",
                        String.format("Funcionário: %s%nCódigo: %s%n%n" +
                                        "Esta ação não poderá ser desfeita.",
                                cliente.getNome(), cliente.getId())
                );

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clienteService.excluirFuncionario(cliente.getId());
                    ConnectionFactory.exportarBancoDeDados("BACKUP.2024");
                    carregarFuncionarios();
                    AlertHelper.showSuccess("Funcionário excluído com sucesso!");
                }
            } catch (Exception e) {
                AlertHelper.showError("Erro",
                        "Erro ao excluir funcionário: " + e.getMessage());
            }
        }
    }

    private void atualizarStatusLabel() {
        int total = tabelaFuncionarios.getItems().size();
        statusLabel.setText(String.format("Total de funcionários: %d", total));
    }
}