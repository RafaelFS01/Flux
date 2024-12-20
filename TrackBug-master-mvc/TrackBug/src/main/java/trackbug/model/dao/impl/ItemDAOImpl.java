// Em src/main/java/seuprojeto/model/dao/impl/ItemDAOImpl.java

package trackbug.model.dao.impl;

import trackbug.model.dao.interfaces.ItemDAO;
import trackbug.model.dao.interfaces.CategoriaDAO;
import trackbug.model.entity.Item;
import trackbug.model.entity.Categoria;
import trackbug.util.ConnectionFactory; // Sua classe de conexão

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {

    private final CategoriaDAO categoriaDAO;

    public ItemDAOImpl(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public void salvarItem(Item item) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia a transação

            // 1. Garantir que as categorias existam e obter seus IDs
            Categoria categoria = categoriaDAO.salvarCategoria(item.getCategoria());
            Categoria embalagem = categoriaDAO.salvarCategoria(item.getEmbalagem());
            Categoria etiqueta = categoriaDAO.salvarCategoria(item.getEtiqueta());

            // 2. Preparar o SQL para inserir o item
            String sql = "INSERT INTO itens (nome, descricao, preco_venda, preco_custo, unidade_medida, quantidade_estoque, quantidade_minima, quantidade_atual, categoria_id, embalagem_id, etiqueta_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            // 3. Definir os valores dos parâmetros
            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getDescricao());
            stmt.setDouble(3, item.getPrecoVenda());
            stmt.setDouble(4, item.getPrecoCusto());
            stmt.setString(5, item.getUnidadeMedida());
            stmt.setInt(6, item.getQuantidadeEstoque());
            stmt.setInt(7, item.getQuantidadeMinima());
            stmt.setInt(8, item.getQuantidadeAtual());

            // Usando os IDs das categorias obtidos anteriormente
            if (categoria != null) {
                stmt.setInt(9, categoria.getId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            if (embalagem != null) {
                stmt.setInt(10, embalagem.getId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            if (etiqueta != null) {
                stmt.setInt(11, etiqueta.getId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }

            // 4. Executar a inserção
            stmt.executeUpdate();
            conn.commit(); // Finaliza a transação com sucesso

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz a transação em caso de erro
                } catch (SQLException ex) {
                    throw new Exception("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Erro ao salvar item: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public boolean buscarItemPorNome(String nome) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT 1 FROM itens WHERE nome = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            rs = stmt.executeQuery();

            return rs.next(); // Retorna true se encontrar algum resultado, false caso contrário

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar item por nome: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public Item buscarItemPorId(int id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = """
                    SELECT i.*,
                           c.nome as categoria_nome, c.descricao as categoria_descricao, c.tipo as categoria_tipo,
                           e.nome as embalagem_nome, e.descricao as embalagem_descricao, e.tipo as embalagem_tipo,
                           et.nome as etiqueta_nome, et.descricao as etiqueta_descricao, et.tipo as etiqueta_tipo
                    FROM itens i
                    LEFT JOIN categorias c ON i.categoria_id = c.id
                    LEFT JOIN categorias e ON i.embalagem_id = e.id
                    LEFT JOIN categorias et ON i.etiqueta_id = et.id
                    WHERE i.id = ?
                    """;
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetParaItem(rs);
            } else {
                return null; // Item não encontrado
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar item por ID: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Item> listarItens() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Item> itens = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = """
                    SELECT i.*,
                           c.nome as categoria_nome, c.descricao as categoria_descricao, c.tipo as categoria_tipo,
                           e.nome as embalagem_nome, e.descricao as embalagem_descricao, e.tipo as embalagem_tipo,
                           et.nome as etiqueta_nome, et.descricao as etiqueta_descricao, et.tipo as etiqueta_tipo
                    FROM itens i
                    LEFT JOIN categorias c ON i.categoria_id = c.id
                    LEFT JOIN categorias e ON i.embalagem_id = e.id
                    LEFT JOIN categorias et ON i.etiqueta_id = et.id
                    """;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapearResultSetParaItem(rs));
            }

            return itens;

        } catch (SQLException e) {
            throw new Exception("Erro ao listar itens: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Item> listarItensAbaixoDoMinimo() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Item> itens = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = """
                    SELECT i.*,
                           c.nome as categoria_nome, c.descricao as categoria_descricao, c.tipo as categoria_tipo,
                           e.nome as embalagem_nome, e.descricao as embalagem_descricao, e.tipo as embalagem_tipo,
                           et.nome as etiqueta_nome, et.descricao as etiqueta_descricao, et.tipo as etiqueta_tipo
                    FROM itens i
                    LEFT JOIN categorias c ON i.categoria_id = c.id
                    LEFT JOIN categorias e ON i.embalagem_id = e.id
                    LEFT JOIN categorias et ON i.etiqueta_id = et.id
                    WHERE i.quantidade_atual < i.quantidade_minima
                    ORDER BY i.nome
                    """;
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapearResultSetParaItem(rs));
            }

            return itens;
        } catch (SQLException e) {
            throw new Exception("Erro ao listar itens abaixo do mínimo: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public void deletar(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "DELETE FROM equipamentos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    private Item mapearResultSetParaItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(String.valueOf(rs.getInt("id")));
        item.setNome(rs.getString("nome"));
        item.setDescricao(rs.getString("descricao"));
        item.setPrecoVenda(rs.getDouble("preco_venda"));
        item.setPrecoCusto(rs.getDouble("preco_custo"));
        item.setUnidadeMedida(rs.getString("unidade_medida"));
        item.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        item.setQuantidadeMinima(rs.getInt("quantidade_minima"));
        item.setQuantidadeAtual(rs.getInt("quantidade_atual"));

        // Mapeia as categorias, se existirem
        Categoria categoria = null;
        if (rs.getString("categoria_nome") != null) {
            categoria = new Categoria();
            categoria.setId(rs.getInt("categoria_id"));
            categoria.setNome(rs.getString("categoria_nome"));
            categoria.setDescricao(rs.getString("categoria_descricao"));
            categoria.setTipo(rs.getString("categoria_tipo"));
        }
        item.setCategoria(categoria);

        Categoria embalagem = null;
        if (rs.getString("embalagem_nome") != null) {
            embalagem = new Categoria();
            embalagem.setId(rs.getInt("embalagem_id"));
            embalagem.setNome(rs.getString("embalagem_nome"));
            embalagem.setDescricao(rs.getString("embalagem_descricao"));
            embalagem.setTipo(rs.getString("embalagem_tipo"));
        }
        item.setEmbalagem(embalagem);

        Categoria etiqueta = null;
        if (rs.getString("etiqueta_nome") != null) {
            etiqueta = new Categoria();
            etiqueta.setId(rs.getInt("etiqueta_id"));
            etiqueta.setNome(rs.getString("etiqueta_nome"));
            etiqueta.setDescricao(rs.getString("etiqueta_descricao"));
            etiqueta.setTipo(rs.getString("etiqueta_tipo"));
        }
        item.setEtiqueta(etiqueta);

        return item;
    }
}