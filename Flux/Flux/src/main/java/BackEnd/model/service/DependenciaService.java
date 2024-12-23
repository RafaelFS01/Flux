package BackEnd.model.service;

import BackEnd.model.dao.impl.DependenciaDAOImpl;
import BackEnd.model.dao.interfaces.DependenciaDAO;
import BackEnd.model.entity.Dependencia;
import java.util.List;

public class DependenciaService {

    private DependenciaDAO dependenciaDAO;

    public DependenciaService() {
        this.dependenciaDAO = new DependenciaDAOImpl();
    }

    public void salvarDependencia(Dependencia dependencia) throws Exception {
        if (dependencia.getIdItemDependente() <= 0) {
            throw new Exception("Dependência inválida: ID de item dependente deve ser maior que zero.");
        }
        if (dependencia.getIdItemNecessario() <= 0) {
            throw new Exception("Dependência inválida: ID de item necessário deve ser maior que zero.");
        }
        if (dependencia.getIdCategoria() <= 0) {
            throw new Exception("Dependência inválida: ID de categoria deve ser maior que zero.");
        }
        if (dependencia.getQuantidade() <= 0) {
            throw new Exception("Dependência inválida: Quantidade deve ser maior que zero.");
        }
        dependenciaDAO.salvarDependencia(dependencia);
    }

    public Dependencia buscarPorId(int id) throws Exception {
        return dependenciaDAO.buscarPorId(id);
    }

    public List<Dependencia> buscarPorIdItemDependente(int idItemDependente) throws Exception {
        return dependenciaDAO.buscarPorIdItemDependente(idItemDependente);
    }

    public void atualizar(Dependencia dependencia) throws Exception {
        if (dependencia.getId() <= 0) {
            throw new Exception("Dependência inválida: ID deve ser maior que zero para atualização.");
        }
        dependenciaDAO.atualizar(dependencia);
    }

    public void excluir(int id) throws Exception {
        if (id <= 0) {
            throw new Exception("ID inválido: ID deve ser maior que zero para exclusão.");
        }
        dependenciaDAO.excluir(id);
    }
}