package BackEnd.model.service;

import BackEnd.model.dao.impl.ItemDAOImpl;
import BackEnd.model.dao.interfaces.ItemDAO;
import BackEnd.model.dao.interfaces.ItemPedidoDAO;
import BackEnd.model.dao.interfaces.PedidoDAO;
import BackEnd.model.dao.impl.ItemPedidoDAOImpl;
import BackEnd.model.dao.impl.PedidoDAOImpl;
import BackEnd.model.entity.*;
import BackEnd.util.AlertHelper;
import BackEnd.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoService {

    private PedidoDAO pedidoDAO;
    private ItemPedidoDAO itemPedidoDAO;
    private ItemDAO itemDAO;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAOImpl();
        this.itemPedidoDAO = new ItemPedidoDAOImpl();
        this.itemDAO = new ItemDAOImpl(); // Adicione esta linha para instanciar o ItemDAO
    }

    public void salvarPedido(Pedido pedido) throws Exception {
        validarPedido(pedido);
        pedidoDAO.salvar(pedido);
    }

    public void salvarItens(Pedido pedido) throws Exception {
        // Salvar itens do pedido e atualizar itens
        for (ItemPedido itemPedido : pedido.getItens()) {
            itemPedido.setPedido(pedido);
            itemPedidoDAO.salvar(itemPedido);

            Item item = itemDAO.buscarItemPorId(itemPedido.getItem().getId());
            if (item != null) {
                if (pedido.getTipoVenda() == TipoVenda.NOTA_FISCAL || pedido.getTipoVenda() == TipoVenda.VENDA_NORMAL) {
                    item.setQuantidadeEstoque(item.getQuantidadeEstoque() - itemPedido.getQuantidade());
                }
                if (pedido.getTipoVenda() == TipoVenda.NOTA_FISCAL || pedido.getTipoVenda() == TipoVenda.VENDA_NORMAL || pedido.getTipoVenda() == TipoVenda.PEDIDO) {
                    item.setQuantidadeAtual(item.getQuantidadeAtual() - itemPedido.getQuantidade());
                }
                itemDAO.atualizar(item);
            }
        }
    }

    public Pedido buscarPedidoPorId(int id) throws Exception {
        return pedidoDAO.buscarPorId(id);
    }

    public List<Pedido> listarPedidos() throws Exception {
        return pedidoDAO.listar();
    }

    public void cancelarPedido(int id) throws Exception {
        // Implementar lógica de cancelamento (por exemplo, alterar o status do pedido)
        // A atualização do status deve ser feita no DAO correspondente.
    }

    public void validarPedido(Pedido pedido) throws Exception {
        if (pedido.getCliente() == null) {
            throw new Exception("Cliente não selecionado.");
        }
        if (pedido.getTipoVenda() == null) {
            throw new Exception("Tipo de venda não selecionado.");
        }
        if (pedido.getDataPedido() == null) {
            throw new Exception("Data do pedido não selecionada.");
        }
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new Exception("Nenhum item adicionado ao pedido.");
        }
        for (ItemPedido itemPedido : pedido.getItens()) {
            if (itemPedido.getQuantidade() <= 0) {
                throw new Exception("Quantidade do item '" + itemPedido.getItem().getNome() + "' deve ser maior que zero.");
            }
            if (itemPedido.getPrecoVenda() <= 0){
                throw new Exception("O preço do item '" + itemPedido.getItem().getNome() + "' deve ser maior que zero.");
            }
            // Adicione outras validações necessárias, como verificar se há estoque suficiente, etc.
        }
    }

    public double calcularValorTotal(List<ItemPedido> itensPedido) {
        return itensPedido.stream()
                .mapToDouble(itemPedido -> itemPedido.getQuantidade() * itemPedido.getPrecoVenda())
                .sum();
    }
}