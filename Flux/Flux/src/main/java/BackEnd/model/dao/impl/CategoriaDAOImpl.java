// Em src/main/java/seuprojeto/model/dao/impl/CategoriaDAOImpl.java

package BackEnd.model.dao.impl;

import BackEnd.model.dao.interfaces.CategoriaDAO;
import BackEnd.model.entity.Categoria;
import BackEnd.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    @Override
    public Categoria salvarCategoria(Categoria categoria) throws Exception {
        if (categoria == null || categoria.getNome() == null) {
            return null; // Ou lançar uma exceção, dependendo da sua lógica
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();

            // 1. Verificar se a categoria já existe
            String sql = "SELECT id FROM categorias WHERE nome = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, categoria.getNome());
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Categoria já existe, retornar a categoria existente
                categoria.setId(rs.getInt("id"));
                return categoria;
            } else {
                // Categoria não existe, inserir e retornar a nova categoria
                rs.close();
                stmt.close();

                sql = "INSERT INTO categorias (nome, descricao) VALUES (?, ?)";
                stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS); // Para obter o ID gerado
                stmt.setString(1, categoria.getNome());
                stmt.setString(2, categoria.getDescricao());
                stmt.executeUpdate();

                // Obter o ID gerado
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
                return categoria;
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao salvar categoria: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public Categoria buscarCategoriaPorNome(String nome) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM categorias WHERE nome = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetParaCategoria(rs);
            } else {
                return null; // Categoria não encontrada
            }

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar categoria por nome e tipo: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    public List<Categoria> listarCategorias() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Categoria> categorias = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM categorias";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                categorias.add(mapearResultSetParaCategoria(rs));
            }

            return categorias;

        } catch (SQLException e) {
            throw new Exception("Erro ao buscar categoria por nome e tipo: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private Categoria mapearResultSetParaCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setDescricao(rs.getString("descricao"));
        return categoria;
    }
}