-- TABELAS DO MYSQL
create database Flux;
use Flux;

-- Tabelas principais


-- Tabela de Categorias
CREATE TABLE categorias (
id INT AUTO_INCREMENT PRIMARY KEY,
nome VARCHAR(255) NOT NULL UNIQUE,
descricao TEXT
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
FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE Dependencias (
id INT PRIMARY KEY AUTO_INCREMENT,
id_item_dependente INT NOT NULL,
id_item_necessario INT NOT NULL,
id_categoria INT NOT NULL,
quantidade DECIMAL(10, 2) NOT NULL,
FOREIGN KEY (id_item_dependente) REFERENCES itens(id),
FOREIGN KEY (id_item_necessario) REFERENCES itens(id),
FOREIGN KEY (id_categoria) REFERENCES categorias(id),
CONSTRAINT check_quantidade CHECK (quantidade > 0)
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
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_funcionarios_nome ON funcionarios(nome);

-- Dados iniciais
INSERT INTO usuarios (username, password, nome, email, nivel_acesso)
VALUES ('admin', '1234', 'Administrador', 'admin@BackEnd.com', 1);