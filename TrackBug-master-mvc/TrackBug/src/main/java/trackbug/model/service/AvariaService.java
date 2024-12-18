// src/main/java/trackbug/model/service/AvariaService.java
package trackbug.model.service;

import trackbug.model.dao.interfaces.AvariaDAO;
import trackbug.model.dao.impl.AvariaDAOImpl;
import trackbug.model.entity.Avaria;
import trackbug.model.entity.Equipamento;
import trackbug.model.entity.LogEquipamento;

import java.util.List;

public class AvariaService {
    private final AvariaDAO avariaDAO;
    private final EquipamentoService equipamentoService;
    private final LogEquipamentoService logService;

    public AvariaService() {
        this.avariaDAO = new AvariaDAOImpl();
        this.equipamentoService = new EquipamentoService();
        this.logService = new LogEquipamentoService();
    }

    public void registrarAvaria(Avaria avaria) throws Exception {
        // Validações
        validarAvaria(avaria);

        // Verifica disponibilidade no equipamento
        Equipamento equipamento = equipamentoService.buscarPorId(avaria.getIdEquipamento());
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não encontrado");
        }

        if (avaria.getQuantidade() > equipamento.getQuantidadeAtual()) {
            throw new IllegalArgumentException("Quantidade indisponível no equipamento");
        }

        // Registra a avaria
        avariaDAO.registrar(avaria);

        // Registra no log de equipamentos
        logService.registrarLog(new LogEquipamento(
                avaria.getIdEquipamento(),
                "Avaria registrada",
                "AVARIA",
                String.format("Quantidade: %d, Descrição: %s",
                        avaria.getQuantidade(),
                        avaria.getDescricao())
        ));
    }

    public List<Avaria> listarPorEquipamento(String idEquipamento) throws Exception {
        if (idEquipamento == null || idEquipamento.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        return avariaDAO.listarPorEquipamento(idEquipamento);
    }

    public List<Avaria> listarTodas() throws Exception {
        return avariaDAO.listarTodas();
    }

    private void validarAvaria(Avaria avaria) {
        if (avaria == null) {
            throw new IllegalArgumentException("Avaria não pode ser nula");
        }
        if (avaria.getIdEquipamento() == null || avaria.getIdEquipamento().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do equipamento é obrigatório");
        }
        if (avaria.getQuantidade() == null || avaria.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (avaria.getDescricao() == null || avaria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
    }
}