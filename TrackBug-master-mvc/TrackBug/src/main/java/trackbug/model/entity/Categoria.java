// Em src/main/java/seuprojeto/model/entity/Categoria.java

package trackbug.model.entity;

public class Categoria {
    private Integer id;
    private String nome;
    private String descricao;
    private String tipo; // "Geral", "Embalagem", "Etiqueta"

    // Construtor vazio
    public Categoria() {
    }

    // Construtor com todos os campos, exceto id
    public Categoria(String nome, String descricao, String tipo) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}