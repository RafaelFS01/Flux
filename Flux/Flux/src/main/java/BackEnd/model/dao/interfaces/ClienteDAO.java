package BackEnd.model.dao.interfaces;

import BackEnd.model.entity.Cliente;
import java.util.List;

public interface ClienteDAO {
    void criar(Cliente cliente) throws Exception;
    Cliente buscarPorId(String id) throws Exception;
    Cliente buscarPorCPF(String id) throws Exception;
    List<Cliente> listarTodos() throws Exception;
    void atualizar(Cliente cliente) throws Exception;
    void deletar(String id) throws Exception;
    boolean verificarEmprestimosAtivos(String id) throws Exception;
}