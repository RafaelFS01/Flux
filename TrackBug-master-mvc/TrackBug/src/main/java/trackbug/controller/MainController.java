package trackbug.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import trackbug.model.NivelAcesso;
import trackbug.util.ConnectionFactory;
import trackbug.util.SessionManager;
import trackbug.util.AlertHelper;

public class MainController {

    @FXML private VBox areaPrincipal;
    @FXML private Label labelAdministracao;
    @FXML private Button btnGerenciarUsuarios;
    @FXML private Label usuarioLabel;
    @FXML
    private void initialize() {
        verificarPermissoes();
        usuarioLabel.setText("Usuário: " + SessionManager.getUsuarioLogado().getNome());
    }

    private void verificarPermissoes() {
        boolean isAdmin = SessionManager.getUsuarioLogado().getNivelAcesso() ==
                NivelAcesso.ADMIN.getNivel();
        labelAdministracao.setVisible(isAdmin);
        btnGerenciarUsuarios.setVisible(isAdmin);
    }

    private void carregarFXML(String fxmlPath) {
        try {
            ConnectionFactory.importarBancoDeDados("BACKUP.2024");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            areaPrincipal.getChildren().clear();
            areaPrincipal.getChildren().add(loader.load());
        } catch (Exception e) {
            AlertHelper.showError("Erro ao carregar tela",
                    "Não foi possível carregar a tela solicitada: " + e.getMessage());
        }
    }

    @FXML
    private void telaPrincipal() {
        carregarFXML("/fxml/tela-principal.fxml");
    }

    @FXML
    private void mostrarRegistroEquipamento() {
        carregarFXML("/fxml/CadastroItem.fxml");
    }

    @FXML
    private void mostrarListaEquipamentos() {
        carregarFXML("/fxml/listar-equipamentos.fxml");
    }

    @FXML
    private void mostrarCadastroFuncionario() {
        carregarFXML("/fxml/cadastrar-funcionario.fxml");
    }

    @FXML
    private void mostrarListaFuncionarios() {
        carregarFXML("/fxml/listar-funcionarios.fxml");
    }

    @FXML
    private void mostrarGerenciamentoUsuarios() {
        carregarFXML("/fxml/gerenciar-permissoes.fxml");
    }

    @FXML
    private void logout() {
        try {
            SessionManager.limparSessao();
            // Aqui você pode adicionar a lógica para voltar para a tela de login
        } catch (Exception e) {
            AlertHelper.showError("Erro ao fazer logout", e.getMessage());
        }
    }
}