-- TABELAS DO MYSQL
create database Flux;
use Flux;

-- Tabelas principais


-- Tabela de Categorias
CREATE TABLE categorias (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(255) NOT NULL UNIQUE,
descricao TEXT,
tipo ENUM('Geral', 'Embalagem Primária', 'Embalagem Secundária',  'Etiqueta') NOT NULL
);

-- Tabela de Itens
CREATE TABLE itens (
id INT PRIMARY KEY,
nome VARCHAR(255) NOT NULL UNIQUE,
descricao TEXT,
preco_venda DECIMAL(10, 2) NOT NULL,
preco_custo DECIMAL(10, 2) NOT NULL,
unidade_medida VARCHAR(50) NOT NULL,
quantidade_estoque INT NOT NULL,
quantidade_minima INT NOT NULL,
quantidade_atual INT NOT NULL DEFAULT 0,
categoria_id INT,
embalagem_id INT,
etiqueta_id INT,
FOREIGN KEY (categoria_id) REFERENCES categorias(id),
FOREIGN KEY (embalagem_id) REFERENCES categorias(id),
FOREIGN KEY (etiqueta_id) REFERENCES categorias(id)
);

CREATE TABLE funcionarios (
    id VARCHAR(200) PRIMARY KEY,
    nome VARCHAR(200),
    funcao VARCHAR(200),
    dt DATE,
    cpf VARCHAR(11)
);

CREATE TABLE usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    nivel_acesso INT NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Índices para otimização de performance
CREATE INDEX idx_equipamentos_status ON itens(status);
CREATE INDEX idx_equipamentos_peso ON itens(peso);
CREATE INDEX idx_equipamentos_largura ON itens(largura);
CREATE INDEX idx_equipamentos_comprimento ON itens(comprimento);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_funcionarios_nome ON funcionarios(nome);

-- Dados iniciais
INSERT INTO usuarios (username, password, nome, email, nivel_acesso) 
VALUES ('admin', '1234', 'Administrador', 'admin@BackEnd.com', 1);
