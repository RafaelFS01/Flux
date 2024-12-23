package BackEnd.model.dao.impl;

import BackEnd.model.dao.interfaces.DependenciaDAO;
import BackEnd.model.entity.Dependencia;
import BackEnd.util.AlertHelper;
import BackEnd.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DependenciaDAOImpl implements DependenciaDAO {

    @Override
    public void salvarDependencia(Dependencia dependencia) {
        String sql = "INSERT INTO Dependencias (id_item_dependente, id_item_necessario, id_categoria, quantidade) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, dependencia.getIdItemDependente());
            stmt.setInt(2, dependencia.getIdItemNecessario());
            stmt.setInt(3, dependencia.getIdCategoria());
            stmt.setDouble(4, dependencia.getQuantidade());

            stmt.executeUpdate();

        } catch (SQLException e) {
            AlertHelper.showError("ERRO EM SALVAR", e.getMessage());
            e.printStackTrace();
            // Tratar a exceção apropriadamente (lançar uma exceção personalizada, logar o erro, etc.)
        }
    }

    @Override
    public Dependencia buscarPorId(int id) {
        String sql = "SELECT * FROM Dependencias WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaDependencia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Tratar a exceção apropriadamente
        }
        return null;
    }

    @Override
    public List<Dependencia> buscarPorIdItemDependente(int idItemDependente) {
        List<Dependencia> dependencias = new ArrayList<>();
        String sql = "SELECT * FROM Dependencias WHERE id_item_dependente = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, idItemDependente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dependencias.add(mapearResultSetParaDependencia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Tratar a exceção apropriadamente
        }
        return dependencias;
    }

    @Override
    public void atualizar(Dependencia dependencia) {
        String sql = "UPDATE Dependencias SET id_item_dependente = ?, id_item_necessario = ?, id_categoria = ?, quantidade = ? WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, dependencia.getIdItemDependente());
            stmt.setInt(2, dependencia.getIdItemNecessario());
            stmt.setInt(3, dependencia.getIdCategoria());
            stmt.setDouble(4, dependencia.getQuantidade());
            stmt.setInt(5, dependencia.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Tratar a exceção apropriadamente
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM Dependencias WHERE id = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Tratar a exceção apropriadamente
        }
    }

    private Dependencia mapearResultSetParaDependencia(ResultSet rs) throws SQLException {
        Dependencia dependencia = new Dependencia();
        dependencia.setId(rs.getInt("id"));
        dependencia.setIdItemDependente(rs.getInt("id_item_dependente"));
        dependencia.setIdItemNecessario(rs.getInt("id_item_necessario"));
        dependencia.setIdCategoria(rs.getInt("id_categoria"));
        dependencia.setQuantidade(rs.getDouble("quantidade"));
        return dependencia;
    }
}