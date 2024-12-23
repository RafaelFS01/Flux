// Em src/main/java/seuprojeto/model/dao/impl/ItemDAOImpl.java

package BackEnd.model.dao.impl;

import BackEnd.model.dao.interfaces.ItemDAO;
import BackEnd.model.dao.interfaces.CategoriaDAO;
import BackEnd.model.entity.Item;
import BackEnd.model.entity.Categoria;
import BackEnd.util.ConnectionFactory; // Sua classe de conexão

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {

    private final CategoriaDAO categoriaDAO;

    public ItemDAOImpl() {
        this.categoriaDAO = new CategoriaDAOImpl();
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

            // 2. Preparar o SQL para inserir o item
            String sql = "INSERT INTO itens (id, nome, descricao, preco_venda, preco_custo, unidade_medida, " +
                    "quantidade_estoque, quantidade_minima, quantidade_atual, " +
                    "categoria_id " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            // 3. Definir os valores dos parâmetros
            stmt.setString(1, item.getId());
            stmt.setString(2, item.getNome());
            stmt.setString(3, item.getDescricao());
            stmt.setDouble(4, item.getPrecoVenda());
            stmt.setDouble(5, item.getPrecoCusto());
            stmt.setString(6, item.getUnidadeMedida());
            stmt.setInt(7, item.getQuantidadeEstoque());
            stmt.setInt(8, item.getQuantidadeMinima());
            stmt.setInt(9, item.getQuantidadeAtual());

            // Usando os IDs das categorias obtidos anteriormente
            if (categoria != null) {
                stmt.setInt(10, categoria.getId());
            } else {
                stmt.setNull(10, Types.INTEGER);
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
                    FROM itens i
                    LEFT JOIN categorias c ON i.categoria_id = c.id
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
                    FROM itens i
                    LEFT JOIN categorias c ON i.categoria_id = c.id
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
                    FROM itens i
                    LEFT JOIN categorias c ON i.categoria_id = c.id
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
        }
        item.setCategoria(categoria);

        return item;
    }
}