// Em src/main/java/seuprojeto/model/dao/interfaces/CategoriaDAO.java

package BackEnd.model.dao.interfaces;

import BackEnd.model.entity.Categoria;
import java.util.List;

public interface CategoriaDAO {

    /**
     * Salva uma categoria no banco de dados.
     * Se a categoria já existir (mesmo nome e tipo), retorna a categoria existente.
     * Se a categoria não existir, insere uma nova e retorna a categoria inserida.
     *
     * @param categoria A categoria a ser salva.
     * @return A categoria salva (nova ou existente).
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    Categoria salvarCategoria(Categoria categoria) throws Exception;

    /**
     * Busca uma categoria pelo nome e tipo.
     *
     * @param nome O nome da categoria.
     * @param tipo O tipo da categoria ("Geral", "Embalagem" ou "Etiqueta").
     * @return A categoria encontrada, ou null se não for encontrada.
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    Categoria buscarCategoriaPorNomeETipo(String nome, String tipo) throws Exception;

    /**
     * Busca todas as categorias de um determinado tipo.
     *
     * @param tipo O tipo das categorias a serem buscadas ("Geral", "Embalagem" ou "Etiqueta").
     * @return Uma lista de categorias do tipo especificado.
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    List<Categoria> buscarCategoriasPorTipo(String tipo) throws Exception;
}