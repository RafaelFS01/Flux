package trackbug.model.service;

import trackbug.model.dao.interfaces.CategoriaDAO;
import trackbug.model.entity.Categoria;
import trackbug.util.ValidationHelper;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public void salvarCategoria(Categoria categoria) throws Exception {
        validarCategoria(categoria);
        categoriaDAO.salvarCategoria(categoria);
    }

    private void validarCategoria(Categoria categoria) throws Exception {
        StringBuilder erros = new StringBuilder();

        if (ValidationHelper.isNullOrEmpty(categoria.getNome())) {
            erros.append("O nome é obrigatório.\n");
        } else if (categoria.getNome().length() < 3 || categoria.getNome().length() > 255) {
            erros.append("O nome deve ter entre 3 e 255 caracteres.\n");
        }

        if (ValidationHelper.isNullOrEmpty(categoria.getDescricao())) {
            erros.append("A descrição é obrigatória.\n");
        } else if (categoria.getDescricao().length() < 3 || categoria.getDescricao().length() > 255) {
            erros.append("A descrição deve ter entre 3 e 255 caracteres.\n");
        }

        if (categoria.getTipo() == null) {
            erros.append("O tipo é obrigatório.\n");
        }

        if (erros.length() > 0) {
            throw new Exception(erros.toString());
        }
    }
}