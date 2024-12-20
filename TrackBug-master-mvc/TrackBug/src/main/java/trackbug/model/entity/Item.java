// Em src/main/java/seuprojeto/model/entity/Item.java

package trackbug.model.entity;

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

    // Construtor vazio
    public Item() {
    }

    // Construtor com todos os campos, exceto id
    public Item(String nome, String descricao, Double precoVenda, Double precoCusto,
                String unidadeMedida, Integer quantidadeEstoque, Integer quantidadeMinima, Integer quantidadeAtual,
                Categoria categoria, Categoria embalagem, Categoria etiqueta) {
        this.nome = nome;
        this.descricao = descricao;
        this.precoVenda = precoVenda;
        this.precoCusto = precoCusto;
        this.unidadeMedida = unidadeMedida;
        this.quantidadeEstoque = quantidadeEstoque;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeAtual = quantidadeAtual;
        this.categoria = categoria;
        this.embalagem = embalagem;
        this.etiqueta = etiqueta;
    }

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

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", precoVenda=" + precoVenda +
                ", precoCusto=" + precoCusto +
                ", unidadeMedida='" + unidadeMedida + '\'' +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", quantidadeMinima=" + quantidadeMinima +
                ", quantidadeAtual=" + quantidadeAtual +
                ", categoria=" + categoria +
                ", embalagem=" + embalagem +
                ", etiqueta=" + etiqueta +
                '}';
    }
}