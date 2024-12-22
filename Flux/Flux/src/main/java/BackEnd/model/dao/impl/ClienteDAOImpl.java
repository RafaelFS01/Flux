package BackEnd.model.dao.impl;

import BackEnd.model.dao.interfaces.ClienteDAO;
import BackEnd.model.entity.Cliente;
import BackEnd.util.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void criar(Cliente cliente) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "INSERT INTO funcionarios (id, nome, funcao, dt, cpf) VALUES (?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cliente.getId());
            stmt.setString(2, cliente.getNome());
            stmt.setString(3, cliente.getFuncao());
            stmt.setDate(4, Date.valueOf(cliente.getDataAdmissao()));
            stmt.setString(5, cliente.getCpf());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public Cliente buscarPorId(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }
            return null;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    public Cliente buscarPorCPF(String cpf) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM funcionarios WHERE cpf = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpf);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSet(rs);
            }
            return null;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public List<Cliente> listarTodos() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT * FROM funcionarios ORDER BY nome";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }

            return clientes;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    @Override
    public void atualizar(Cliente cliente) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "UPDATE funcionarios SET nome = ?, funcao = ?, dt = ? WHERE id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getFuncao());
            stmt.setDate(3, Date.valueOf(cliente.getDataAdmissao()));
            stmt.setString(4, cliente.getId());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public void deletar(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Primeiro, atualiza as observações dos empréstimos
            String sqlHistorico = "UPDATE emprestimos SET observacoes = " +
                    "CONCAT(IFNULL(observacoes, ''), ' [Funcionário excluído em: " +
                    java.time.LocalDateTime.now() + "]') " +
                    "WHERE idFuncionario = ?";
            stmt = conn.prepareStatement(sqlHistorico);
            stmt.setString(1, id);
            stmt.executeUpdate();

            // Depois, deleta o funcionário
            String sqlDelete = "DELETE FROM funcionarios WHERE id = ?";
            stmt = conn.prepareStatement(sqlDelete);
            stmt.setString(1, id);
            stmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new Exception("Erro ao realizar rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    @Override
    public boolean verificarEmprestimosAtivos(String id) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.getConnection();
            String sql = "SELECT COUNT(*) FROM emprestimos WHERE idFuncionario = ? AND ativo = true";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }
    }

    private Cliente mapearResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getString("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setFuncao(rs.getString("funcao"));
        cliente.setDataAdmissao(rs.getDate("dt").toLocalDate());
        return cliente;
    }
}