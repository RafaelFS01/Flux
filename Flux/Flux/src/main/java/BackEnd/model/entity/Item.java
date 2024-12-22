// Em src/main/java/seuprojeto/model/entity/Item.java

package BackEnd.model.entity;

public class Item {
    private String id;
    private String nome;
    private String descricao;
    private Double precoVenda;
    private Double precoCusto;
    private String unidadeMedida;
    private Integer quantidadeEstoque;
    private Integer quantidadeMinima;
    private Integer quantidadeAtual;
    private Categoria categoria; // Categoria principal
    private Categoria embalagem;  // Categoria de embalagem
    private Categoria etiqueta;   // Categoria de etiqueta

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(Double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public Double getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(Double precoCusto) {
        this.precoCusto = precoCusto;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(Categoria embalagem) {
        this.embalagem = embalagem;
    }

    public Categoria getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(Categoria etiqueta) {
        this.etiqueta = etiqueta;
    }
}