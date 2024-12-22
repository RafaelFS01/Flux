package BackEnd.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import BackEnd.model.dao.interfaces.CategoriaDAO;
import BackEnd.model.entity.Categoria;
import BackEnd.model.service.CategoriaService;
import BackEnd.util.AlertHelper;

import java.net.URL;
import java.util.ResourceBundle;

public class CadastroCategoriaController implements Initializable {

    @FXML private TextField nomeField;
    @FXML private TextField descricaoField;
    @FXML private TextField tipoField;

    private CategoriaService categoriaService;

    private String tipoCategoria;

    public void setCategoriaService(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    public void setTipoCategoria(String tipo) {
        this.tipoCategoria = tipo;
        tipoField.setText(tipo);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCampos();
    }

    private void configurarCampos() {
        // Transforma em letras maiúsculas
        nomeField.textProperty().addListener((obs, old, novo) -> {
            if (novo != null) {
                nomeField.setText(novo.toUpperCase());
            }
        });
    }

    @FXML
    private void salvarCategoria(ActionEvent event) {
        try {
            Categoria categoria = criarCategoria();
            categoriaService.salvarCategoria(categoria);
            AlertHelper.showSuccess("Categoria salva com sucesso!");
            fecharModal(event);
        } catch (Exception e) {
            AlertHelper.showError("Erro ao salvar categoria", e.getMessage());
        System.out.println(e.getMessage());
        }
    }

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNome(nomeField.getText().trim());
        categoria.setDescricao(descricaoField.getText().trim());
        categoria.setTipo(tipoCategoria);
        return categoria;
    }

    @FXML
    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) nomeField.getScene().getWindow();
        stage.close();
    }

    public void setCategoriaDAO(CategoriaDAO categoriaDAO) {

    }
}