package BackEnd.model.dao.impl;

import BackEnd.model.dao.interfaces.ClienteDAO;
import BackEnd.model.dao.interfaces.ItemDAO;
import BackEnd.model.dao.interfaces.ItemPedidoDAO;
import BackEnd.model.dao.interfaces.PedidoDAO;
import BackEnd.model.entity.*;
import BackEnd.util.AlertHelper;
import BackEnd.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    private final ClienteDAO clienteDAO;
    private final ItemPedidoDAO itemPedidoDAO;

    private final ItemDAO itemDAO;

    public PedidoDAOImpl() {
        this.clienteDAO = new ClienteDAOImpl();
        this.itemPedidoDAO = new ItemPedidoDAOImpl();
        this.itemDAO = new ItemDAOImpl();
    }

    @Override
    public void salvar(Pedido pedido) throws Exception {
        String sql = "INSERT INTO pedidos (cliente_id, tipo_venda, data_pedido, valor_total, status, observacoes) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        int maxRetries = 1;
        int retryDelay = 1000; // Milissegundos

        for (int retryCount = 0; retryCount < maxRetries; retryCount++) {
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false); // Inicia a transação

                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, pedido.getCliente().getId());
                    stmt.setString(2, pedido.getTipoVenda().name());
                    stmt.setDate(3, java.sql.Date.valueOf(pedido.getDataPedido()));
                    stmt.setDouble(4, pedido.getValorTotal());
                    stmt.setString(5, pedido.getStatus().name());
                    stmt.setString(6, pedido.getObservacoes());
                    stmt.executeUpdate();

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            pedido.setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Falha ao obter o ID do pedido, nenhum ID foi gerado.");
                        }
                    }
                }

                conn.commit(); // Commita a transação
                ConnectionFactory.exportarBancoDeDados("BACKUP.2024");
            } catch (Exception e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                        if (e.getCause() instanceof com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException &&
                                e.getMessage().contains("Lock wait timeout exceeded")) { // Adapte para a exceção específica do seu driver JDBC

                            if (retryCount < maxRetries - 1) {
                                AlertHelper.showWarning("Erro ao salvar pedido", "Ocorreu um erro de timeout. Tentando novamente... (Tentativa " + (retryCount + 2) + " de " + maxRetries + ")");
                                Thread.sleep(retryDelay);
                                retryDelay *= 2; // Aumenta o delay exponencialmente
                                continue; // Tenta novamente
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        AlertHelper.showError("Erro ao fazer rollback", ex.getMessage());
                    }
                }
                throw e;
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AlertHelper.showError("Erro ao fechar conexão", e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public Pedido buscarPorId(int id) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE id = ?";
        Pedido pedido = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedido = new Pedido();
                    pedido.setId(rs.getInt("id"));
                    Cliente cliente = clienteDAO.buscarPorId(rs.getString("cliente_id"));
                    pedido.setCliente(cliente);
                    pedido.setTipoVenda(TipoVenda.valueOf(rs.getString("tipo_venda")));
                    pedido.setDataPedido(rs.getDate("data_pedido").toLocalDate());
                    pedido.setValorTotal(rs.getDouble("valor_total"));
                    pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
                    pedido.setObservacoes(rs.getString("observacoes"));
                    // Buscar itens do pedido (implementar em ItemPedidoDAO)
                    List<ItemPedido> itensPedido = itemPedidoDAO.buscarPorIdPedido(id);
                    pedido.setItens(itensPedido);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar pedido por ID: " + e.getMessage(), e);
        }
        return pedido;
    }

    @Override
    public List<Pedido> listar() throws Exception {
        String sql = "SELECT * FROM pedidos";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                Cliente cliente = clienteDAO.buscarPorId(rs.getString("cliente_id"));
                pedido.setCliente(cliente);
                pedido.setTipoVenda(TipoVenda.valueOf(rs.getString("tipo_venda")));
                pedido.setDataPedido(rs.getDate("data_pedido").toLocalDate());
                pedido.setValorTotal(rs.getDouble("valor_total"));
                pedido.setStatus(StatusPedido.valueOf(rs.getString("status")));
                pedido.setObservacoes(rs.getString("observacoes"));
                // Buscar itens do pedido (implementar em ItemPedidoDAO)
                List<ItemPedido> itensPedido = itemPedidoDAO.buscarPorIdPedido(pedido.getId());
                pedido.setItens(itensPedido);

                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return pedidos;
    }

    @Override
    public void atualizarStatus(int id, StatusPedido status, Connection conn) throws Exception {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar status do pedido: " + e.getMessage(), e);
        }
    }
}