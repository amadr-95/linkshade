INSERT INTO users (id, name, email, password, role, created_at)
VALUES (1, 'Admin User', 'admin@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ADMIN',
        '2023-01-01 10:00:00'),
       (2, 'Normal User', 'user@example.com', '$2a$10$J5bFGV5zN1BHwxVVT3WUqOfCc8yBZVZ7.jyPJAnBN/SjMcRrLJC2.', 'USER',
        '2023-01-15 14:30:00');

-- Datos de ejemplo para la tabla short_urls
INSERT INTO short_urls (id, shortened_url, original_url, created_by_user, is_private, created_at, expires_at,
                        number_of_clicks)
VALUES ('a1b2c3d4-e5f6-47a8-8b10-c11d12e13f14', 'abc123', 'https://www.google.com', 1, false, '2023-02-01 09:15:00',
        '2024-02-01 09:15:00', 1250),
       ('b2c3d4e5-f6a7-48b9-ac11-d12e13f14a15', 'def456', 'https://www.github.com', 1, true, '2023-02-05 11:20:00',
        '2024-02-05 11:20:00', 843),
       ('c3d4e5f6-a7b8-49c1-ad12-e13f14a15b16', 'ghi789', 'https://www.stackoverflow.com', 2, false,
        '2023-02-10 14:30:00', '2024-02-10 14:30:00', 2156),
       ('d4e5f6a7-b8c9-41d1-ae13-f14a15b16c17', 'jkl012', 'https://www.medium.com', 2, true, '2023-02-15 16:45:00',
        '2024-02-15 16:45:00', 534),
       ('e5f6a7b8-c9d0-42e1-af14-a15b16c17d18', 'mno345', 'https://www.youtube.com', 1, false, '2023-02-20 10:00:00',
        '2024-02-20 10:00:00', 3750),
       ('f6a7b8c9-d0e1-43f1-aa15-b16c17d18e19', 'pqr678', 'https://www.twitter.com', 1, false, '2023-02-25 13:15:00',
        '2024-02-25 13:15:00', 987),
       ('a7b8c9d0-e1f2-44a1-ab16-c17d18e19f20', 'stu901', 'https://www.linkedin.com', 2, false, '2023-03-01 15:30:00',
        '2024-03-01 15:30:00', 1568),
       ('b8c9d0e1-f2a3-45b1-ac17-d18e19f20a21', 'vwx234', 'https://www.netflix.com', 2, true, '2023-03-05 17:45:00',
        '2024-03-05 17:45:00', 321),
       ('c9d0e1f2-a3b4-46c1-ad18-e19f20a21b22', 'yz0123', 'https://www.amazon.com', 1, false, '2023-03-10 09:00:00',
        '2024-03-10 09:00:00', 4250),
       ('d0e1f2a3-b4c5-47d1-ae19-f20a21b22c23', 'abc789', 'https://www.microsoft.com', 2, false, '2023-03-15 11:15:00',
        '2024-03-15 11:15:00', 756);