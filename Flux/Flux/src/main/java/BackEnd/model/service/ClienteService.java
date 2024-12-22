package BackEnd.model.service;

import BackEnd.model.dao.interfaces.ClienteDAO;
import BackEnd.model.dao.impl.ClienteDAOImpl;
import BackEnd.model.entity.Cliente;

import java.time.LocalDate;
import java.util.List;

public class ClienteService {
    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public void cadastrarFuncionario(Cliente cliente) throws Exception {
        validarFuncionario(cliente);

        if (existePorId(cliente.getId())) {
            throw new IllegalArgumentException("Já existe um funcionário com este ID.");
        }

        if (existePorCPF(cliente.getCpf())) {
            throw new IllegalArgumentException("Já existe um funcionário com este CPF.");
        }

        try {
            clienteDAO.criar(cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao cadastrar funcionário: " + e.getMessage());
        }
    }

    public void criar(Cliente cliente) throws Exception {
        validarFuncionario(cliente);

        if (existePorId(cliente.getId())) {
            throw new IllegalArgumentException("Já existe um funcionário com este ID");
        }

        try {
            clienteDAO.criar(cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao criar funcionário: " + e.getMessage());
        }
    }

    public void atualizar(Cliente cliente) throws Exception {
        validarFuncionario(cliente);

        if (!existePorId(cliente.getId())) {
            throw new IllegalArgumentException("Funcionário não encontrado");
        }

        try {
            clienteDAO.atualizar(cliente);
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar funcionário: " + e.getMessage());
        }
    }

    public void excluirFuncionario(String id) throws Exception {
        try {
            if (clienteDAO.verificarEmprestimosAtivos(id)) {
                throw new IllegalStateException("Não é possível excluir funcionário com empréstimos ativos");
            }
            clienteDAO.deletar(id);
        } catch (Exception e) {
            throw new Exception("Erro ao excluir funcionário: " + e.getMessage());
        }
    }

    public Cliente buscarPorId(String id) throws Exception {
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null) {
                throw new IllegalArgumentException("Funcionário não encontrado");
            }
            return cliente;
        } catch (Exception e) {
            throw new Exception("Erro ao buscar funcionário: " + e.getMessage());
        }
    }

    public List<Cliente> listarTodos() throws Exception {
        try {
            return clienteDAO.listarTodos();
        } catch (Exception e) {
            throw new Exception("Erro ao listar funcionários: " + e.getMessage());
        }
    }

    public boolean existePorId(String id) throws Exception {
        try {
            return clienteDAO.buscarPorId(id) != null;
        } catch (Exception e) {
            throw new Exception("Erro ao verificar existência do funcionário: " + e.getMessage());
        }
    }

    public boolean existePorCPF(String cpf) throws Exception {
        try {
            return clienteDAO.buscarPorCPF(cpf) != null;
        } catch (Exception e) {
            throw new Exception("Erro ao verificar existência do funcionário: " + e.getMessage());
        }
    }

    private void validarFuncionario(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Funcionário não pode ser nulo");
        }
        if (cliente.getId() == null || cliente.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do funcionário é obrigatório");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (cliente.getCpf() == null || cliente.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        if (cliente.getFuncao() == null || cliente.getFuncao().trim().isEmpty()) {
            throw new IllegalArgumentException("Função é obrigatória");
        }
        if (cliente.getDataAdmissao() == null) {
            throw new IllegalArgumentException("Data de admissão é obrigatória");
        }
        if (cliente.getDataAdmissao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de admissão não pode ser futura");
        }
    }

    public boolean possuiEmprestimosAtivos(String id) throws Exception {
        try {
            return clienteDAO.verificarEmprestimosAtivos(id);
        } catch (Exception e) {
            throw new Exception("Erro ao verificar empréstimos ativos: " + e.getMessage());
        }
    }
}