INSERT INTO endereco (cep, estado, cidade, bairro, rua, numero)
VALUES  ('12345-678', 'BA', 'Salvador', 'Centro', 'Rua Principal', '123'),
        ('11111-111', 'BA', 'Salvador', 'Pituba', 'Rua dois', '111'),
        ('22222-222', 'BA', 'Camaçari', 'Centro', 'Rua Principal', '222'),
        ('33333-333', 'BA', 'Lauro de Freitas', 'Centro', 'Rua Principal', '333');

INSERT INTO cliente (cpf, nome, email, endereco_id)
VALUES  ('12345678901', 'João Silva', 'joaosilva@mail.com', 1),
        ('11122233344', 'Pedro Pascal', 'pedropascal@mail.com', 2),
        ('22233344455', 'Paulo Peixoto', 'paulopeixoto@mail.com', 3),
        ('33344455566', 'João Maria', 'joaomaria@mail.com', 4);
