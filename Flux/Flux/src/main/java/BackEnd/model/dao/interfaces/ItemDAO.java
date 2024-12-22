// Em src/main/java/seuprojeto/model/dao/interfaces/ItemDAO.java

package BackEnd.model.dao.interfaces;

import BackEnd.model.entity.Item;
import java.util.List;

public interface ItemDAO {

    /**
     * Salva um item no banco de dados.
     * Garante que as categorias associadas ao item existam no banco de dados.
     *
     * @param item O item a ser salvo.
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    void salvarItem(Item item) throws Exception;

    /**
     * Verifica se um item com o nome especificado existe no banco de dados.
     *
     * @param nome O nome do item a ser buscado.
     * @return true se o item existir, false caso contrário.
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    boolean buscarItemPorNome(String nome) throws Exception;

    /**
     * Busca um item pelo seu ID.
     *
     * @param id O ID do item a ser buscado.
     * @return O item encontrado, ou null se não for encontrado.
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    Item buscarItemPorId(int id) throws Exception;

    /**
     * Lista todos os itens cadastrados no banco de dados.
     *
     * @return Uma lista de todos os itens.
     * @throws Exception Se ocorrer algum erro durante a operação.
     */
    List<Item> listarItens() throws Exception;
    List<Item> listarItensAbaixoDoMinimo() throws Exception;
    void deletar(String id) throws Exception;
}