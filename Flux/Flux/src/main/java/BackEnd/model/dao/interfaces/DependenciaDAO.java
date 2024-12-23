package BackEnd.model.dao.interfaces;

import BackEnd.model.entity.Dependencia;
import java.util.List;

public interface DependenciaDAO {
    void salvarDependencia(Dependencia dependencia);
    Dependencia buscarPorId(int id);
    List<Dependencia> buscarPorIdItemDependente(int idItemDependente);
    void atualizar(Dependencia dependencia);
    void excluir(int id);
}